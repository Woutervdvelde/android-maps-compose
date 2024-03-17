package com.google.maps.android.compose.kml.manager

public class ContainerManager {
    private var containerName: String = ""
    private val containers: MutableList<ContainerManager> = mutableListOf()
    private val markers: MutableList<MarkerManager> = mutableListOf()

    public fun getName(): String = containerName
    public fun getContainers(): List<ContainerManager> = containers
    public fun getMarkers(): List<MarkerManager> = markers

    public fun setName(name: String) {
        containerName = name
    }

    public fun addContainer(container: ContainerManager) {
        containers.add(container)
    }

    public fun addMarker(marker: MarkerManager) {
        markers.add(marker)
    }
}