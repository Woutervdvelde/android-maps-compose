package com.google.maps.android.compose.kml.manager

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import com.google.maps.android.compose.kml.data.KmlStyle
import com.google.maps.android.compose.kml.data.KmlStyleMap
import com.google.maps.android.compose.kml.event.KmlEventListener
import com.google.maps.android.compose.kml.parser.Anchor
import java.io.IOException
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

public abstract class KmlComposableManager {
    internal var style: KmlStyle = KmlStyle()
    internal var listener: KmlEventListener? = null

    /**
     * Sets the styles received from the KML Parser
     *
     * @param styleMaps All StyleMap tags parsed from the KML file
     * @param styles All Style tags parsed from the KML file
     * @param images All images when present in KMZ file
     */
    internal abstract suspend fun setStyle(
        styleMaps: HashMap<String, KmlStyleMap>,
        styles: HashMap<String, KmlStyle>,
        images: HashMap<String, Bitmap>
    )

    internal abstract fun setProperties(data: HashMap<String, Any>)

    internal open fun setEventListener(eventListener: KmlEventListener) {
        listener = eventListener
    }

    /**
     * Retrieves the images from the provided url.
     * Searches in the provided hashmap, checks for HTTPS url and fetches the images if necessary.
     */
    internal suspend fun getBitmap(url: String, images: HashMap<String, Bitmap>): Bitmap? {
        images[url]?.let { return it }

        if (url.lowercase().startsWith("https")) {
            fetchImageFromUrl(url)?.let { return it }
        }

        return null
    }

    /**
     * Fetches an image from a URL and converts it to a Bitmap
     *
     * @param url Source of the image
     * @return Bitmap when the url request is successful, null otherwise
     */
    private suspend fun fetchImageFromUrl(url: String): Bitmap? {
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

    @Composable
    internal abstract fun Render()

    internal companion object {
        internal const val DEFAULT_DESCRIPTION = ""
        internal const val DEFAULT_NAME = ""
        internal const val DEFAULT_VISIBILITY = true
        internal const val DEFAULT_ALPHA = 1f
        internal const val DEFAULT_DRAW_ORDER = 0f
        internal val DEFAULT_ANCHOR = Anchor()
        internal const val DEFAULT_ROTATION = 0
        internal val DEFAULT_COLOR = null
        internal const val DEFAULT_STYLE_URL = ""
        internal val DEFAULT_ICON = null
        internal val DEFAULT_EXTENDED_DATA = null
    }
}