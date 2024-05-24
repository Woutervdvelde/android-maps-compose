package com.google.maps.android.compose.kml.parser

import android.graphics.Bitmap
import com.google.maps.android.compose.kml.data.KmlStyle
import com.google.maps.android.compose.kml.data.KmlStyleMap
import com.google.maps.android.compose.kml.data.KmlTags.Companion.GROUND_OVERLAY_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.NAME_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.PLACEMARK_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.STYLE_MAP_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.STYLE_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.VISIBILITY_TAG
import com.google.maps.android.compose.kml.event.KmlEvent
import com.google.maps.android.compose.kml.event.KmlEventListener
import com.google.maps.android.compose.kml.event.KmlEventPublisher
import com.google.maps.android.compose.kml.manager.ContainerManager
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

/**
 * Parses a given KML file into KmlStyle, KmlPlacemark, KmlGroundOverlay and KmlContainer objects
 */
public class KmlParser(
    private val parser: XmlPullParser,
    private val styleMaps: HashMap<String, KmlStyleMap> = hashMapOf(),
    private val styles: HashMap<String, KmlStyle> = hashMapOf(),
    public var container: ContainerManager = ContainerManager(),
    public val eventPublisher: KmlEventPublisher = KmlEventPublisher(),
) : KmlEventListener {
    override fun onEvent(event: KmlEvent) {
        eventPublisher.emit(event)
    }

    /**
     * Parses the KML file stored in the current XmlPullParser.
     * Creates a ContainerManager that stores the relevant data of the KML file.
     */
    @Throws(IOException::class, XmlPullParserException::class)
    internal fun parseKml() {
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
                } else if (parser.name.equals(GROUND_OVERLAY_TAG)) {
                    KmlGroundOverlayParser.parseGroundOverlay(parser, containerManager)
                } else if (parser.name.equals(STYLE_MAP_TAG)) {
                    val styleMap = KmlStyleParser.parseStyleMap(parser)
                    styleMaps[styleMap.getId()] = styleMap
                } else if (parser.name.equals(STYLE_TAG)) {
                    val style = KmlStyleParser.parseStyle(parser)
                    styles[style.getId()] = style
                } else if (parser.name.equals(VISIBILITY_TAG)) {
                    containerManager.setActive(parser.nextText() == "1")
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
    private fun skip(parser: XmlPullParser) {
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
    internal suspend fun applyStyles(images: HashMap<String, Bitmap>) {
        container.setStyle(styleMaps, styles, images, container.getActive())
    }

    internal fun setEventListener(listener: KmlEventListener) {
        container.setEventListener(listener)
    }

    public companion object {
        internal val CONTAINER_REGEX = Regex("Folder|Document")
        internal val UNSUPPORTED_REGEX = Regex(
            "altitude|altitudeModeGroup|altitudeMode|" +
                    "begin|bottomFov|cookie|displayName|displayMode|end|expires|extrude|" +
                    "flyToView|gridOrigin|httpQuery|leftFov|linkDescription|linkName|linkSnippet|" +
                    "listItemType|maxSnippetLines|maxSessionLength|message|minAltitude|minFadeExtent|" +
                    "minLodPixels|minRefreshPeriod|maxAltitude|maxFadeExtent|maxLodPixels|maxHeight|" +
                    "maxWidth|near|NetworkLink|NetworkLinkControl|overlayXY|range|refreshMode|" +
                    "refreshInterval|refreshVisibility|rightFov|roll|rotationXY|screenXY|shape|sourceHref|" +
                    "state|targetHref|tessellate|tileSize|topFov|viewBoundScale|viewFormat|viewRefreshMode|" +
                    "viewRefreshTime|when|BalloonStyle"
        )

        internal fun convertPropertyToBoolean(
            properties: HashMap<String, Any>,
            key: String,
            defaultValue: Boolean = false
        ): Boolean {
            val value = properties[key] as? String
            return value?.let { it == "1" } ?: defaultValue
        }

        internal fun convertPropertyToFloat(
            properties: HashMap<String, Any>,
            key: String,
            defaultValue: Float = 1f
        ): Float {
            val value = properties[key] as? String
            return value?.toFloat() ?: defaultValue
        }

        /**
         * Parses an input KML/KMZ file and returns a KmlParser
         * containing all information if parsed successfully
         *
         * @param inputStream inputStream containing KML/KMZ
         * @return KmlParser on successful parse, otherwise null
         */
        public suspend fun parse(inputStream: InputStream): KmlParser? {
            val parsedKmlData = MapFileParser.parseStream(inputStream)
            parsedKmlData.parser?.let {
                it.applyStyles(parsedKmlData.media)
                it.setEventListener(it)
                return it
            }
            return null
        }
    }
}