package com.google.maps.android.compose.kml.parser

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

internal class KmlParser (
    private val mParser: XmlPullParser,
    val mPlacemarks: HashMap<Any, Any> = HashMap()
) {


    fun parseKml() {
        var eventType = mParser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (mParser.name.matches(UNSUPPORTED_REGEX)) {
                    skip(mParser)
                }
                if (mParser.name.equals(STYLE)) {
                    //TODO(handle style)
                }
                if (mParser.name.equals(STYLE_MAP)) {
                    //TODO(handle stylemap)
                }
                if (mParser.name.equals(PLACEMARK)) {
                    //TODO(handle placemark)
                }
                if (mParser.name.equals(GROUND_OVERLAY)) {
                    //TODO(handle groundoverlay)
                }
            }

            eventType = mParser.next()
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun skip(parser: XmlPullParser) {
        check(parser.eventType == XmlPullParser.START_TAG)
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    companion object {
        private const val STYLE = "Style"
        private const val STYLE_MAP = "StyleMap"
        private const val PLACEMARK = "Placemark"
        private const val GROUND_OVERLAY = "GroundOverlay"
        private const val CONTAINER_REGEX = "Folder|Document"
        private val UNSUPPORTED_REGEX = Regex("altitude|altitudeModeGroup|altitudeMode|" +
                "begin|bottomFov|cookie|displayName|displayMode|end|expires|extrude|" +
                "flyToView|gridOrigin|httpQuery|leftFov|linkDescription|linkName|linkSnippet|" +
                "listItemType|maxSnippetLines|maxSessionLength|message|minAltitude|minFadeExtent|" +
                "minLodPixels|minRefreshPeriod|maxAltitude|maxFadeExtent|maxLodPixels|maxHeight|" +
                "maxWidth|near|NetworkLink|NetworkLinkControl|overlayXY|range|refreshMode|" +
                "refreshInterval|refreshVisibility|rightFov|roll|rotationXY|screenXY|shape|sourceHref|" +
                "state|targetHref|tessellate|tileSize|topFov|viewBoundScale|viewFormat|viewRefreshMode|" +
                "viewRefreshTime|when")
    }
}