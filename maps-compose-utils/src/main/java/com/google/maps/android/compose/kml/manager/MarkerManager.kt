package com.google.maps.android.compose.kml.manager

import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState

public class MarkerManager (
    private val position: LatLng
): KmlComposableManager {
    private var markerData: MarkerProperties = MarkerProperties()

    public override fun setProperties(data: HashMap<String, Any>) {
        markerData = MarkerProperties.from(data)
    }
    public fun getPosition(): LatLng = position

    @Composable
    override fun Render() {
        val markerState = rememberMarkerState(position = position)
        Marker(
            state = markerState,
            snippet = markerData.description,
            title = markerData.name,
            visible = markerData.visibility,
            zIndex = markerData.drawOrder
        )
    }
}

internal data class MarkerProperties(
    val description: String = DEFAULT_DESCRIPTION,
    val name: String = DEFAULT_NAME,
    val visibility: Boolean = DEFAULT_VISIBILITY,
    val drawOrder: Float = DEFAULT_DRAW_ORDER
) {
    companion object {
        fun from(properties: HashMap<String, Any>): MarkerProperties {
            val description: String by properties.withDefault { DEFAULT_DESCRIPTION }
            val name: String by properties.withDefault { DEFAULT_NAME }
            val visibility: Boolean by properties.withDefault { DEFAULT_VISIBILITY }
            val drawOrder: Float by properties.withDefault { DEFAULT_DRAW_ORDER }
            return MarkerProperties(description, name, visibility, drawOrder)
        }

        private const val DEFAULT_DESCRIPTION = ""
        private const val DEFAULT_NAME = ""
        private const val DEFAULT_VISIBILITY = true
        private const val DEFAULT_DRAW_ORDER = 0f
    }
}