package com.google.maps.android.compose.kml.parser

import org.xmlpull.v1.XmlPullParser

internal abstract class KmlFeatureParser {
    companion object {
        /**
         * Extracts data from KML ExtendedData tag, returns a list of [ExtendedData] containing all Data tags inside the ExtendedData
         *
         * @param parser XmlPullParser containing KML ExtendedData tag
         * @return list of [ExtendedData] containing name, displayName and value from data and value tags
         */
        internal fun parseExtendedData(parser: XmlPullParser): List<ExtendedData> {
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

        internal val PROPERTY_REGEX =
            Regex("name|description|drawOrder|visibility|address|phoneNumber|styleUrl")
        internal const val EXTENDED_DATA_TAG = "ExtendedData"
        private const val DATA_TAG = "Data"
        private const val VALUE_TAG = "value"
        private const val DISPLAY_NAME_TAG = "displayName"
    }
}