package com.google.maps.android.compose.kml.manager

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.maps.android.compose.kml.data.KmlStyle
import com.google.maps.android.compose.kml.data.KmlStyleMap
import com.google.maps.android.compose.kml.event.KmlEventListener
import java.io.IOException
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

public abstract class KmlComposableManager<T : IKmlComposableProperties> {
    internal var style: KmlStyle = KmlStyle()
    internal var listener: KmlEventListener? = null
    internal var isActive: MutableState<Boolean> = mutableStateOf(true)

    internal abstract val _properties: MutableState<T>
    public val properties: T get() = _properties.value

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
        images: HashMap<String, Bitmap>,
        parentVisibility: Boolean
    )

    internal abstract fun setProperties(data: HashMap<String, Any>)

    internal open fun setEventListener(eventListener: KmlEventListener) {
        listener = eventListener
    }

    /**
     * Sets the KmlComposableManger active or inactive
     *
     * @param active true if KmlComposableManager should be visible
     */
    internal open fun setActive(active: Boolean) {
        this.isActive.value = active
    }

    /**
     * Retrieves the images from the provided url.
     * Searches in the provided hashmap, checks for HTTPS url and fetches the images if necessary.
     */
    internal suspend fun getBitmap(url: String, images: HashMap<String, Bitmap>): Bitmap? {
        images[url]?.let { return it }

        if (url.lowercase().startsWith("https")) {
            fetchImageFromUrl(url)?.let { return it }
        } else if (url.lowercase().startsWith("http")) {
            Log.w(
                "KML fetchBitmap",
                "http is not supported, use https instead"
            )
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
}