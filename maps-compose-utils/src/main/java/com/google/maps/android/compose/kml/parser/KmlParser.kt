package com.google.maps.android.compose.kml.parser

import com.google.maps.android.compose.kml.manager.ContainerManager
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

/**
 * Parses a given KML file into KmlStyle, KmlPlacemark, KmlGroundOverlay and KmlContainer objects
 */
internal class KmlParser (
    private val mParser: XmlPullParser,
    var container: ContainerManager = ContainerManager()
) {

    /**
     * Parses the KML file stored in the current XmlPullParser.
     * Stores values in their corresponding list
     */
    @Throws(IOException::class, XmlPullParserException::class)
    fun parseKml() {
        var eventType = mParser.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (mParser.name.matches(CONTAINER_REGEX)) {
                    container.addContainer(parseKmlContainer(mParser))
                }
//                if (mParser.name.equals("Placemark")) {
//                    val markerManager = MarkerManager()
//                    container.addMarker(markerManager)
//                }
            }

            eventType = mParser.next()
        }
    }

    /**
     * Parses KML from nested container
     *
     * @param
     */
    private fun parseKmlContainer(parser: XmlPullParser): ContainerManager {
        //TODO(extract id from container tag)
        parser.next()
        var eventType = parser.eventType
        val containerManager = ContainerManager()

        while (!(eventType == XmlPullParser.END_TAG && parser.name.matches(CONTAINER_REGEX))) {
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.name.matches(CONTAINER_REGEX)) {
                    containerManager.addContainer(parseKmlContainer(parser))
                } else if (parser.name.equals(NAME_TAG)) {
                    containerManager.setName(parser.nextText())
                } else if (parser.name.equals(PLACEMARK_TAG)) {
                    KmlPlacemarkParser.parsePlacemark(parser, containerManager)
                }
            }
            eventType = parser.next()
        }

        return containerManager
    }

    /**
     * Skips tags from START_TAG to END_TAG
     * @param parser XmlPullParser
     */
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
        private const val STYLE_TAG = "Style"
        private const val STYLE_MAP_TAG = "StyleMap"
        private const val PLACEMARK_TAG = "Placemark"
        private const val NAME_TAG = "name"
        private const val GROUND_OVERLAY_TAG = "GroundOverlay"
        private val CONTAINER_REGEX = Regex("Folder|Document")
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