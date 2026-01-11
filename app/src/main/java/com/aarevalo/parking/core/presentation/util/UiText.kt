package com.aarevalo.parking.core.presentation.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * A sealed interface that represents text that can be displayed in the UI.
 * This allows ViewModels to provide text without needing a Context reference,
 * deferring the string resolution until the UI layer.
 *
 * Usage:
 * - Use [DynamicString] for runtime-generated strings
 * - Use [StringResource] for strings defined in strings.xml
 *
 * Example:
 * ```kotlin
 * // In ViewModel
 * val errorMessage: UiText = UiText.StringResource(R.string.error_network)
 *
 * // In Composable
 * Text(text = errorMessage.asString())
 *
 * // In non-Composable context
 * toast.show(errorMessage.asString(context))
 * ```
 */
sealed interface UiText {

    /**
     * Represents a dynamic string that is generated at runtime.
     * Use this for strings that come from APIs or are computed dynamically.
     */
    data class DynamicString(val value: String) : UiText

    /**
     * Represents a string resource from strings.xml.
     * Supports format arguments for parameterized strings.
     *
     * @param id The string resource ID
     * @param args Optional format arguments
     */
    class StringResource(
        @StringRes val id: Int,
        val args: Array<Any> = arrayOf()
    ) : UiText {
        // Override equals and hashCode for proper comparison
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is StringResource) return false
            return id == other.id && args.contentEquals(other.args)
        }

        override fun hashCode(): Int {
            var result = id
            result = 31 * result + args.contentHashCode()
            return result
        }
    }

    /**
     * Resolves the text in a Composable context.
     * Use this when displaying text in Compose UI.
     */
    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(id = id, formatArgs = args)
        }
    }

    /**
     * Resolves the text using a Context.
     * Use this in non-Composable contexts like Services, BroadcastReceivers, etc.
     */
    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(id, *args)
        }
    }

    companion object {
        /**
         * Creates a UiText from a nullable string.
         * Returns an empty DynamicString if the input is null.
         */
        fun fromString(value: String?): UiText {
            return DynamicString(value ?: "")
        }

        /**
         * Creates an empty UiText.
         */
        fun empty(): UiText = DynamicString("")
    }
}
