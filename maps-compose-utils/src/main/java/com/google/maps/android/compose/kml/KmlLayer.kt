package com.google.maps.android.compose.kml

import androidx.compose.runtime.Composable
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.kml.manager.ContainerManager

/**
 * Displays all the contents, including children of given ContainerManager
 *
 * @param containerManager The ContainerManager to be displayed
 */
@Composable
@GoogleMapComposable
public fun KmlLayer(
    containerManager: ContainerManager
) {
    containerManager.Render()
}