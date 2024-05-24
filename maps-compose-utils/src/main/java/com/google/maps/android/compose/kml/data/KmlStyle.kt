package com.google.maps.android.compose.kml.data

import androidx.compose.ui.graphics.Color
import com.google.maps.android.compose.kml.parser.Anchor

public class KmlStyle: KmlStyleId() {
    private var mIconUrl: String? = DEFAULT_ICON_URL
    private var mIconScale: Float = DEFAULT_ICON_SCALE
    private var mIconHeading: Int = DEFAULT_ICON_HEADING
    private var mIconAnchor: Anchor = DEFAULT_ICON_ANCHOR
    private var mIconColor: Float? = DEFAULT_ICON_COLOR
    private var mIconAlpha: Float = DEFAULT_ICON_ALPHA
    private var mIconColorModeRandom: Boolean = DEFAULT_ICON_COLOR_MODE_RANDOM

    private var mLineColor: Color = DEFAULT_LINE_COLOR
    private var mLineWidth: Float = DEFAULT_LINE_WIDTH

    private var mPolyFillColor: Color = DEFAULT_POLY_FILL_COLOR
    private var mPolyFill: Boolean = DEFAULT_POLY_FILL
    private var mPolyOutline: Boolean = DEFAULT_POLY_OUTLINE

    /**
     * Gets the current iconUrl of the marker
     *
     * @return [String?] icon url
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
     * @return [Float] scale value
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
     * @return [Int] heading in degrees
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
     * @return [Anchor] offset with x, y values
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
     * @return [Float?] hue
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
     * @return [Float] value between 0 and 1
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
     * @return [Boolean] true if the icon colorMode is set to random
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

    /**
     * Gets the color of the line
     *
     * @return [Color] color
     */
    public fun getLineColor(): Color = mLineColor


    /**
     * Sets the line color
     *
     * @param color Color of the line
     */
    internal fun setLineColor(color: Color) {
        mLineColor = color
    }

    /**
     * Gets the width of the line in pixels
     *
     * @return [Float] width in pixels
     */
    public fun getLineWidth(): Float = mLineWidth

    /**
     * Sets the line width
     *
     * @param width width of the line in pixels
     */
    internal fun setLineWidth(width: Float) {
        mLineWidth = width
    }

    /**
     * Gets the fill color of a polygon style
     *
     * @return [Color] color of the polygon fill
     */
    public fun getPolyFillColor(): Color = mPolyFillColor


    /**
     * Sets the fill color of a polygon
     *
     * @param color color of the polygon fill
     */
    internal fun setFillColor(color: Color) {
        mPolyFillColor = color
    }

    /**
     * Gets if a polygon should be filled or not
     *
     * @return [Boolean] true if polygon is filled
     */
    public fun getPolyFill(): Boolean = mPolyFill


    /**
     * Sets if a polygon should be filled or not
     *
     * @param fill true if polygon should be filled
     */
    internal fun setPolyFill(fill: Boolean) {
        mPolyFill = fill
    }


    /**
     * Gets if a polygon should have an outline (stroke)
     *
     * @return [Boolean] true if polygon should have an outline
     */
    public fun getPolyOutline(): Boolean = mPolyOutline


    /**
     * Sets if a polygon should have an outline (stroke)
     *
     * @param outline true if polygon should have an outline
     */
    internal fun setPolyOutline(outline: Boolean) {
        mPolyOutline = outline
    }

    internal companion object {
        val DEFAULT_ICON_URL = null
        const val DEFAULT_ICON_SCALE = 1f
        const val DEFAULT_ICON_HEADING = 0
        val DEFAULT_ICON_ANCHOR = Anchor()
        val DEFAULT_ICON_COLOR = null
        const val DEFAULT_ICON_ALPHA = 1f
        const val DEFAULT_ICON_COLOR_MODE_RANDOM = false

        val DEFAULT_LINE_COLOR = Color.Black
        const val DEFAULT_LINE_WIDTH = 1f

        val DEFAULT_POLY_FILL_COLOR = Color.Black
        const val DEFAULT_POLY_FILL = true
        const val DEFAULT_POLY_OUTLINE = true
    }
}