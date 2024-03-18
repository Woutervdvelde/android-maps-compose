package com.google.maps.android.compose.kml.parser

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
                    when(parser.name) {
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

                }

                eventType = parser.next()
            }
        }

        private const val STYLE_MAP_KEY_TAG = "key"
        private const val STYLE_URL_TAG = "styleUrl"
        private const val STYLE_MAP_NORMAL_STYLE = "normal"
        private const val ICON_STYLE_TAG = "IconStyle"
        private const val LINE_STYLE_TAG = "LineStyle"
        private const val POLY_STYLE_TAG = "PolyStyle"
        private const val BALLOON_STYLE_TAG = "BalloonStyle"
    }
}