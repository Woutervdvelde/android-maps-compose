package com.google.maps.android.compose.kml.manager

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.kml.data.KmlStyle
import com.google.maps.android.compose.kml.data.KmlStyleMap
import com.google.maps.android.compose.rememberMarkerState
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

public class MarkerManager(
    private val position: LatLng
) : KmlComposableManager {
    public var markerData: MutableState<MarkerProperties> = mutableStateOf(MarkerProperties())
    public override var style: KmlStyle = KmlStyle()

    public override fun setProperties(data: HashMap<String, Any>) {
        markerData.value = MarkerProperties.from(data)
    }

    public override suspend fun setStyle(
        styleMaps: HashMap<String, KmlStyleMap>,
        styles: HashMap<String, KmlStyle>,
        images: HashMap<String, Bitmap>
    ) {
        styles[styleMaps[markerData.value?.styleUrl]?.getNormalStyleId()]?.let {
            style = it
            generateIcon(images)
        }
    }

    public fun getPosition(): LatLng = position

    private suspend fun generateIcon(images: HashMap<String, Bitmap>) {
        style.getIconUrl()?.let { iconUrl ->
            images[iconUrl]?.let { bitmap -> //is defined in KMZ image files
                markerData.value = markerData.value.copy(icon =
                    BitmapDescriptorFactory.fromBitmap(
                        resizeIcon(
                            bitmap,
                            style.getIconScale()
                        )
                    ))
            }
            ?: run {//TODO("check if url is actual HTTP url")
                markerData.value = markerData.value.copy(icon = suspendCoroutine { continuation -> //fetch image from HTTP url
                    val url = URL(iconUrl)
                    val icon = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    val resizedIcon = resizeIcon(icon, style.getIconScale())

                    continuation.resume(BitmapDescriptorFactory.fromBitmap(resizedIcon))
                })
            }
        }
    }

    private fun resizeIcon(icon: Bitmap, scale: Float): Bitmap {
        val width = DEFAULT_ICON_WIDTH * scale
        val height = DEFAULT_ICON_HEIGHT * scale
        return Bitmap.createScaledBitmap(icon, width.toInt(), height.toInt(), true)
    }

    @Composable
    override fun Render() {
        val markerState = rememberMarkerState(position = position)

        Marker(
            state = markerState,
            snippet = markerData.value.description,
            title = markerData.value.name,
            visible = markerData.value.visibility,
            zIndex = markerData.value.drawOrder,
            icon = markerData.value.icon,
        )

    }

    private companion object {
        private const val DEFAULT_ICON_WIDTH = 110
        private const val DEFAULT_ICON_HEIGHT = 110
        //TODO(aspect ratio)
    }
}

public data class MarkerProperties(
    val description: String = DEFAULT_DESCRIPTION,
    val name: String = DEFAULT_NAME,
    val visibility: Boolean = DEFAULT_VISIBILITY,
    val drawOrder: Float = DEFAULT_DRAW_ORDER,
    val styleUrl: String? = DEFAULT_STYLE_URL,
    var icon: BitmapDescriptor = BitmapDescriptorFactory.defaultMarker(),
) {
    public companion object {
        internal fun from(properties: HashMap<String, Any>): MarkerProperties {
            val description: String by properties.withDefault { DEFAULT_DESCRIPTION }
            val name: String by properties.withDefault { DEFAULT_NAME }
            val visibility: Boolean by properties.withDefault { DEFAULT_VISIBILITY }
            val drawOrder: Float by properties.withDefault { DEFAULT_DRAW_ORDER }
            val styleUrl: String? by properties
            return MarkerProperties(description, name, visibility, drawOrder, styleUrl)
        }

        private const val DEFAULT_DESCRIPTION = ""
        private const val DEFAULT_NAME = ""
        private const val DEFAULT_VISIBILITY = true
        private const val DEFAULT_DRAW_ORDER = 0f
        private const val DEFAULT_STYLE_URL = ""
    }
}