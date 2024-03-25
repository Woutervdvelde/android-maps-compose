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

    /**
     * Gets the current iconUrl of the marker
     *
     * @return icon url
     */
    public fun getIconUrl(): String? = mIconUrl

    /**
     * Sets iconUrl of the marker
     *
     * @param url icon url
     */
    internal fun setIconUrl(url: String) {
        mIconUrl = url
    }

    /**
     * Gets the size of the icon
     *
     * @return scale value
     */
    public fun getIconScale(): Float = mIconScale

    /**
     * Sets icon scale
     *
     * @param scale Icon scale
     */
    internal fun setIconScale(scale: Float) {
        mIconScale = scale
    }

    /**
     * Gets the heading (rotation) of the icon
     *
     * @return heading in degrees
     */
    public fun getIconHeading(): Int = mIconHeading

    /**
     * Sets the icon heading (rotation)
     *
     * @param heading represented in degrees (0 - 360)
     */
    internal fun setIconHeading(heading: Int) {
        mIconHeading = heading
    }

    /**
     * Gets icon offset
     *
     * @return offset with x, y values
     */
    public fun getIconAnchor(): Anchor = mIconAnchor

    /**
     * Sets icon anchor, parsed from kml <hotSpot> tag
     *
     * @param anchor Anchor value
     */
    internal fun setIconAnchor(anchor: Anchor) {
        mIconAnchor = anchor
    }

    /**
     * Gets the color hue of the icon color
     *
     * @return hue
     */
    public fun getIconColor(): Float? = mIconColor

    /**
     * Sets the color of the icon using the provided hue value.
     *
     * @param colorHue Hue value representing the color of the icon. (0 - 360)
     */
    internal fun setIconColor(colorHue: Float) {
        mIconColor = colorHue
    }

    /**
     * Gets the alpha value of the icon
     *
     * @return float value between 0 and 1
     */
    public fun getIconAlpha(): Float = mIconAlpha

    /**
     * Sets the alpha of the icon
     *
     * @param alpha Float value between 0f and 1f
     */
    internal fun setIconAlpha(alpha: Float) {
        mIconAlpha = alpha
    }

    /**
     * @return true if the icon colorMode is set to random
     */
    public fun getIconRandomColorMode(): Boolean = mIconColorModeRandom

    /**
     * Sets the icon random colorMode
     *
     * @param isRandomMode true if colorMode is set tue 'random'
     */
    internal fun setIconColorMode(isRandomMode: Boolean) {
        mIconColorModeRandom = isRandomMode
    }

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