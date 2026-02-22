package ru.resodostudios.cashsense.core.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.CartesianLayerDimensions
import com.patrykandpatrick.vico.compose.cartesian.layer.CartesianLayerMargins
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.CartesianMarkerVisibilityListener
import com.patrykandpatrick.vico.compose.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberFadingEdges
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.Insets
import com.patrykandpatrick.vico.compose.common.LayeredComponent
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.component.ShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month
import kotlinx.datetime.toJavaDayOfWeek
import kotlinx.datetime.toJavaMonth
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.locales.R
import ru.resodostudios.cashsense.core.model.data.DateType.ALL
import ru.resodostudios.cashsense.core.model.data.DateType.MONTH
import ru.resodostudios.cashsense.core.model.data.DateType.WEEK
import ru.resodostudios.cashsense.core.model.data.DateType.YEAR
import ru.resodostudios.cashsense.core.model.data.FinanceType
import ru.resodostudios.cashsense.core.model.data.TransactionFilter
import ru.resodostudios.cashsense.core.ui.util.getCurrentZonedDateTime
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import java.math.BigDecimal
import java.time.format.TextStyle
import java.util.Currency
import java.util.Locale

@Composable
fun FinanceGraph(
    transactionFilter: TransactionFilter,
    modelProducer: CartesianChartModelProducer,
    currency: Currency,
    modifier: Modifier = Modifier,
    locale: Locale = Locale.getDefault(),
) {
    val scrollState = rememberVicoScrollState()
    val zoomState = rememberVicoZoomState(initialZoom = Zoom.max(Zoom.Content, Zoom.Content))

    val xDateFormatter = CartesianValueFormatter { _, x, _ ->
        val textStyle = TextStyle.NARROW_STANDALONE
        when (transactionFilter.dateType) {
            YEAR -> Month(x.toInt().coerceIn(1, 12)).toJavaMonth().getDisplayName(textStyle, locale)
            MONTH -> x.toInt().toString()
            ALL, WEEK -> DayOfWeek(x.toInt().coerceIn(1, 7)).toJavaDayOfWeek()
                .getDisplayName(textStyle, locale)
        }
    }

    val hapticFeedback = LocalHapticFeedback.current
    val markerVisibilityListener = remember {
        object : CartesianMarkerVisibilityListener {

            private var xTarget = -1

            override fun onUpdated(marker: CartesianMarker, targets: List<CartesianMarker.Target>) {
                val target = targets.firstOrNull() as? LineCartesianLayerMarkerTarget ?: return
                val markerIndex = target.points.last().entry.x.toInt()
                if (markerIndex != xTarget) {
                    xTarget = markerIndex
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                }
            }

            override fun onShown(marker: CartesianMarker, targets: List<CartesianMarker.Target>) {
                val target = targets.firstOrNull() as? LineCartesianLayerMarkerTarget ?: return
                val markerIndex = target.points.last().entry.x.toInt()
                xTarget = markerIndex
                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
            }

            override fun onHidden(marker: CartesianMarker) {
                xTarget = -1
            }
        }
    }

    ProvideVicoTheme(rememberM3VicoTheme()) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    lineProvider = LineCartesianLayer.LineProvider.series(
                        vicoTheme.lineCartesianLayerColors.map { color ->
                            LineCartesianLayer.rememberLine(
                                pointConnector = LineCartesianLayer.PointConnector.cubic(),
                                areaFill = LineCartesianLayer.AreaFill.single(
                                    Fill(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                color.copy(alpha = 0.15f),
                                                Color.Transparent,
                                            ),
                                        )
                                    )
                                ),
                            )
                        }
                    ),
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = xDateFormatter,
                    guideline = null,
                    line = null,
                ),
                marker = rememberMarker(
                    DefaultCartesianMarker.ValueFormatter.default(
                        prefix = currency.symbol,
                    )
                ),
                markerVisibilityListener = markerVisibilityListener,
                fadingEdges = rememberFadingEdges(),
            ),
            modelProducer = modelProducer,
            scrollState = scrollState,
            zoomState = zoomState,
            modifier = modifier,
            placeholder = { FinanceGraphPlaceholder() },
        )
    }
}

@Composable
internal fun FinanceGraphPlaceholder() {
    Box(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.not_enough_data),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun rememberMarker(
    valueFormatter: DefaultCartesianMarker.ValueFormatter = DefaultCartesianMarker.ValueFormatter.default(),
    showIndicator: Boolean = true,
): CartesianMarker {
    val labelBackground = rememberShapeComponent(
        fill = Fill(MaterialTheme.colorScheme.surfaceContainer),
        shape = CircleShape,
        shadows = listOf(
            Shadow(
                radius = 4.dp,
                color = MaterialTheme.colorScheme.inverseSurface.copy(0.35f),
            ),
        ),
    )
    val label = rememberTextComponent(
        style = MaterialTheme.typography.bodyMedium,
        padding = Insets(8.dp, 4.dp),
        background = labelBackground,
    )

    val indicatorRearShape = MaterialShapes.Cookie7Sided.toShape()
    val indicatorCenterShape = MaterialShapes.Clover4Leaf.toShape()

    val indicatorFrontComponent = rememberShapeComponent(
        fill = Fill(MaterialTheme.colorScheme.surface),
        shape = CircleShape,
    )

    val guideline = rememberAxisGuidelineComponent()

    return remember(label, valueFormatter, showIndicator, guideline) {
        object :
            DefaultCartesianMarker(
                label = label,
                valueFormatter = valueFormatter,
                indicator = if (showIndicator) {
                    { color ->
                        LayeredComponent(
                            back = ShapeComponent(
                                fill = Fill(color.copy(alpha = 0.38f)),
                                shape = indicatorRearShape,
                            ),
                            front = LayeredComponent(
                                back = ShapeComponent(
                                    fill = Fill(color),
                                    shape = indicatorCenterShape,
                                    shadows = listOf(Shadow(radius = 12.dp, color = color)),
                                ),
                                front = indicatorFrontComponent,
                                padding = Insets(5.dp),
                            ),
                            padding = Insets(10.dp),
                        )
                    }
                } else {
                    null
                },
                indicatorSize = 36.dp,
                guideline = guideline,
            ) {

            override fun updateLayerMargins(
                context: CartesianMeasuringContext,
                layerMargins: CartesianLayerMargins,
                layerDimensions: CartesianLayerDimensions,
                model: CartesianChartModel,
            ) {
                with(context) {
                    val baseShadowMarginDp = 1.4f * 4f
                    var topMargin = (baseShadowMarginDp - 2f)
                    var bottomMargin = (baseShadowMarginDp + 2f)
                    when (labelPosition) {
                        LabelPosition.Top,
                        LabelPosition.AbovePoint,
                            -> topMargin += label.getHeight(context)

                        LabelPosition.Bottom -> bottomMargin += label.getHeight(context)
                        else -> {}
                    }
                    layerMargins.ensureValuesAtLeast(top = topMargin, bottom = bottomMargin)
                }
            }
        }
    }
}

@Preview
@Composable
fun FinanceGraphPreview() {
    CsTheme {
        Surface {
            val graphData = mapOf(
                1 to BigDecimal(100),
                2 to BigDecimal(200),
                3 to BigDecimal(300),
            )
            val modelProducer = remember { CartesianChartModelProducer() }
            runBlocking {
                modelProducer.runTransaction {
                    lineSeries { series(graphData.keys, graphData.values) }
                }
            }
            FinanceGraph(
                transactionFilter = TransactionFilter(
                    dateType = MONTH,
                    financeType = FinanceType.EXPENSES,
                    selectedDate = getCurrentZonedDateTime().date,
                    selectedCategories = emptySet(),
                ),
                currency = getUsdCurrency(),
                modelProducer = modelProducer,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}