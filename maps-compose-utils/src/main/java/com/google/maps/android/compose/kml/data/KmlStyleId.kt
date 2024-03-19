package com.google.maps.android.compose.kml.data

public abstract class KmlStyleId {
    private var styleId: String = ""

    internal fun getId(): String = styleId

    internal fun setId(id: String): String {
        styleId = if (id.startsWith("#")) id else "#${id}"
        return id
    }
}