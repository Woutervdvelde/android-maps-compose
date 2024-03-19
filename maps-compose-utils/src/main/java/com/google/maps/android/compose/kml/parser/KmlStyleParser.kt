package com.google.maps.android.compose.kml.parser

import android.graphics.Color
import com.google.maps.android.compose.kml.data.KmlStyle
import com.google.maps.android.compose.kml.data.KmlStyleMap
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

internal class KmlStyleParser {
    companion object {
        /**
         *
         */
        @Throws(IOException::class, XmlPullParserException::class)
        fun parseStyleMap(parser: XmlPullParser): KmlStyleMap {
            var eventType = parser.eventType
            var styleKey = ""
            val styleMap = KmlStyleMap()
            styleMap.setId(parser.getAttributeValue(null, "id"))

            while (!(eventType == XmlPullParser.END_TAG && parser.name.equals(KmlParser.STYLE_MAP_TAG))) {
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
         *
         */
        @Throws(IOException::class, XmlPullParserException::class)
        fun parseStyle(parser: XmlPullParser): KmlStyle {
            var eventType = parser.eventType
            val style = KmlStyle()
            style.setId(parser.getAttributeValue(null, "id"))

            while (!(eventType == XmlPullParser.END_TAG && parser.name.equals(KmlParser.STYLE_TAG))) {
                if (eventType == XmlPullParser.START_TAG) {
                    when (parser.name) {
                        ICON_STYLE_TAG -> parseIconStyle(parser, style)
                        LINE_STYLE_TAG -> return style //TODO()
                        POLY_STYLE_TAG -> return style //TODO()
                        BALLOON_STYLE_TAG -> return style //TODO()
                    }
                }
                eventType = parser.next()
            }
            return style
        }

        /**
         *
         */
        @Throws(IOException::class, XmlPullParserException::class)
        private fun parseIconStyle(parser: XmlPullParser, style: KmlStyle) {
            var eventType = parser.eventType

            while (!(eventType == XmlPullParser.END_TAG && parser.name.equals(ICON_STYLE_TAG))) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.name.equals(ICON_HREF_TAG)) {
                        style.setIconUrl(parser.nextText())
                    } else if (parser.name.equals(STYLE_SCALE_TAG)) {
                        style.setScale(parser.nextText().toFloat())
                    } else if (parser.name.equals(STYLE_HEADING_TAG)) {
                        style.setHeading(parser.nextText().toInt())
                    } else if (parser.name.equals(STYLE_HOTSPOT_TAG)) {
                        parseIconHotSpot(parser, style)
                    } else if (parser.name.equals(STYLE_COLOR_TAG)) {
                        style.setColor(parseKmlColor(parser.nextText()))
                    } else if (parser.name.equals(STYLE_COLOR_MODE_TAG)) {
                        style.setColorMode(parser.nextText().equals(COLOR_MODE_RANDOM))
                    }
                }

                eventType = parser.next()
            }
        }

        /**
         *
         */
        @Throws(XmlPullParserException::class)
        private fun parseIconHotSpot(parser: XmlPullParser, style: KmlStyle) {
            val xValue = parser.getAttributeValue(null, "x").toFloat()
            val yValue = parser.getAttributeValue(null, "y").toFloat()
            style.setAnchor(xValue, yValue)
        }

        /**
         * Parses color value from KML data in AABBGGRR format and returns the hue value
         *
         * @param color Color in AABBGGRR format
         * @return Float hue value from color
         */
        private fun parseKmlColor(color: String): Float {
            val integerColor = Color.parseColor("#${convertColorToAARRGGB(color)}")
            val hsvValues = FloatArray(HSV_VALUES)
            Color.colorToHSV(integerColor, hsvValues)
            return hsvValues[HUE_VALUE]
        }

        /**
         * Converts a color format of the form AABBGGRR to AARRGGBB.
         * Any leading or trailing spaces in the provided string will be trimmed prior to conversion.
         *
         * @param color Color in AABBGGRR format
         * @return color in AARRGGBB format
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

        private const val STYLE_MAP_KEY_TAG = "key"
        private const val STYLE_URL_TAG = "styleUrl"
        private const val STYLE_MAP_NORMAL_STYLE = "normal"
        private const val ICON_STYLE_TAG = "IconStyle"
        private const val ICON_HREF_TAG = "href"
        private const val STYLE_SCALE_TAG = "scale"
        private const val STYLE_HEADING_TAG = "heading"
        private const val STYLE_HOTSPOT_TAG = "hotSpot"
        private const val STYLE_COLOR_TAG = "color"
        private const val STYLE_COLOR_MODE_TAG = "colorMode"
        private const val LINE_STYLE_TAG = "LineStyle"
        private const val POLY_STYLE_TAG = "PolyStyle"
        private const val BALLOON_STYLE_TAG = "BalloonStyle"
        private const val COLOR_MODE_RANDOM = "random"
        private const val HSV_VALUES = 3
        private const val HUE_VALUE = 0
    }
}