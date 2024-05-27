package com.google.maps.android.compose.kml.manager

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.maps.android.compose.kml.data.KmlStyle
import com.google.maps.android.compose.kml.data.KmlStyleMap
import com.google.maps.android.compose.kml.event.KmlEventListener
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_DESCRIPTION
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_DRAW_ORDER
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_EXTENDED_DATA
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_NAME
import com.google.maps.android.compose.kml.manager.IKmlComposableProperties.Companion.DEFAULT_STYLE_URL
import com.google.maps.android.compose.kml.parser.ExtendedData

public class ContainerManager : KmlComposableManager<ContainerProperties>() {
    private val children: MutableList<KmlComposableManager<IKmlComposableProperties>> =
        mutableListOf()

    override val _properties: MutableState<ContainerProperties> =
        mutableStateOf(ContainerProperties())

    public fun getName(): String = _properties.value.name

    /**
     * Sets the name of the container
     *
     * @param name Name of the container
     */
    public fun setName(name: String) {
        _properties.value = _properties.value.copy(
            name = name
        )
    }

    /**
     * Gets the current active state of the container
     */
    public fun getActive(): Boolean = isActive.value

    /**
     * Toggles the active state of the container
     */
    public fun toggleActive() {
        setActive(!isActive.value)
    }

    /**
     * Gets a list of containers that are direct children of this ContainerManager
     *
     * @return List of [ContainerManager]s
     */
    public fun getContainers(): List<ContainerManager> =
        children.filterIsInstance<ContainerManager>()

    /**
     * Gets a list of containers from a specific depth in the tree of nested ContainerManagers.
     * When a branch can't go deeper it will return the leave
     *
     * @param depth The depth all containers will be returned from, if possible
     * @return list of containers at specified depth or as deep as a branch goes
     */
    public fun getContainers(depth: Int): List<ContainerManager> = getContainers(depth, 0)

    /**
     * Recursive function that returns a list of containers from a specific depth
     *
     * @param targetDepth The depth all containers will be returned from, if possible
     * @param currentDepth The current depth in recursion
     * @return list of containers at specified depth or as deep as a branch goes
     */
    private fun getContainers(targetDepth: Int, currentDepth: Int): List<ContainerManager> {
        val containers = getContainers()
        return if (targetDepth == currentDepth || containers.isEmpty()) {
            listOf(this)
        } else {
            containers.flatMap { it.getContainers(targetDepth, currentDepth + 1) }
        }
    }

    /**
     * Gets a list of markers that are direct children of this ContainerManager
     *
     * @return List of [MarkerManager]s
     */
    public fun getMarkers(): List<MarkerManager> = children.filterIsInstance<MarkerManager>()

    /**
     * Gets a list of polylines that are direct children of this ContainerManager
     *
     * @return List of [PolylineManager]s
     */
    public fun getPolylines(): List<PolylineManager> = children.filterIsInstance<PolylineManager>()

    /**
     * Gets a list of ground overlays that are direct children of this ContainerManager
     *
     * @return List of [GroundOverlayManager]s
     */
    public fun getGroundOverlays(): List<GroundOverlayManager> =
        children.filterIsInstance<GroundOverlayManager>()

    /**
     * Adds a child to the ContainerManager
     *
     * @param child Any class extending from the [KmlComposableManager]
     */
    internal fun addChild(child: KmlComposableManager<IKmlComposableProperties>) {
        children.add(child)
    }

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
     */
    override suspend fun setStyle(
        styleMaps: HashMap<String, KmlStyleMap>,
        styles: HashMap<String, KmlStyle>,
        images: HashMap<String, Bitmap>,
        parentVisibility: Boolean
    ) {
        setActive(parentVisibility)
        children.forEach { it.setStyle(styleMaps, styles, images, getActive()) }
    }

    /**
     * Sets the object that uses the KmlEventListener interface for children
     *
     * @param eventListener KmlEventListener to be used
     */
    override fun setEventListener(eventListener: KmlEventListener) {
        children.forEach { it.setEventListener(eventListener) }
    }

    /**
     * Renders all features of the container and direct children containers
     */
    @Composable
    override fun Render() {
        if (isActive.value) {
            children.forEach { it.Render() }
        }
    }
}

public data class ContainerProperties(
    override val name: String = DEFAULT_NAME,
    override val description: String = DEFAULT_DESCRIPTION,
    override val drawOrder: Float = DEFAULT_DRAW_ORDER,
    override val styleUrl: String? = DEFAULT_STYLE_URL,
    override val extendedData: List<ExtendedData>? = DEFAULT_EXTENDED_DATA,
) : IKmlComposableProperties