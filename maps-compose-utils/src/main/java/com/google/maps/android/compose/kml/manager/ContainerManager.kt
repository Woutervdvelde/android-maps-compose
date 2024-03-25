package com.google.maps.android.compose.kml.manager

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.maps.android.compose.kml.data.KmlStyle
import com.google.maps.android.compose.kml.data.KmlStyleMap

public class ContainerManager() : KmlComposableManager {
    override var style: KmlStyle = KmlStyle()
    private var containerName: String = ""
    private var isActive: MutableState<Boolean> = mutableStateOf(true)
    private val children: MutableList<KmlComposableManager> = mutableListOf()

    public fun getName(): String = containerName

    /**
     * Sets the name of the container
     *
     * @param name Name of the container
     */
    public fun setName(name: String) {
        containerName = name
    }

    /**
     * Gets the current active state of the container
     */
    public fun getActive(): Boolean = isActive.value

    /**
     * Sets a container active or inactive. Inactive containers won't render their children
     *
     * @param active Active state of the container
     */
    public fun setActive(active: Boolean) {
        isActive.value = active
    }

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
    public fun getContainers(): List<ContainerManager> = children.filterIsInstance<ContainerManager>()

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
     * @return List of [MarkerManager]s
     */
    public fun getMarkers(): List<MarkerManager> = children.filterIsInstance<MarkerManager>()

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
        children.forEach { it.setStyle(styleMaps, styles, images, context) }
    }

    /**
     * Adds a child to the ContainerManager
     *
     * @param child Any class extending from the [KmlComposableManager]
     */
    internal fun addChild(child: KmlComposableManager) {
        children.add(child)
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