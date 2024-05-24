package com.google.maps.android.compose.kml.event

import com.google.maps.android.compose.kml.manager.GroundOverlayManager
import com.google.maps.android.compose.kml.manager.MarkerManager
import com.google.maps.android.compose.kml.manager.PolygonManager
import com.google.maps.android.compose.kml.manager.PolylineManager

public sealed class KmlEvent {
    public sealed class Marker : KmlEvent() {
        public data class Clicked(val data: MarkerManager.MarkerProperties) : Marker()
    }

    public sealed class Polyline : KmlEvent() {
        public data class Clicked(val data: PolylineManager.PolylineProperties) : Polyline()
    }

    public sealed class Polygon : KmlEvent() {
        public data class Clicked(val data: PolygonManager.PolygonProperties) : Polygon()
    }

    public sealed class GroundOverlay : KmlEvent() {
        public data class Clicked(val data: GroundOverlayManager.GroundOverlayProperties) : GroundOverlay()
    }
}
