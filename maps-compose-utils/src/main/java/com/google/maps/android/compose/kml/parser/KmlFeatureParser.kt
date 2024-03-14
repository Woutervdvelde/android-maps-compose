package com.google.maps.android.compose.kml.parser

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.data.Geometry
import com.google.maps.android.data.kml.KmlPlacemark
import com.google.maps.android.data.kml.KmlPoint
import com.google.maps.android.data.kml.KmlStyle
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

internal class KmlFeatureParser {
    companion object {
        /**
         * Creates a new Placemark object (created if a Placemark start tag is read by the
         * XmlPullParser and if a Geometry tag is contained within the Placemark tag)
         * and assigns specific elements read from the parser to the Placemark
         */
        fun createPlacemark(parser: XmlPullParser): KmlPlacemark {
            val styleId: String? = null
            val inlineStyle: KmlStyle? = null
            val properties: HashMap<String, String>? = HashMap()
            var geometry: Geometry<*>? = null

            var eventType = parser.eventType
            while (!(eventType == XmlPullParser.END_TAG && parser.name.equals("Placemark"))) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.name.equals(STYLE_URL_TAG)) {
                        //TODO(Handle style url tag)
                    } else if (parser.name.matches(GEOMETRY_REGEX)) {
                        geometry = createGeometry(parser, parser.name)
                    } else if (parser.name.matches(PROPERTY_REGEX)) {
                        //TODO(Handle property)
                    } else if (parser.name.equals(EXTENDED_DATA)) {
                        //TODO(Handle extended data)
                    } else if (parser.name.equals(STYLE_TAG)) {
                        //TODO(Handle style tag)
                    }
                }

                eventType = parser.next()
            }

            return KmlPlacemark(geometry, styleId, inlineStyle, properties)
        }

        /**
         * Creates a new Geometry object
         * (Created if "Point", "LineString", "Track", "Polygon", "MultiGeometry"
         * or "MultiTrack" tag is detected by the XmlPullParser)
         *
         * @param geometryType Type of geometry object to create
         * @return A geometry object of one of the supported types: KmlPoint, KmlLineString, KmlTrack,
         *  KmlPolygon, KmlMultiGeometry, or MultiTrack. Returns null if the specified geometry type is not found.
         */
        @Throws(IOException::class, XmlPullParserException::class)
        private fun createGeometry(parser: XmlPullParser, geometryType: String): Geometry<*>? {
            var eventType = parser.eventType
            while (!(eventType == XmlPullParser.END_TAG && parser.name == geometryType)) {
                if (eventType == XmlPullParser.START_TAG) {
                    when (parser.name) {
                        "Point" -> return createPoint(parser)
                        "LineString" -> return null //TODO()
                        "Track" -> return null //TODO()
                        "Polygon" -> return null //TODO()
                        "MultiGeometry" -> return null //TODO()
                        "MultiTrack" -> return null //TODO()
                    }
                }
                eventType = parser.next()
            }
            return null
        }


        /**
         * Creates a new KmlPoint object
         *
         * @param XmlPullParser parser containing Point xml
         * @return KmlPoint object with data extracted from the parser
         */
        private fun createPoint(parser: XmlPullParser): KmlPoint {
            var eventType = parser.eventType
            var latLngAlt: LatLngAlt? = null
            while (!(eventType == XmlPullParser.END_TAG && parser.name.equals("Point"))) {
                if (eventType == XmlPullParser.START_TAG && parser.name.equals("coordinates")) {
                    latLngAlt = LatLngAlt.convertToLatLngAlt(parser.nextText())
                }
                eventType = parser.next()
            }

            return KmlPoint(latLngAlt!!.latLng, latLngAlt.altitude)
        }

        private val GEOMETRY_REGEX = Regex("Point|LineString|Polygon|MultiGeometry|Track|MultiTrack")
        private val PROPERTY_REGEX = Regex("name|description|drawOrder|visibility|open|address|phoneNumber")
        private val BOUNDARY_REGEX = Regex("outerBoundaryIs|innerBoundaryIs")
        private val COMPASS_REGEX = Regex("north|south|east|west")
        private const val LONGITUDE_INDEX = 0
        private const val LATITUDE_INDEX = 1
        private const val ALTITUDE_INDEX = 2
        private const val LAT_LNG_ALT_SEPARATOR = ","
        private const val EXTENDED_DATA = "ExtendedData"
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