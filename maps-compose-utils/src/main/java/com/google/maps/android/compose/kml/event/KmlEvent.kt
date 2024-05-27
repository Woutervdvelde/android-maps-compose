package com.google.maps.android.compose.kml.event

import com.google.maps.android.compose.kml.manager.GroundOverlayManager
import com.google.maps.android.compose.kml.manager.GroundOverlayProperties
import com.google.maps.android.compose.kml.manager.MarkerManager
import com.google.maps.android.compose.kml.manager.MarkerProperties
import com.google.maps.android.compose.kml.manager.PolygonManager
import com.google.maps.android.compose.kml.manager.PolygonProperties
import com.google.maps.android.compose.kml.manager.PolylineManager
import com.google.maps.android.compose.kml.manager.PolylineProperties

public sealed class KmlEvent {
    public sealed class Marker : KmlEvent() {
        public data class Clicked(val data: MarkerProperties) : Marker()
    }

    public sealed class Polyline : KmlEvent() {
        public data class Clicked(val data: PolylineProperties) : Polyline()
    }

    public sealed class Polygon : KmlEvent() {
        public data class Clicked(val data: PolygonProperties) : Polygon()
    }

    public sealed class GroundOverlay : KmlEvent() {
        public data class Clicked(val data: GroundOverlayProperties) : GroundOverlay()
    }
}
