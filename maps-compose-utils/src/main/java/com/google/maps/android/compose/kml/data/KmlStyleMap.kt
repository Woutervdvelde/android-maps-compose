package com.google.maps.android.compose.kml.data

public class KmlStyleMap: KmlStyleId() {
    private val styles: HashMap<String, String> = hashMapOf()
    private var normalStyleId: String? = null

    internal fun getNormalStyleId(): String? = normalStyleId

    /**
     * Adds a style to the StyleMap and makes sure it is set as the normal style.
     */
    internal fun addNormalStyle(key: String, styleUrl: String) {
        normalStyleId = styleUrl
        addStyle(key, styleUrl)
    }

    /**
     * Adds a style to the StyleMap. Using an key (commonly "normal" and "highlight") and a styleUrl
     */
    internal fun addStyle(key: String, styleUrl: String) {
        styles[key] = styleUrl
    }
}