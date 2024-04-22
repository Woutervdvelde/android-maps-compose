package com.google.maps.android.compose.kml.parser

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.kml.manager.ContainerManager
import com.google.maps.android.compose.kml.manager.KmlComposableManager
import com.google.maps.android.compose.kml.manager.MarkerManager
import com.google.maps.android.compose.kml.manager.PolylineManager
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

internal class KmlPlacemarkParser: KmlFeatureParser() {
    companion object {
        /**
         * Parses a Placemark KML tag from the XmlPullParser and adds it to the provided container
         *
         * @param parser XmlPullParser containing KML Placemark
         * @param container the ContainerManager the placemark will be added to
         */
        @Throws(IOException::class, XmlPullParserException::class)
        fun parsePlacemark(parser: XmlPullParser, container: ContainerManager) {
            var eventType = parser.eventType
            val properties: HashMap<String, Any> = hashMapOf()
            val extendedData: MutableList<ExtendedData> = mutableListOf()
            var placemark: KmlComposableManager? = null

            while (!(eventType == XmlPullParser.END_TAG && parser.name == KmlParser.PLACEMARK_TAG)) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.name.matches(PROPERTY_REGEX)) {
                        properties[parser.name] = parser.nextText()
                    } else if (parser.name.equals(POINT_TAG)) {
                        placemark = createMarker(parser)
                    } else if (parser.name.equals(LINE_STRING_TAG)) {
                        val (manager, data) = createPolyline(parser)
                        placemark = manager
                        properties.putAll(data)
                    } else if (parser.name.equals(POLYGON_TAG)) {
                        //TODO()
                    } else if (parser.name.equals(MUTLI_GEOMETRY_TAG)) {
                        //TODO()
                    } else if (parser.name.equals(EXTENDED_DATA_TAG)) {
                        extendedData.addAll(parseExtendedData(parser))
                    }
                }
                eventType = parser.next()
            }

            if (extendedData.isNotEmpty())
                properties[EXTENDED_DATA_TAG] = extendedData.toList()

            placemark?.let {
                it.setProperties(properties)
                container.addChild(it)
            }
        }

        /**
         * Creates a MarkerManager based on the given KML data, coordinates will be extracted from the <point> tag
         *
         * @param parser XmlPullParser containing KML Point tag
         * @return MarkerManager containing the point position tag data
         */
        private fun createMarker(parser: XmlPullParser): MarkerManager {
            var eventType = parser.eventType
            var position: LatLng? = null
            while (!(eventType == XmlPullParser.END_TAG && parser.name.equals(POINT_TAG))) {
                if (eventType == XmlPullParser.START_TAG && parser.name.equals(COORDINATES_TAG)) {
                    position = parseCoordinates(parser.nextText())[0]
                }
                eventType = parser.next()
            }

            if (position == null) {
                throw IllegalArgumentException("KML doesn't contain coordinates for placemark point")
            }

            return MarkerManager(position)
        }

        /**
         * Creates a PolylineManager based on the given KML data
         *
         * @param parser XmlPullParser containing KML Linestring tag
         * @return PolylineManager containing the lines position data
         */
        private fun createPolyline(parser: XmlPullParser): Pair<PolylineManager, HashMap<String, Any>> {
            var eventType = parser.eventType
            var coordinates: List<LatLng>? = null
            val properties: HashMap<String, Any> = hashMapOf()

            while (!(eventType == XmlPullParser.END_TAG && parser.name.equals(LINE_STRING_TAG))) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.name.matches(PROPERTY_REGEX)) {
                        properties[parser.name] = parser.nextText()
                    } else if (parser.name.equals(COORDINATES_TAG)) {
                        coordinates = parseCoordinates(parser.nextText())
                    }
                }
                eventType = parser.next()
            }

            if (coordinates == null) {
                throw IllegalArgumentException("KML doesn't contain coordinates for placemark linestring")
            }

            return Pair(
                PolylineManager(coordinates),
                properties
            )
        }

        /**
         *
         */
        private fun parseCoordinates(input: String): List<LatLng> {
            return input.trim().split("\n").map {
                Log.e("TAG", it)
                val coordinate = it.split(LAT_LNG_ALT_SEPARATOR)
                val lat = coordinate[LATITUDE_INDEX].toDouble()
                val lng = coordinate[LONGITUDE_INDEX].toDouble()
                LatLng(lat, lng)
            }
        }


        private val BOUNDARY_REGEX = Regex("outerBoundaryIs|innerBoundaryIs")
        private const val LONGITUDE_INDEX = 0
        private const val LATITUDE_INDEX = 1
        private const val LAT_LNG_ALT_SEPARATOR = ","
        private const val POINT_TAG = "Point"
        private const val LINE_STRING_TAG = "LineString"
        private const val POLYGON_TAG = "Polygon"
        private const val MUTLI_GEOMETRY_TAG = "MultiGeometry"
        private const val COORDINATES_TAG = "coordinates"
    }
}

public data class ExtendedData(
    var name: String,
    var displayName: String?,
    var value: String
) {
    internal companion object {
        fun empty(): ExtendedData = ExtendedData(name = "", displayName = null, value = "")
    }
}