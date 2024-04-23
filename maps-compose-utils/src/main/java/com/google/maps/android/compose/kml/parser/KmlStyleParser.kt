package com.google.maps.android.compose.kml.parser

import android.graphics.Color
import com.google.maps.android.compose.kml.data.KmlStyle
import com.google.maps.android.compose.kml.data.KmlStyleMap
import com.google.maps.android.compose.kml.data.KmlTags.Companion.BALLOON_STYLE_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.HREF_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.ICON_STYLE_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.LINE_STYLE_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.POLY_STYLE_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.STYLE_COLOR_MODE_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.STYLE_COLOR_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.STYLE_HEADING_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.STYLE_HOTSPOT_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.STYLE_MAP_KEY_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.STYLE_MAP_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.STYLE_SCALE_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.STYLE_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.STYLE_URL_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.WIDTH_TAG
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.util.Random
import androidx.compose.ui.graphics.Color as ComposeColor

internal class KmlStyleParser {
    companion object {
        /**
         * Parses the StyleMap tag and stores it in a [KmlStyleMap]
         *
         * @param parser XmlPullParser containing the StyleMap tag
         * @return KmlStyleMap containing all style id information
         */
        @Throws(IOException::class, XmlPullParserException::class)
        fun parseStyleMap(parser: XmlPullParser): KmlStyleMap {
            var eventType = parser.eventType
            var styleKey = ""
            val styleMap = KmlStyleMap()
            styleMap.setId(parser.getAttributeValue(null, "id"))

            while (!(eventType == XmlPullParser.END_TAG && parser.name.equals(STYLE_MAP_TAG))) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.name.equals(STYLE_MAP_KEY_TAG)) {
                        styleKey = parser.nextText()
                    } else if (parser.name.equals(STYLE_URL_TAG)) {
                        if (styleKey == STYLE_MAP_NORMAL_STYLE) {
                            styleMap.addNormalStyle(styleKey, parser.nextText())
                        } else {
                            styleMap.addStyle(styleKey, parser.nextText())
                        }
                        styleKey = ""
                    }
                }
                eventType = parser.next()
            }
            return styleMap
        }

        /**
         * Parses the IconStyle, LineStyle, PolyStyle and BalloonStyle into a [KmlStyle] object
         *
         * @param parser XmlPullParser containing the Style tag
         */
        @Throws(IOException::class, XmlPullParserException::class)
        fun parseStyle(parser: XmlPullParser): KmlStyle {
            var eventType = parser.eventType
            val style = KmlStyle()
            style.setId(parser.getAttributeValue(null, "id"))

            while (!(eventType == XmlPullParser.END_TAG && parser.name.equals(STYLE_TAG))) {
                if (eventType == XmlPullParser.START_TAG) {
                    when (parser.name) {
                        ICON_STYLE_TAG -> parseIconStyle(parser, style)
                        LINE_STYLE_TAG -> parseLineStyle(parser, style)
                        POLY_STYLE_TAG -> return style //TODO()
                        BALLOON_STYLE_TAG -> return style //TODO()
                    }
                }
                eventType = parser.next()
            }
            return style
        }

        /**
         * Receives input from the XMLPullParser and assigns relevant properties to a [KmlStyle]
         *
         * @param parser The XMLPullParser
         * @param style The KmlStyle properties should be saved in
         */
        @Throws(IOException::class, XmlPullParserException::class)
        private fun parseIconStyle(parser: XmlPullParser, style: KmlStyle) {
            var eventType = parser.eventType

            while (!(eventType == XmlPullParser.END_TAG && parser.name.equals(ICON_STYLE_TAG))) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.name.equals(HREF_TAG)) {
                        style.setIconUrl(parser.nextText())
                    } else if (parser.name.equals(STYLE_SCALE_TAG)) {
                        style.setIconScale(parser.nextText().toFloat())
                    } else if (parser.name.equals(STYLE_HEADING_TAG)) {
                        style.setIconHeading(parser.nextText().toInt())
                    } else if (parser.name.equals(STYLE_HOTSPOT_TAG)) {
                        parseIconHotSpot(parser, style)
                    } else if (parser.name.equals(STYLE_COLOR_TAG)) {
                        val (color, alpha) = parseKmlColor(parser.nextText())
                        style.setIconColor(color)
                        style.setIconAlpha(alpha)
                    } else if (parser.name.equals(STYLE_COLOR_MODE_TAG)) {
                        style.setIconColorMode(parser.nextText().equals(COLOR_MODE_RANDOM))
                    }
                }

                eventType = parser.next()
            }
        }

        /**
         * Receives input from the XMLPullParser and assigns relevant properties to a [KmlStyle]
         *
         * @param parser The XMLPullParser
         * @param style The KmlStyle properties should be saved in
         */
        private fun parseLineStyle(parser: XmlPullParser, style: KmlStyle) {
            var eventType = parser.eventType

            while (!(eventType == XmlPullParser.END_TAG && parser.name.equals(LINE_STYLE_TAG))) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.name.equals(STYLE_COLOR_TAG)) {
                        val stringColor = convertColorToAARRGGB(parser.nextText())
                        style.setLineColor(convertStringToColor(stringColor))
                    } else if (parser.name.equals(WIDTH_TAG)) {
                        style.setLineWidth(parser.nextText().toFloat())
                    }
                }

                eventType = parser.next()
            }
        }

        /**
         * Sets the hot spot for the icon
         *
         * @param parser The XmlPullParser
         * @param style The [KmlStyle] the value should be set to
         */
        @Throws(XmlPullParserException::class)
        private fun parseIconHotSpot(parser: XmlPullParser, style: KmlStyle) {
            val xUnits = parser.getAttributeValue(null, "xunits")
            val yUnits = parser.getAttributeValue(null, "yunits")
            val xValue = parser.getAttributeValue(null, "x").toFloat()
            val yValue = parser.getAttributeValue(null, "y").toFloat()
            style.setIconAnchor(Anchor(xValue, yValue, xUnits, yUnits))
        }

        /**
         * Parses color value from KML data in AABBGGRR format and returns the hue value
         *
         * @param color Color in AABBGGRR format
         * @return Pair with first containing float hue value from color and as second the alpha
         */
        private fun parseKmlColor(color: String): Pair<Float, Float> {
            val c = color.substringAfter('#')
            val integerColor =
                Color.parseColor("#${convertColorToAARRGGB(c)}")

            val alpha = if (c.length > 6) {
                val intValue = c.substring(0, 2).toInt(16)
                intValue.toFloat() / 255f
            } else 1f

            return Pair(convertIntColorToHueValue(integerColor), alpha)
        }


        /**
         * Converts an integer color value and returns the hue value
         *
         * @param color Color as integer
         * @return Float hue value from color
         */
        internal fun convertIntColorToHueValue(color: Int): Float {
            val hsvValues = FloatArray(HSV_VALUES)
            Color.colorToHSV(color, hsvValues)
            return hsvValues[HUE_VALUE]
        }

        /**
         * Converts a color format of the form (AA)BBGGRR to (AA)RRGGBB.
         * Any leading or trailing spaces in the provided string will be trimmed prior to conversion.
         *
         * @param color Color in (AA)BBGGRR format
         * @return color in (AA)RRGGBB format
         */
        private fun convertColorToAARRGGB(color: String): String {
            val trimmedColor = color.trim()
            return if (trimmedColor.length > 6) {
                trimmedColor.substring(0, 2) +
                        trimmedColor.substring(6, 8) +
                        trimmedColor.substring(4, 6) +
                        trimmedColor.substring(2, 4)
            } else {
                trimmedColor.substring(4, 6) +
                        trimmedColor.substring(2, 4) +
                        trimmedColor.substring(0, 2)
            }
        }

        private fun convertStringToColor(color: String): ComposeColor {
            require(color.length == 6 || color.length == 8)
            val colorLong = color.toLong(radix = 16)
            val colorWithAlpha = if (color.length == 6) {
                0xFF000000.toInt() or colorLong.toInt()
            } else {
                colorLong.toInt()
            }

            return ComposeColor(colorWithAlpha)
        }

        /**
         * Computes a random color given an integer. Algorithm to compute the random color can be found in
         * https://developers.google.com/kml/documentation/kmlreference#colormode
         *
         * @param color Color represented as an integer
         * @return Integer representing a random color
         */
        internal fun computeRandomColor(color: Int): Int {
            val random = Random()
            var red = Color.red(color)
            var green = Color.green(color)
            var blue = Color.blue(color)

            if (red != 0) {
                red = random.nextInt(red)
            }
            if (blue != 0) {
                blue = random.nextInt(blue)
            }
            if (green != 0) {
                green = random.nextInt(green)
            }
            if (red == 0 && blue == 0 && green == 0) {
                red = random.nextInt(256)
                blue = random.nextInt(256)
                green = random.nextInt(256)
            }
            return Color.rgb(red, green, blue)
        }


        private const val STYLE_MAP_NORMAL_STYLE = "normal"
        private const val COLOR_MODE_RANDOM = "random"
        private const val HSV_VALUES = 3
        private const val HUE_VALUE = 0
        const val HOTSPOT_UNIT_FRACTION = "fraction"
        const val HOTSPOT_UNIT_PIXELS = "pixels"
        const val HOTSPOT_UNIT_INSET_PIXELS = "insetPixels"
    }
}

/**
 * Helper data class for saving hotSpot anchor information
 */
public data class Anchor(
    val x: Float = 0.5f,
    val y: Float = 1.0f,
    val xUnit: String = KmlStyleParser.HOTSPOT_UNIT_FRACTION,
    val yUnit: String = KmlStyleParser.HOTSPOT_UNIT_FRACTION
)