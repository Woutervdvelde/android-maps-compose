package com.google.maps.android.compose.kml.manager

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import com.google.maps.android.compose.kml.data.KmlStyle
import com.google.maps.android.compose.kml.data.KmlStyleMap

public class ContainerManager() : KmlComposableManager {
    private var containerName: String = ""
    override var style: KmlStyle = KmlStyle()
    private val containers: MutableList<ContainerManager> = mutableListOf()
    private val markers: MutableList<MarkerManager> = mutableListOf()

    public fun getName(): String = containerName
    public fun getContainers(): List<ContainerManager> = containers
    public fun getMarkers(): List<MarkerManager> = markers

    override fun setProperties(data: HashMap<String, Any>) {
        //TODO()
    }

    override suspend fun setStyle(
        styleMaps: HashMap<String, KmlStyleMap>,
        styles: HashMap<String, KmlStyle>,
        images: HashMap<String, Bitmap>,
        context: Context
    ) {
        containers.forEach { it.setStyle(styleMaps, styles, images, context) }
        markers.forEach { it.setStyle(styleMaps, styles, images, context) }
    }

    public fun setName(name: String) {
        containerName = name
    }

    public fun addContainer(container: ContainerManager) {
        containers.add(container)
    }

    public fun addMarker(marker: MarkerManager) {
        markers.add(marker)
    }

    @Composable
    override fun Render() {
        markers.forEach { it.Render() }
        containers.forEach { it.Render() }
    }
}