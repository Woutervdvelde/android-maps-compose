package com.google.maps.android.compose.kml.data

internal class KmlStyleMap: KmlStyleId() {
    private val styles: HashMap<String, String> = hashMapOf()
    private var normalStyleId: String? = null

    fun addNormalStyle(key: String, styleUrl: String) {
        normalStyleId = styleUrl
        addStyle(key, styleUrl)
    }

    fun addStyle(key: String, styleUrl: String) {
        styles[key] = styleUrl
    }
}