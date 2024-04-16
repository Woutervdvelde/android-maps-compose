package com.google.maps.android.compose.kml.parser

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.kml.manager.ContainerManager
import com.google.maps.android.compose.kml.manager.GroundOverlayManager
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

internal class KmlGroundOverlayParser: KmlFeatureParser() {
    companion object {
        /**
         * Parses a GroundOverlay KML tag from the XmlPullParser and adds it to the provided container
         *
         * @param parser XmlPullParser containing KML Placemark
         * @param container the ContainerManager the placemark will be added to
         */
        @Throws(IOException::class, XmlPullParserException::class)
        fun parseGroundOverlay(parser: XmlPullParser, container: ContainerManager) {
            var eventType = parser.eventType
            val properties: HashMap<String, Any> = hashMapOf()
            val extendedData: MutableList<ExtendedData> = mutableListOf()
            val compass: HashMap<String, Double> = hashMapOf()
            val groundOverlay: GroundOverlayManager = GroundOverlayManager()

            while (!(eventType == XmlPullParser.END_TAG && parser.name == KmlParser.GROUND_OVERLAY_TAG)) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.name.matches(PROPERTY_REGEX)) {
                        properties[parser.name] = parser.nextText()
                    } else if (parser.name.equals(EXTENDED_DATA_TAG)) {
                        extendedData.addAll(parseExtendedData(parser))
                    } else if (parser.name.matches(COMPASS_REGEX)) {
                        compass[parser.name] = parser.nextText().toDouble()
                    } else if (parser.name.equals(ICON_TAG)) {
                        parseGroundOverlayIcon(parser, properties)
                    }
                }
                eventType = parser.next()
            }

            if (extendedData.isNotEmpty())
                properties[EXTENDED_DATA_TAG] = extendedData.toList()
            compass[ROTATION_TAG]?.let { properties[ROTATION_TAG] = it }

            groundOverlay.setProperties(properties)
            groundOverlay.setCompass(LatLngBounds(
                LatLng(compass[SOUTH_TAG]!!, compass[WEST_TAG]!!),
                LatLng(compass[NORTH_TAG]!!, compass[EAST_TAG]!!)
            ))

            container.addChild(groundOverlay)
        }

        /**
         * Parses the Icon tag of a GroundOverlay. Places the information in the provided properties hashmap
         *
         * @param parser XmlPullParser containing KML Icon tag
         * @param properties Hashmap the information will be placed in
         */
        private fun parseGroundOverlayIcon(parser: XmlPullParser, properties: HashMap<String, Any>) {
            var eventType = parser.eventType

            while (!(eventType == XmlPullParser.END_TAG && parser.name.equals(ICON_TAG))) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.name.equals(ICON_HREF_TAG)) {
                        properties[ICON_HREF_TAG] = parser.nextText()
                    }
                }

                eventType = parser.next()
            }
        }

        private const val NORTH_TAG = "north"
        private const val EAST_TAG = "east"
        private const val SOUTH_TAG = "south"
        private const val WEST_TAG = "west"
        private const val ROTATION_TAG = "rotation"
        private val COMPASS_REGEX = Regex("$NORTH_TAG|$EAST_TAG|$SOUTH_TAG|$WEST_TAG|$ROTATION_TAG")
        private const val ICON_TAG = "Icon"
        private const val ICON_HREF_TAG = "href"
    }
}