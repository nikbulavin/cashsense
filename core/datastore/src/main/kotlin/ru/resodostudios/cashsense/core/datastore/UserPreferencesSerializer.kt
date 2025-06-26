package ru.resodostudios.cashsense.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import ru.resodostudios.cashsense.core.util.getDefaultCurrency
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * An [androidx.datastore.core.Serializer] for the [UserPreferences] proto.
 */
class UserPreferencesSerializer @Inject constructor() : Serializer<UserPreferences> {

    override val defaultValue: UserPreferences = getCustomInstance()

    override suspend fun readFrom(input: InputStream): UserPreferences =
        try {
            UserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}

internal fun getCustomInstance() = userPreferences {
    currency = getDefaultCurrency().currencyCode
    useDynamicColor = true
    shouldShowTotalBalance = true
}