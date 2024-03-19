package com.google.maps.android.compose.kml.data

public class KmlStyle: KmlStyleId() {
    private var mIconUrl: String = ""
    private var mScale: Float = 1f
    private var mHeading: Int = 0
    private var mAnchor: Anchor = Anchor()
    private var mColor: Float = 0f
    private var mColorModeRandom: Boolean = false

    internal fun setIconUrl(url: String) {
        mIconUrl = url
    }

    internal fun setScale(scale: Float) {
        mScale = scale
    }

    internal fun setHeading(heading: Int) {
        mHeading = heading
    }

    internal fun setAnchor(x: Float, y: Float) {
        mAnchor = Anchor(x, y)
    }

    internal fun setColor(colorHue: Float) {
        mColor = colorHue
    }

    internal fun setColorMode(isRandomMode: Boolean) {
        mColorModeRandom = isRandomMode
    }

    public fun getIconUrl(): String = mIconUrl
}

internal data class Anchor(
    val x: Float = 0f,
    val y: Float = 0f
)