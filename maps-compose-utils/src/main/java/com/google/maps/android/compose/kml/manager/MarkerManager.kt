package com.google.maps.android.compose.kml.manager

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.kml.data.KmlStyle
import com.google.maps.android.compose.kml.data.KmlStyleMap
import com.google.maps.android.compose.rememberMarkerState

public class MarkerManager (
    private val position: LatLng
): KmlComposableManager {
    public lateinit var markerData: MarkerProperties
    public override var style: KmlStyle? = null

    public override fun setProperties(data: HashMap<String, Any>) {
        markerData = MarkerProperties.from(data)
    }

    public override fun setStyle(
        styleMaps: HashMap<String, KmlStyleMap>,
        styles: HashMap<String, KmlStyle>
    ) {
        style = styles[styleMaps[markerData.styleUrl]?.getNormalStyleId()]
    }

    public fun getPosition(): LatLng = position

    @Composable
    override fun Render(images: HashMap<String, Bitmap>) {
        style?.getIconUrl()?.let { iconUrl ->
            images[iconUrl]?.let {
                markerData.icon = BitmapDescriptorFactory.fromBitmap(it)
            }
        }
        val markerState = rememberMarkerState(position = position)
        Marker(
            state = markerState,
            snippet = markerData.description,
            title = markerData.name,
            visible = markerData.visibility,
            zIndex = markerData.drawOrder,
            icon = markerData.icon,
        )
    }
}

public data class MarkerProperties(
    val description: String = DEFAULT_DESCRIPTION,
    val name: String = DEFAULT_NAME,
    val visibility: Boolean = DEFAULT_VISIBILITY,
    val drawOrder: Float = DEFAULT_DRAW_ORDER,
    var icon: BitmapDescriptor = BitmapDescriptorFactory.defaultMarker(),
    val styleUrl: String = ""
) {
    public companion object {
        internal fun from(properties: HashMap<String, Any>): MarkerProperties {
            val description: String by properties.withDefault { DEFAULT_DESCRIPTION }
            val name: String by properties.withDefault { DEFAULT_NAME }
            val visibility: Boolean by properties.withDefault { DEFAULT_VISIBILITY }
            val drawOrder: Float by properties.withDefault { DEFAULT_DRAW_ORDER }
            val styleUrl: String by properties.withDefault { "" }
            return MarkerProperties(description, name, visibility, drawOrder, styleUrl = styleUrl)
        }

        private const val DEFAULT_DESCRIPTION = ""
        private const val DEFAULT_NAME = ""
        private const val DEFAULT_VISIBILITY = true
        private const val DEFAULT_DRAW_ORDER = 0f
    }
}