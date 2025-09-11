package ru.resodostudios.cashsense.core.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import ru.resodostudios.cashsense.core.designsystem.component.AnimatedIcon
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.AccountBalance
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Apparel
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Attractions
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Autorenew
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.BakeryDining
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Bitcoin
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Book
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Category
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Chair
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.CreditCard
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Dentistry
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Diamond
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.DirectionsBus
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Exercise
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Fastfood
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Handyman
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Language
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.LocalBar
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.LocalGasStation
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.LocalTaxi
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Memory
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Monitoring
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Movie
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.MusicNote
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Payments
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Percent
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Pets
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Phishing
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Pill
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ReceiptLong
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Restaurant
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.School
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Scooter
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SelfCare
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ShoppingCart
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SimCard
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SmokingRooms
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SportsEsports
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Travel
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Work

@Composable
fun IconPickerDropdownMenu(
    currentIcon: ImageVector,
    onSelectedIconClick: (Int) -> Unit,
    onClick: () -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(
            onClick = {
                onClick()
                expanded = true
            },
        ) {
            AnimatedIcon(
                icon = currentIcon,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            FlowRow(maxItemsInEachRow = 5) {
                StoredIcon.entries.forEach { icon ->
                    IconButton(
                        onClick = {
                            onSelectedIconClick(icon.storedId)
                            expanded = false
                        },
                    ) {
                        Icon(
                            imageVector = icon.imageVector,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}

enum class StoredIcon(
    val imageVector: ImageVector,
    val storedId: Int,
) {
    CATEGORY(CsIcons.Outlined.Category, 0),
    ACCOUNT_BALANCE(CsIcons.Outlined.AccountBalance, 1),
    APPAREL(CsIcons.Outlined.Apparel, 2),
    CHAIR(CsIcons.Outlined.Chair, 3),
    EXERCISE(CsIcons.Outlined.Exercise, 4),
    FASTFOOD(CsIcons.Outlined.Fastfood, 5),
    RESTAURANT(CsIcons.Outlined.Restaurant, 17),
    BAKERY_DINING(CsIcons.Outlined.BakeryDining, 37),
    DIRECTIONS_BUS(CsIcons.Outlined.DirectionsBus, 6),
    LOCAL_TAXI(CsIcons.Outlined.LocalTaxi, 35),
    SCOOTER(CsIcons.Outlined.Scooter, 36),
    HANDYMAN(CsIcons.Outlined.Handyman, 7),
    LANGUAGE(CsIcons.Outlined.Language, 8),
    LOCAL_BAR(CsIcons.Outlined.LocalBar, 9),
    LOCAL_GAS_STATION(CsIcons.Outlined.LocalGasStation, 10),
    MEMORY(CsIcons.Outlined.Memory, 11),
    PAYMENTS(CsIcons.Outlined.Payments, 12),
    PETS(CsIcons.Outlined.Pets, 13),
    PHISHING(CsIcons.Outlined.Phishing, 14),
    PILL(CsIcons.Outlined.Pill, 15),
    TRANSACTION(CsIcons.Outlined.ReceiptLong, 16),
    SCHOOL(CsIcons.Outlined.School, 18),
    SELF_CARE(CsIcons.Outlined.SelfCare, 19),
    SHOPPING_CART(CsIcons.Outlined.ShoppingCart, 20),
    SIM_CARD(CsIcons.Outlined.SimCard, 21),
    SMOKING_ROOMS(CsIcons.Outlined.SmokingRooms, 22),
    SPORTS_ESPORTS(CsIcons.Outlined.SportsEsports, 23),
    TRAVEL(CsIcons.Outlined.Travel, 24),
    ATTRACTIONS(CsIcons.Outlined.Attractions, 25),
    CREDIT_CARD(CsIcons.Outlined.CreditCard, 26),
    MONITORING(CsIcons.Outlined.Monitoring, 27),
    MUSIC_NOTE(CsIcons.Outlined.MusicNote, 28),
    WORK(CsIcons.Outlined.Work, 29),
    BITCOIN(CsIcons.Outlined.Bitcoin, 30),
    BOOK(CsIcons.Outlined.Book, 31),
    DENTISTRY(CsIcons.Outlined.Dentistry, 32),
    DIAMOND(CsIcons.Outlined.Diamond, 33),
    MOVIE(CsIcons.Outlined.Movie, 34),
    PERCENT(CsIcons.Outlined.Percent, 38),
    AUTORENEW(CsIcons.Outlined.Autorenew, 39);

    companion object {

        fun asImageVector(storedId: Int?): ImageVector =
            entries.find { it.storedId == storedId }?.imageVector ?: CATEGORY.imageVector
    }
}