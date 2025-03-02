package ru.resodostudios.cashsense.core.ui.component

import android.text.Layout
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberFadingEdges
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shadow
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.insets
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.compose.common.shape.markerCorneredShape
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.CartesianLayerDimensions
import com.patrykandpatrick.vico.core.cartesian.layer.CartesianLayerMargins
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarkerVisibilityListener
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.LayeredComponent
import com.patrykandpatrick.vico.core.common.component.Shadow
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.locales.R
import ru.resodostudios.cashsense.core.model.data.DateType.ALL
import ru.resodostudios.cashsense.core.model.data.DateType.MONTH
import ru.resodostudios.cashsense.core.model.data.DateType.WEEK
import ru.resodostudios.cashsense.core.model.data.DateType.YEAR
import ru.resodostudios.cashsense.core.model.data.FinanceType
import ru.resodostudios.cashsense.core.model.data.TransactionFilter
import ru.resodostudios.cashsense.core.ui.util.getCurrentZonedDateTime
import ru.resodostudios.cashsense.core.ui.util.getDecimalFormat
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
            YEAR -> Month(x.toInt().coerceIn(1, 12)).getDisplayName(textStyle, locale)
            MONTH -> x.toInt().toString()
            ALL, WEEK -> DayOfWeek(x.toInt().coerceIn(1, 7)).getDisplayName(textStyle, locale)
        }
    }

    var xTarget by remember { mutableIntStateOf(0) }
    val hapticFeedback = LocalHapticFeedback.current
    val markerVisibilityListener = remember {
        object : CartesianMarkerVisibilityListener {

            override fun onUpdated(marker: CartesianMarker, targets: List<CartesianMarker.Target>) {
                super.onUpdated(marker, targets)
                val target = targets.first() as LineCartesianLayerMarkerTarget
                val markerIndex = target.points.last().entry.x.toInt()

                if (markerIndex != xTarget) {
                    xTarget = 0
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                }
            }

            override fun onShown(marker: CartesianMarker, targets: List<CartesianMarker.Target>) {
                val target = targets.first() as LineCartesianLayerMarkerTarget
                xTarget = target.points.last().entry.x.toInt()
                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
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
                                    fill(
                                        ShaderProvider.verticalGradient(
                                            arrayOf(color.copy(alpha = 0.15f), Color.Transparent),
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
                        getDecimalFormat(currency)
                    )
                ),
                markerVisibilityListener = markerVisibilityListener,
                fadingEdges = rememberFadingEdges(),
            ),
            modelProducer = modelProducer,
            scrollState = scrollState,
            zoomState = zoomState,
            modifier = modifier,
            placeholder = {
                Text(
                    text = stringResource(R.string.not_enough_data),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        )
    }
}

@Composable
private fun rememberMarker(
    valueFormatter: DefaultCartesianMarker.ValueFormatter = DefaultCartesianMarker.ValueFormatter.default(),
    showIndicator: Boolean = true,
): CartesianMarker {
    val labelBackground = rememberShapeComponent(
        fill = fill(MaterialTheme.colorScheme.surfaceContainer),
        shape = markerCorneredShape(CorneredShape.Corner.Rounded),
        shadow = shadow(radius = 4.dp, y = 2.dp),
    )
    val label = rememberTextComponent(
        textSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface,
        textAlignment = Layout.Alignment.ALIGN_CENTER,
        padding = insets(8.dp, 4.dp),
        background = labelBackground,
    )
    val indicatorFrontComponent =
        rememberShapeComponent(fill(MaterialTheme.colorScheme.surface), CorneredShape.Pill)
    val indicatorCenterComponent = rememberShapeComponent(shape = CorneredShape.Pill)
    val indicatorRearComponent = rememberShapeComponent(shape = CorneredShape.Pill)
    val indicator = LayeredComponent(
        back = indicatorRearComponent,
        front = LayeredComponent(
            back = indicatorCenterComponent,
            front = indicatorFrontComponent,
            padding = insets(5.dp),
        ),
        padding = insets(10.dp),
    )
    val guideline = rememberAxisGuidelineComponent()
    return remember(label, valueFormatter, indicator, showIndicator, guideline) {
        object :
            DefaultCartesianMarker(
                label = label,
                valueFormatter = valueFormatter,
                indicator = if (showIndicator) {
                    { color ->
                        LayeredComponent(
                            back = ShapeComponent(
                                Fill(ColorUtils.setAlphaComponent(color, 38)),
                                CorneredShape.Pill,
                            ),
                            front = LayeredComponent(
                                back = ShapeComponent(
                                    fill = Fill(color),
                                    shape = CorneredShape.Pill,
                                    shadow = Shadow(radiusDp = 12f, color = color),
                                ),
                                front = indicatorFrontComponent,
                                padding = insets(5.dp),
                            ),
                            padding = insets(10.dp),
                        )
                    }
                } else {
                    null
                },
                indicatorSizeDp = 36f,
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
                    var topMargin = (baseShadowMarginDp - 2f).pixels
                    var bottomMargin = (baseShadowMarginDp + 2f).pixels
                    when (labelPosition) {
                        LabelPosition.Top,
                        LabelPosition.AbovePoint -> topMargin += label.getHeight(context) + tickSizeDp.pixels

                        LabelPosition.Bottom -> bottomMargin += label.getHeight(context) + tickSizeDp.pixels
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