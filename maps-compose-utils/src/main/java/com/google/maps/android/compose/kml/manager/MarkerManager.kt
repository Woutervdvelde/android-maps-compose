package com.google.maps.android.compose.kml.manager

import android.content.Context
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
import com.google.maps.android.compose.kml.parser.KmlStyleParser
import com.google.maps.android.compose.rememberMarkerState
import java.io.IOException
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

public class MarkerManager(
    private val position: LatLng
) : KmlComposableManager {
    private var markerData: MutableState<MarkerProperties> = mutableStateOf(MarkerProperties())
    public override var style: KmlStyle = KmlStyle()

    public override fun setProperties(data: HashMap<String, Any>) {
        markerData.value = MarkerProperties.from(data)
    }

    public override suspend fun setStyle(
        styleMaps: HashMap<String, KmlStyleMap>,
        styles: HashMap<String, KmlStyle>,
        images: HashMap<String, Bitmap>,
        context: Context
    ) {
        val styleUrl = markerData.value.styleUrl
        val normalStyleId = styleMaps[styleUrl]?.getNormalStyleId()
        val selectedStyle = styles[normalStyleId]

        style = selectedStyle ?: KmlStyle()
        generateIcon(images, context)
    }

    public fun setVisibility(visible: Boolean) {
        markerData.value = markerData.value.copy(visibility = visible)
    }

    /**
     *
     */
    private suspend fun generateIcon(images: HashMap<String, Bitmap>, context: Context) {
        val bitmap = getMarkerIconBitmap(images)

        val bitmapDescriptor = bitmap?.let {
            BitmapDescriptorFactory.fromBitmap(resizeIcon(it, style.getIconScale(), context))
        } ?: style.getIconColor()?.let {
            if (style.getIconRandomColorMode()) {
                val color = KmlStyleParser.computeRandomColor(it.toInt())
                BitmapDescriptorFactory.defaultMarker(KmlStyleParser.convertIntColorToHueValue(color))
            } else {
                BitmapDescriptorFactory.defaultMarker(it)
            }
        } ?: BitmapDescriptorFactory.defaultMarker()

        markerData.value = markerData.value.copy(icon = bitmapDescriptor)
    }

    private suspend fun getMarkerIconBitmap(images: HashMap<String, Bitmap>): Bitmap? {
        val iconUrl = style.getIconUrl()
        iconUrl?.let { images[it]?.let { bitmap -> return bitmap } } //bitmap exists in parsed KMZ

        if (iconUrl != null && iconUrl.lowercase().startsWith("http")) {
            fetchIconFromUrl(iconUrl)?.let { return it }
        }

        return null
    }

    private suspend fun fetchIconFromUrl(url: String): Bitmap? { //TODO(Test with non image url for errors)
        return suspendCoroutine { continuation ->
            try {
                val inputStream = URL(url).openConnection().getInputStream()
                continuation.resume(BitmapFactory.decodeStream(inputStream))
            } catch (e: IOException) {
                e.printStackTrace()
                continuation.resume(null)
            }
        }
    }

    private fun resizeIcon(icon: Bitmap, scale: Float, context: Context): Bitmap {
        if (icon.height == 0 || icon.density == 0)
            return icon

        val dpi = context.resources.displayMetrics.densityDpi
        val iconAspectRatio = icon.width.toFloat() / icon.height.toFloat()
        val (defaultWidth, defaultHeight) = if (iconAspectRatio < 1) {
            Pair(DEFAULT_ICON_WIDTH * iconAspectRatio, DEFAULT_ICON_HEIGHT)
        } else {
            Pair(DEFAULT_ICON_WIDTH, DEFAULT_ICON_HEIGHT / iconAspectRatio)
        }
        val width = defaultWidth * scale * (dpi / icon.density)
        val height = defaultHeight * scale * (dpi / icon.density)
        return Bitmap.createScaledBitmap(icon, width.toInt(), height.toInt(), true)
    }

    @Composable
    override fun Render() {
        val markerState = rememberMarkerState(position = position)
        val currentMarkerData = markerData.value

        Marker(
            state = markerState,
            snippet = currentMarkerData.description,
            title = currentMarkerData.name,
            visible = currentMarkerData.visibility,
            zIndex = currentMarkerData.drawOrder,
            icon = currentMarkerData.icon,
        )
    }

    private companion object {
        const val DEFAULT_ICON_WIDTH: Float = 110f
        const val DEFAULT_ICON_HEIGHT: Float = 110f
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