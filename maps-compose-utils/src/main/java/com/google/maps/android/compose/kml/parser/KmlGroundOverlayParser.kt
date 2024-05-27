package com.google.maps.android.compose.kml.parser

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.kml.data.KmlTags.Companion.EAST_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.EXTENDED_DATA_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.GROUND_OVERLAY_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.HREF_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.ICON_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.NORTH_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.ROTATION_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.SOUTH_TAG
import com.google.maps.android.compose.kml.data.KmlTags.Companion.WEST_TAG
import com.google.maps.android.compose.kml.manager.ContainerManager
import com.google.maps.android.compose.kml.manager.GroundOverlayManager
import com.google.maps.android.compose.kml.manager.KmlComposableManager
import com.google.maps.android.compose.kml.manager.KmlComposableProperties
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

            while (!(eventType == XmlPullParser.END_TAG && parser.name == GROUND_OVERLAY_TAG)) {
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

            container.addChild(groundOverlay as KmlComposableManager<KmlComposableProperties>)
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
                    if (parser.name.equals(HREF_TAG)) {
                        properties[HREF_TAG] = parser.nextText()
                    }
                }

                eventType = parser.next()
            }
        }


        private val COMPASS_REGEX = Regex("$NORTH_TAG|$EAST_TAG|$SOUTH_TAG|$WEST_TAG|$ROTATION_TAG")
    }
}