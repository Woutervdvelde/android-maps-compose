package com.google.maps.android.compose.kml.parser

import com.google.maps.android.data.Geometry
import com.google.maps.android.data.kml.KmlPlacemark
import com.google.maps.android.data.kml.KmlStyle
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

internal class KmlFeatureParser {
    companion object {
        fun createPlacemark(parser: XmlPullParser): KmlPlacemark {
            val styleId: String? = null
            val inlineStyle: KmlStyle? = null
            val properties: HashMap<String, String>? = HashMap()
            val geometry: Geometry<Any>? = null

            var eventType = parser.eventType
            while (!(eventType == XmlPullParser.END_TAG && parser.name.equals("Placemark"))) {

            }

            return KmlPlacemark(geometry, styleId, inlineStyle, properties)
        }

        @Throws(IOException::class, XmlPullParserException::class)
        private fun createGeometry(parser: XmlPullParser, geometryType: String): Geometry<*>? {
            var eventType = parser.eventType
            while (!(eventType == XmlPullParser.END_TAG && parser.name == geometryType)) {
                if (eventType == XmlPullParser.START_TAG) {
//                    if (parser.name == "Point") {
//                        return KmlFeatureParser.createPoint(parser)
//                    } else if (parser.name == "LineString") {
//                        return KmlFeatureParser.createLineString(parser)
//                    } else if (parser.name == "Track") {
//                        return KmlFeatureParser.createTrack(parser)
//                    } else if (parser.name == "Polygon") {
//                        return KmlFeatureParser.createPolygon(parser)
//                    } else if (parser.name == "MultiGeometry") {
//                        return KmlFeatureParser.createMultiGeometry(parser)
//                    } else if (parser.name == "MultiTrack") {
//                        return KmlFeatureParser.createMultiTrack(parser)
//                    }
                }
                eventType = parser.next()
            }
            return null
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
}