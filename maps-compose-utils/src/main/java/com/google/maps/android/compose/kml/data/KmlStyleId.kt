package com.google.maps.android.compose.kml.data

internal abstract class KmlStyleId {
    private var styleId: String = ""

    fun getId(): String = styleId

    fun setId(id: String): String {
        styleId = if (id.startsWith("#")) id else "#${id}"
        return id
    }
}