package com.google.maps.android.compose.kml.data

import com.google.maps.android.compose.kml.parser.Anchor

public class KmlStyle: KmlStyleId() {
    private var mIconUrl: String? = DEFAULT_ICON_URL
    private var mIconScale: Float = DEFAULT_ICON_SCALE
    private var mIconHeading: Int = DEFAULT_ICON_HEADING
    private var mIconAnchor: Anchor = DEFAULT_ICON_ANCHOR
    private var mIconColor: Float? = DEFAULT_ICON_COLOR
    private var mIconAlpha: Float = DEFAULT_ICON_ALPHA
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

    internal fun setIconAnchor(anchor: Anchor) {
        mIconAnchor = anchor
    }

    internal fun setIconColor(colorHue: Float) {
        mIconColor = colorHue
    }

    internal fun setIconAlpha(alpha: Float) {
        mIconAlpha = alpha
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
     * Gets the size of the icon
     * @return scale value
     */
    public fun getIconScale(): Float = mIconScale

    /**
     * Gets the heading (rotation) of the icon
     * @return heading in degrees
     */
    public fun getIconHeading(): Int = mIconHeading

    /**
     * Gets icon offset
     * @return offset with x, y values
     */
    public fun getIconAnchor(): Anchor = mIconAnchor

    /**
     * Gets the color hue of the icon color
     * @return hue
     */
    public fun getIconColor(): Float? = mIconColor

    /**
     * Gets the alpha value of the icon
     * @return float value between 0 and 1
     */
    public fun getIconAlpha(): Float = mIconAlpha

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
        const val DEFAULT_ICON_ALPHA = 1f
        const val DEFAULT_ICON_COLOR_MODE_RANDOM = false
    }
}