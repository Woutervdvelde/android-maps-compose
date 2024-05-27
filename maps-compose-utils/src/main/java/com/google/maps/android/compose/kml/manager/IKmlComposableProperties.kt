package com.google.maps.android.compose.kml.manager

import com.google.maps.android.compose.kml.parser.Anchor
import com.google.maps.android.compose.kml.parser.ExtendedData

public interface IKmlComposableProperties {
    public val name: String
    public val description: String
    public val drawOrder: Float
    public val styleUrl: String?
    public val extendedData: List<ExtendedData>?

    public companion object {
        internal const val DEFAULT_NAME = ""
        internal const val DEFAULT_DESCRIPTION = ""
        internal const val DEFAULT_DRAW_ORDER = 0f
        internal const val DEFAULT_STYLE_URL = ""
        internal val DEFAULT_EXTENDED_DATA = null

        internal const val DEFAULT_VISIBILITY = true
        internal const val DEFAULT_ALPHA = 1f
        internal val DEFAULT_ANCHOR = Anchor()
        internal const val DEFAULT_ROTATION = 0
        internal val DEFAULT_COLOR = null
        internal val DEFAULT_ICON = null

        internal fun convertPropertyToBoolean(
            properties: HashMap<String, Any>,
            key: String,
            defaultValue: Boolean = false
        ): Boolean {
            val value = properties[key] as? String
            return value?.let { it == "1" } ?: defaultValue
        }

        internal fun convertPropertyToFloat(
            properties: HashMap<String, Any>,
            key: String,
            defaultValue: Float = 1f
        ): Float {
            val value = properties[key] as? String
            return value?.toFloat() ?: defaultValue
        }
    }
}