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

    /**
     * Gets a list of containers that are direct children of this ContainerManager
     *
     * @return List of [ContainerManager]s
     */
    public fun getContainers(): List<ContainerManager> = containers

    /**
     * Gets a list of markers that are direct children of this ContainerManager
     * @return List of [MarkerManager]s
     */
    public fun getMarkers(): List<MarkerManager> = markers

    /**
     *  Sets properties from KML relevant to the container
     *
     *  @param data HashMap containing related properties of the container
     */
    override fun setProperties(data: HashMap<String, Any>) {
        //TODO()
    }


    /**
     * Sets the styles of children [ContainerManager]s and all child features ([MarkerManager]s etc.)
     *
     * @param styleMaps All StyleMap tags parsed from the KML file
     * @param styles All Style tags parsed from the KML file
     * @param images All images when present in KMZ file
     * @param context Current context used to get information about display size
     */
    override suspend fun setStyle(
        styleMaps: HashMap<String, KmlStyleMap>,
        styles: HashMap<String, KmlStyle>,
        images: HashMap<String, Bitmap>,
        context: Context
    ) {
        containers.forEach { it.setStyle(styleMaps, styles, images, context) }
        markers.forEach { it.setStyle(styleMaps, styles, images, context) }
    }

    /**
     * Adds a container as child to the ContainerManager
     *
     * @param container ContainerManager that will be added
     */
    internal fun addContainer(container: ContainerManager) {
        containers.add(container)
    }

    /**
     * Adds a marker as child to the ContainerManager
     *
     * @param marker MarkerManager that will be added
     */
    internal fun addMarker(marker: MarkerManager) {
        markers.add(marker)
    }

    /**
     * Sets the name of the container
     *
     * @param name Name of the container
     */
    public fun setName(name: String) {
        containerName = name
    }

    /**
     * Renders all features of the container and direct children containers
     */
    @Composable
    override fun Render() {
        markers.forEach { it.Render() }
        containers.forEach { it.Render() }
    }
}