package com.google.maps.android.compose.kml.manager

import com.google.maps.android.compose.kml.parser.Anchor
import com.google.maps.android.compose.kml.parser.ExtendedData

public abstract class KmlComposableProperties(
    public val name: String = DEFAULT_NAME,
    public val description: String = DEFAULT_DESCRIPTION,
    public val drawOrder: Float = DEFAULT_DRAW_ORDER,
    public val styleUrl: String? = DEFAULT_STYLE_URL,
    public val extendedData: List<ExtendedData>? = DEFAULT_EXTENDED_DATA
) {
    internal companion object {
        fun convertPropertyToFloat(
            properties: HashMap<String, Any>,
            key: String,
            default: Float
        ): Float {
            return (properties[key] as? Float) ?: default
        }

        fun convertPropertyToBoolean(
            properties: HashMap<String, Any>,
            key: String,
            default: Boolean
        ): Boolean {
            return (properties[key] as? Boolean) ?: default
        }

        internal const val DEFAULT_NAME = ""
        internal const val DEFAULT_DESCRIPTION = ""
        internal const val DEFAULT_DRAW_ORDER = 1f
        internal const val DEFAULT_STYLE_URL = ""
        internal val DEFAULT_EXTENDED_DATA = null

        internal const val DEFAULT_VISIBILITY = true
        internal const val DEFAULT_ALPHA = 1f
        internal val DEFAULT_ANCHOR = Anchor()
        internal const val DEFAULT_ROTATION = 0
        internal val DEFAULT_COLOR = null
        internal val DEFAULT_ICON = null
    }
}

public interface IKmlComposableProperties {
    public val name: String
    public val description: String
    public val drawOrder: Float
    public val styleUrl: String?
    public val extendedData: List<ExtendedData>?
}