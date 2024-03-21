package com.google.maps.android.compose.kml.data

public abstract class KmlStyleId {
    private var styleId: String = ""

    internal fun getId(): String = styleId

    /**
     * Sets id, making sure there is consistently a single # as prefix
     *
     * @return id with # as prefix
     */
    internal fun setId(id: String): String {
        styleId = if (id.startsWith("#")) id else "#${id}"
        return id
    }
}