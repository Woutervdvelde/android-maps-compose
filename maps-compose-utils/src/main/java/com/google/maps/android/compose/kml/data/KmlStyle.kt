package com.google.maps.android.compose.kml.data

public class KmlStyle: KmlStyleId() {
    private var mIconUrl: String? = DEFAULT_ICON_URL
    private var mIconScale: Float = DEFAULT_ICON_SCALE
    private var mIconHeading: Int = DEFAULT_ICON_HEADING
    private var mIconAnchor: Anchor = DEFAULT_ICON_ANCHOR
    private var mIconColor: Float? = DEFAULT_ICON_COLOR
    private var mIconColorModeRandom: Boolean = DEFAULT_ICON_COLOR_MODE_RANDOM

    internal fun setIconUrl(url: String) {
        mIconUrl = url
    }

    internal fun setIconScale(scale: Float) {
        mIconScale = scale
    }

    internal fun setIconHeading(heading: Int) {
        mIconHeading = heading
    }

    internal fun setIconAnchor(x: Float, y: Float) {
        mIconAnchor = Anchor(x, y)
    }

    internal fun setIconColor(colorHue: Float) {
        mIconColor = colorHue
    }

    internal fun setIconColorMode(isRandomMode: Boolean) {
        mIconColorModeRandom = isRandomMode
    }

    /**
     * Gets the iconUrl of the marker if set
     * @return iconUrl if set or [DEFAULT_ICON_URL]
     */
    public fun getIconUrl(): String? = mIconUrl

    /**
     * Gets the size of the marker
     * @return scale value
     */
    public fun getIconScale(): Float = mIconScale

    /**
     * Gets the color hue of the marker color
     * @return hue
     */
    public fun getIconColor(): Float? = mIconColor

    /**
     * @return true if the icon colorMode is set to random
     */
    public fun getIconRandomColorMode(): Boolean = mIconColorModeRandom

    internal companion object {
        val DEFAULT_ICON_URL = null
        const val DEFAULT_ICON_SCALE = 1f
        const val DEFAULT_ICON_HEADING = 0
        val DEFAULT_ICON_ANCHOR = Anchor()
        val DEFAULT_ICON_COLOR = null
        const val DEFAULT_ICON_COLOR_MODE_RANDOM = false
    }
}

/**
 * Internal helper data class to easily store Anchor data used for the KML hotSpot tag
 */
internal data class Anchor(
    val x: Float = 0f,
    val y: Float = 0f
)