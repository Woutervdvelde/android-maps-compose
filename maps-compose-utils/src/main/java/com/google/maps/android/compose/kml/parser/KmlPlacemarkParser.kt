package com.google.maps.android.compose.kml.parser

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.kml.manager.ContainerManager
import com.google.maps.android.compose.kml.manager.KmlComposableManager
import com.google.maps.android.compose.kml.manager.MarkerManager
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

internal class KmlPlacemarkParser {
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

            while (!(eventType == XmlPullParser.END_TAG && parser.name == "Placemark")) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.name.matches(PROPERTY_REGEX)) {
                        properties[parser.name] = parser.nextText()
                    } else if (parser.name.equals(POINT_TAG)) {
                        placemark = createMarker(parser)
                        container.addChild(placemark)
                    } else if (parser.name.equals(LINE_STRING_TAG)) {
                        //TODO()
                    } else if (parser.name.equals(POLYGON_TAG)) {
                        //TODO()
                    } else if (parser.name.equals(MUTLI_GEOMETRY_TAG)) {
                        //TODO()
                    } else if (parser.name.equals(EXTENDED_DATA_TAG)) {
                        val parsedData = parseExtendedData(parser)
                        extendedData.addAll(parsedData)
                    }
                }
                eventType = parser.next()
            }

            if (extendedData.isNotEmpty())
                properties[EXTENDED_DATA_TAG] = extendedData.toList()
            placemark?.setProperties(properties)
        }

        /**
         * Creates a MarkerManager based on the given KML data, coordinates will be extracted from the <point> tag
         *
         * @param parser XmlPullParser containing KML Point tag
         */
        private fun createMarker(parser: XmlPullParser): MarkerManager {
            var eventType = parser.eventType
            var latLngAlt: LatLngAlt? = null
            while (!(eventType == XmlPullParser.END_TAG && parser.name.equals(POINT_TAG))) {
                if (eventType == XmlPullParser.START_TAG && parser.name.equals(COORDINATES_TAG)) {
                    latLngAlt = LatLngAlt.convertToLatLngAlt(parser.nextText())
                }
                eventType = parser.next()
            }

            if (latLngAlt == null) {
                throw IllegalArgumentException("KML doesn't contain coordinates for placemark point")
            }

            return MarkerManager(latLngAlt.latLng)
        }

        /**
         *
         */
        private fun parseExtendedData(parser: XmlPullParser): List<ExtendedData> {
            val extendedData: MutableList<ExtendedData> = mutableListOf()
            var currentData = ExtendedData.empty()
            var eventType = parser.eventType

            while (!(eventType == XmlPullParser.END_TAG && parser.name.equals(EXTENDED_DATA_TAG))) {
                if (eventType == XmlPullParser.START_TAG) {
                    when (parser.name) {
                        DATA_TAG ->
                            currentData.name = parser.getAttributeValue(null, "name")

                        DISPLAY_NAME_TAG ->
                            currentData.displayName = parser.nextText()

                        VALUE_TAG -> {
                            currentData.value = parser.nextText()
                            extendedData.add(currentData)
                            currentData = ExtendedData.empty()
                        }
                    }
                }
                eventType = parser.next()
            }

            return extendedData
        }

        private val PROPERTY_REGEX =
            Regex("name|description|drawOrder|visibility|address|phoneNumber|styleUrl")
        private val BOUNDARY_REGEX = Regex("outerBoundaryIs|innerBoundaryIs")
        private val COMPASS_REGEX = Regex("north|south|east|west")
        private const val LONGITUDE_INDEX = 0
        private const val LATITUDE_INDEX = 1
        private const val ALTITUDE_INDEX = 2
        private const val LAT_LNG_ALT_SEPARATOR = ","
        private const val POINT_TAG = "Point"
        private const val LINE_STRING_TAG = "LineString"
        private const val POLYGON_TAG = "Polygon"
        private const val MUTLI_GEOMETRY_TAG = "MultiGeometry"
        private const val COORDINATES_TAG = "coordinates"
        private const val EXTENDED_DATA_TAG = "ExtendedData"
        private const val DATA_TAG = "Data"
        private const val VALUE_TAG = "value"
        private const val DISPLAY_NAME_TAG = "displayName"
    }

    /**
     * Internal helper class to store latLng and altitude in a single object.
     * This allows to parse [lon,lat,altitude] tuples in KML files more efficiently.
     */
    private data class LatLngAlt(
        val latLng: LatLng,
        val altitude: Double?
    ) {
        companion object {
            /**
             * Convert a string coordinate from a string into a LatLngAlt object
             *
             * @param coordinateString  coordinate string to convert from
             * @param separator         separator to use when splitting coordinates
             * @return LatLngAlt object created from given coordinate string
             */
            fun convertToLatLngAlt(
                coordinateString: String,
                separator: String = LAT_LNG_ALT_SEPARATOR
            ): LatLngAlt {
                val coordinate = coordinateString.split(separator)
                val lat = coordinate[LATITUDE_INDEX].toDouble()
                val lng = coordinate[LONGITUDE_INDEX].toDouble()
                val alt =
                    if (coordinate.size > 2) coordinate[ALTITUDE_INDEX].toDouble() else null

                val latLng = LatLng(lat, lng)
                return LatLngAlt(latLng, alt)
            }
        }
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