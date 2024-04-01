package com.google.maps.android.compose.kml.manager

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import com.google.maps.android.compose.kml.data.KmlStyle
import com.google.maps.android.compose.kml.data.KmlStyleMap
import com.google.maps.android.compose.kml.event.KmlEventListener

public abstract class KmlComposableManager {
    internal var style: KmlStyle = KmlStyle()
    internal var listener: KmlEventListener? = null

    internal abstract suspend fun setStyle(
        styleMaps: HashMap<String, KmlStyleMap>,
        styles: HashMap<String, KmlStyle>,
        images: HashMap<String, Bitmap>
    )

    internal abstract fun setProperties(data: HashMap<String, Any>)

    internal open fun setEventListener(eventListener: KmlEventListener) {
        listener = eventListener
    }

    @Composable
    internal abstract fun Render()
}