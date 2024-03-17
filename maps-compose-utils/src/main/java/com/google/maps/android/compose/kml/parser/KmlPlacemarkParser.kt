package com.google.maps.android.compose.kml.parser

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.kml.manager.ContainerManager
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

            while (!(eventType == XmlPullParser.END_TAG && parser.name == "Placemark")) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.name.matches(PLACEMARK_REGEX)) {
                        when (parser.name) {
                            POINT_TAG -> container.addMarker(createMarker(parser))
                            "LineString" -> return //TODO()
                            "Polygon" -> return //TODO()
                            "MultiGeometry" -> return //TODO()
                        }
                    }
                }
                eventType = parser.next()
            }
        }

        private fun createMarker(parser: XmlPullParser): MarkerManager {
            var eventType = parser.eventType
            var latLngAlt: LatLngAlt? = null
            while (!(eventType == XmlPullParser.END_TAG && parser.name.equals(POINT_TAG))) {
                if (eventType == XmlPullParser.START_TAG && parser.name.equals(COORDINATES_TAG)) {
                    latLngAlt = LatLngAlt.convertToLatLngAlt(parser.nextText())
                }
                eventType = parser.next()
            }

            return MarkerManager(latLngAlt!!.latLng)
        }

        private val PLACEMARK_REGEX = Regex("Point|LineString|Polygon|MultiGeometry")
        private val PROPERTY_REGEX = Regex("name|description|drawOrder|visibility|open|address|phoneNumber")
        private val BOUNDARY_REGEX = Regex("outerBoundaryIs|innerBoundaryIs")
        private val COMPASS_REGEX = Regex("north|south|east|west")
        private const val LONGITUDE_INDEX = 0
        private const val LATITUDE_INDEX = 1
        private const val ALTITUDE_INDEX = 2
        private const val LAT_LNG_ALT_SEPARATOR = ","
        private const val POINT_TAG = "Point"
        private const val COORDINATES_TAG = "coordinates"
        private const val EXTENDED_DATA_TAG = "ExtendedData"
        private const val STYLE_URL_TAG = "styleUrl"
        private const val STYLE_TAG = "Style"
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