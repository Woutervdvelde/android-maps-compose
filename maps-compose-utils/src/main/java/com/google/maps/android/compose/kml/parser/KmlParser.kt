package com.google.maps.android.compose.kml.parser

import android.graphics.Bitmap
import com.google.maps.android.compose.kml.data.KmlStyle
import com.google.maps.android.compose.kml.data.KmlStyleMap
import com.google.maps.android.compose.kml.manager.ContainerManager
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

/**
 * Parses a given KML file into KmlStyle, KmlPlacemark, KmlGroundOverlay and KmlContainer objects
 */
internal class KmlParser (
    private val parser: XmlPullParser,
    private val styleMaps: HashMap<String, KmlStyleMap> = hashMapOf(),
    private val styles: HashMap<String, KmlStyle> = hashMapOf(),
    var container: ContainerManager = ContainerManager(),
) {

    /**
     * Parses the KML file stored in the current XmlPullParser.
     * Creates a ContainerManager that stores the relevant data of the KML file.
     */
    @Throws(IOException::class, XmlPullParserException::class)
    fun parseKml() {
        var eventType = parser.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                //KML defines the <kml> should only contain exactly one Document, Folder, or Placemark as direct child
                if (parser.name.matches(CONTAINER_REGEX) || parser.name.equals(PLACEMARK_TAG)) {
                    container = parseKmlContainer(parser)
                }
            }

            eventType = parser.next()
        }
    }

    /**
     * Parses KML from nested container <Document> or <Folder>
     *
     * @param parser XmlPullParser containing KML Container
     */
    private fun parseKmlContainer(parser: XmlPullParser): ContainerManager {
        //TODO(extract id from container tag)
        parser.next()
        var eventType = parser.eventType
        val containerManager = ContainerManager()

        while (!(eventType == XmlPullParser.END_TAG && parser.name.matches(CONTAINER_REGEX))) {
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.name.matches(UNSUPPORTED_REGEX)) {
                    skip(parser)
                } else if (parser.name.matches(CONTAINER_REGEX)) {
                    containerManager.addChild(parseKmlContainer(parser))
                } else if (parser.name.equals(NAME_TAG)) {
                    containerManager.setName(parser.nextText())
                } else if (parser.name.equals(PLACEMARK_TAG)) {
                    KmlPlacemarkParser.parsePlacemark(parser, containerManager)
                } else if (parser.name.equals(STYLE_MAP_TAG)) {
                    val styleMap = KmlStyleParser.parseStyleMap(parser)
                    styleMaps[styleMap.getId()] = styleMap
                } else if (parser.name.equals(STYLE_TAG)) {
                    val style = KmlStyleParser.parseStyle(parser)
                    styles[style.getId()] = style
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

    /**
     * Applies styles to container and its features
     *
     * @param images Images extracted from KMZ
     */
    suspend fun applyStyles(images: HashMap<String, Bitmap>) {
        container.setStyle(styleMaps, styles, images)
    }

    companion object {
        internal const val STYLE_TAG = "Style"
        internal const val STYLE_MAP_TAG = "StyleMap"
        internal const val PLACEMARK_TAG = "Placemark"
        internal const val NAME_TAG = "name"
        internal const val GROUND_OVERLAY_TAG = "GroundOverlay"
        internal val CONTAINER_REGEX = Regex("Folder|Document")
        internal val UNSUPPORTED_REGEX = Regex("altitude|altitudeModeGroup|altitudeMode|" +
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