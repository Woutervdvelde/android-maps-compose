package com.google.maps.android.compose.kml.manager

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.kml.data.KmlStyle
import com.google.maps.android.compose.kml.data.KmlStyleMap
import com.google.maps.android.compose.kml.event.KmlEvent
import com.google.maps.android.compose.kml.parser.Anchor
import com.google.maps.android.compose.kml.parser.KmlStyleParser
import com.google.maps.android.compose.rememberMarkerState
import java.io.IOException
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

public class MarkerManager(
    private val position: LatLng
) : KmlComposableManager() {
    private var markerData: MutableState<MarkerProperties> = mutableStateOf(MarkerProperties())

    override fun setProperties(data: HashMap<String, Any>) {
        markerData.value = MarkerProperties.from(data)
    }

    /**
     * Sets the styles received from the KML Parser
     *
     * @param styleMaps All StyleMap tags parsed from the KML file
     * @param styles All Style tags parsed from the KML file
     * @param images All images when present in KMZ file
     */
    public override suspend fun setStyle(
        styleMaps: HashMap<String, KmlStyleMap>,
        styles: HashMap<String, KmlStyle>,
        images: HashMap<String, Bitmap>
    ) {
        val styleUrl = markerData.value.styleUrl
        val normalStyleId = styleMaps[styleUrl]?.getNormalStyleId()
        val selectedStyle = styles[normalStyleId]

        style = selectedStyle ?: KmlStyle()
        generateIcon(images)
        applyStylesToProperties()
    }

    /**
     * Sets the visibility of the marker
     *
     * @param visible True when marker should be visible, false if not
     */
    public fun setVisibility(visible: Boolean) {
        markerData.value = markerData.value.copy(visibility = visible)
    }

    /**
     * Sets alpha of the marker, value between 0f and 1f.
     * 0f means the icon is fully transparent and  1f will make the marker fully opaque.
     *
     * @param alpha Float value between 0f and 1f
     */
    public fun setAlpha(alpha: Float) {
        markerData.value = markerData.value.copy(alpha = alpha)
    }

    /**
     * Sets rotation of the marker in degrees
     *
     * @param rotation in degrees, 0 - 360
     */
    public fun setRotation(rotation: Int) {
        markerData.value = markerData.value.copy(rotation = rotation)
    }

    /**
     * Sets icon anchor, only supports fractions
     *
     * @param anchor Anchor value
     */
    public fun setAnchor(anchor: Anchor) {
        //TODO("handle other unit types, only supports fractions at the moment")
        if (anchor.xUnit == KmlStyleParser.HOTSPOT_UNIT_FRACTION && anchor.yUnit == KmlStyleParser.HOTSPOT_UNIT_FRACTION) {
            markerData.value = markerData.value.copy(anchor = anchor)
        } else {
            markerData.value = markerData.value.copy(anchor = Anchor())
        }
    }

    /**
     * Sets marker color, also applies random color if RandomColorMode is enabled
     *
     * @param color Color as integer
     */
    public fun setColor(color: Int) {
        var finalColor = color.toFloat()
        if (style.getIconRandomColorMode()) {
            val randomColor = KmlStyleParser.computeRandomColor(color)
            finalColor = KmlStyleParser.convertIntColorToHueValue(randomColor)
        }
        markerData.value = markerData.value.copy(color = finalColor)
    }

    /**
     * Applies all available styles to properties
     */
    private fun applyStylesToProperties() {
        setAlpha(style.getIconAlpha())
        setAnchor(style.getIconAnchor())
        setRotation(style.getIconHeading())
        style.getIconColor()?.let { setColor(it.toInt()) }
    }

    private fun getIcon(bitmap: Bitmap?): BitmapDescriptor {
        return bitmap?.let {
            BitmapDescriptorFactory.fromBitmap(it)
        } ?: markerData.value.color?.let {
            BitmapDescriptorFactory.defaultMarker(it)
        } ?: BitmapDescriptorFactory.defaultMarker()
    }

    /**
     * Generates icon that can be used by the Marker composable.
     * Sets the value in markerData
     *
     * @param images All images when present in KMZ file
     */
    private suspend fun generateIcon(images: HashMap<String, Bitmap>) {
        val bitmap = getMarkerIconBitmap(images)
        bitmap?.let {
            markerData.value = markerData.value.copy(
                icon = resizeIcon(it, style.getIconScale())
            )
        }
    }

    /**
     * Tries to get the marker Icon from the images in the KMZ or through a url.
     *
     * @param images All images when present in KMZ file
     * @return Bitmap when available in HashMap or fetched via url, null otherwise
     */
    private suspend fun getMarkerIconBitmap(images: HashMap<String, Bitmap>): Bitmap? {
        val iconUrl = style.getIconUrl()
        iconUrl?.let { images[it]?.let { bitmap -> return bitmap } } //bitmap exists in parsed KMZ

        if (iconUrl != null && iconUrl.lowercase().startsWith("https")) {
            fetchIconFromUrl(iconUrl)?.let { return it }
        }

        return null
    }

    /**
     * Fetches an image from a URL and converts it to a Bitmap
     *
     * @param url Source of the image
     * @return Bitmap when the url request is successful, null otherwise
     */
    private suspend fun fetchIconFromUrl(url: String): Bitmap? {
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

    /**
     * Resizes icon bitmap based on icon aspect ratio, scale and display density
     *
     * @param icon The icon Bitmap
     * @param scale Scale that should be applied
     */
    private fun resizeIcon(icon: Bitmap, scale: Float): Bitmap {
        if (icon.height == 0 || icon.density == 0)
            return icon

        val dpi = icon.density
        val iconAspectRatio = icon.width.toFloat() / icon.height.toFloat()
        val (defaultWidth, defaultHeight) = if (iconAspectRatio < 1) {
            Pair(DEFAULT_ICON_WIDTH * iconAspectRatio, DEFAULT_ICON_HEIGHT)
        } else {
            Pair(DEFAULT_ICON_WIDTH, DEFAULT_ICON_HEIGHT / iconAspectRatio)
        }
        val scaleFactor = dpi / DEFAULT_DPI
        val width = defaultWidth * scale * scaleFactor
        val height = defaultHeight * scale * scaleFactor

        return Bitmap.createScaledBitmap(icon, width.toInt(), height.toInt(), true)
    }

    /**
     * Renders the Marker applying all its properties and styles to it
     */
    @Composable
    override fun Render() {
        val markerState = rememberMarkerState(position = position)
        val currentMarkerData = markerData.value

        Marker(
            state = markerState,
            alpha = currentMarkerData.alpha,
            anchor = Offset(currentMarkerData.anchor.x, currentMarkerData.anchor.y),
            rotation = currentMarkerData.rotation.toFloat(),
            snippet = currentMarkerData.description,
            title = currentMarkerData.name,
            visible = currentMarkerData.visibility,
            zIndex = currentMarkerData.drawOrder,
            icon = getIcon(currentMarkerData.icon),
            onClick = {
                Log.e("Marker composable", "onClick")
                listener?.onEvent(KmlEvent.Marker.Clicked(markerData.value))
                true
            }
        )
    }

    private companion object {
        const val DEFAULT_DPI: Float = 560f
        const val DEFAULT_ICON_WIDTH: Float = 110f
        const val DEFAULT_ICON_HEIGHT: Float = 110f
    }
}

/**
 * Helper data class containing all maker properties and styles
 */
public data class MarkerProperties(
    val description: String = DEFAULT_DESCRIPTION,
    val name: String = DEFAULT_NAME,
    val visibility: Boolean = DEFAULT_VISIBILITY,
    val alpha: Float = DEFAULT_ALPHA,
    val drawOrder: Float = DEFAULT_DRAW_ORDER,
    val anchor: Anchor = DEFAULT_ANCHOR,
    val rotation: Int = DEFAULT_ROTATION,
    val color: Float? = DEFAULT_COLOR,
    val styleUrl: String? = DEFAULT_STYLE_URL,
    val icon: Bitmap? = DEFAULT_ICON,
    val extendedData: HashMap<String, String>? = DEFAULT_EXTENDED_DATA
) {
    internal companion object {
        internal fun from(properties: HashMap<String, Any>): MarkerProperties {
            val description: String by properties.withDefault { DEFAULT_DESCRIPTION }
            val name: String by properties.withDefault { DEFAULT_NAME }
            val visibility: Boolean by properties.withDefault { DEFAULT_VISIBILITY }
            val drawOrder: Float by properties.withDefault { DEFAULT_DRAW_ORDER }
            val styleUrl: String? by properties
            val extendedData: HashMap<String, String>? = properties["ExtendedData"] as? HashMap<String, String>
            return MarkerProperties(
                description = description,
                name = name,
                visibility = visibility,
                alpha = DEFAULT_ALPHA,
                drawOrder = drawOrder,
                anchor = DEFAULT_ANCHOR,
                rotation = DEFAULT_ROTATION,
                color = DEFAULT_COLOR,
                styleUrl = styleUrl,
                icon = DEFAULT_ICON,
                extendedData = extendedData
            )
        }

        private const val DEFAULT_DESCRIPTION = ""
        private const val DEFAULT_NAME = ""
        private const val DEFAULT_VISIBILITY = true
        private const val DEFAULT_ALPHA = 1f
        private const val DEFAULT_DRAW_ORDER = 0f
        private val DEFAULT_ANCHOR = Anchor()
        private const val DEFAULT_ROTATION = 0
        private val DEFAULT_COLOR = null
        private const val DEFAULT_STYLE_URL = ""
        private val DEFAULT_ICON = null
        private val DEFAULT_EXTENDED_DATA = null
    }
}
