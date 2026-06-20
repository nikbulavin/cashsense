package ru.resodostudios.cashsense.core.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import ru.resodostudios.cashsense.core.model.Category

/**
 * This [PreviewParameterProvider](https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameterProvider)
 * provides list of [Category] for Composable previews.
 */
class CategoryPreviewParameterProvider : PreviewParameterProvider<List<Category>> {

    override val values: Sequence<List<Category>>
        get() = sequenceOf(
            listOf(
                Category(
                    id = "1",
                    title = "Savings",
                    iconId = 1,
                ),
                Category(
                    id = "2",
                    title = "Apparel",
                    iconId = 2,
                ),
                Category(
                    id = "3",
                    title = "Furniture",
                    iconId = 3,
                ),
                Category(
                    id = "4",
                    title = "Gym",
                    iconId = 4,
                ),
                Category(
                    id = "5",
                    title = "Fastfood",
                    iconId = 5,
                ),
                Category(
                    id = "6",
                    title = "Transportation",
                    iconId = 6,
                ),
                Category(
                    id = "7",
                    title = "Home",
                    iconId = 7,
                ),
                Category(
                    id = "8",
                    title = "Internet",
                    iconId = 8,
                ),
                Category(
                    id = "9",
                    title = "Bars",
                    iconId = 9,
                ),
                Category(
                    id = "10",
                    title = "Gas",
                    iconId = 10,
                ),
                Category(
                    id = "11",
                    title = "Electronics",
                    iconId = 11,
                ),
                Category(
                    id = "12",
                    title = "Cash",
                    iconId = 12,
                ),
            ),
        )
}