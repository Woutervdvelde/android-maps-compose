package com.google.maps.android.compose.kml.data

public class KmlStyleMap: KmlStyleId() {
    private val styles: HashMap<String, String> = hashMapOf()
    private var normalStyleId: String? = null

    internal fun addNormalStyle(key: String, styleUrl: String) {
        normalStyleId = styleUrl
        addStyle(key, styleUrl)
    }

    internal fun addStyle(key: String, styleUrl: String) {
        styles[key] = styleUrl
    }

    internal fun getNormalStyleId(): String? = normalStyleId
}