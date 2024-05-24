package com.google.maps.android.compose.kml.event

import com.google.maps.android.compose.kml.manager.MarkerManager
import com.google.maps.android.compose.kml.manager.PolygonManager
import com.google.maps.android.compose.kml.manager.PolylineManager
import java.util.concurrent.CopyOnWriteArrayList

public class KmlEventPublisher {
    private val subscribers: CopyOnWriteArrayList<(KmlEvent) -> Unit> = CopyOnWriteArrayList()

    public fun subscribe(listener: (KmlEvent) -> Unit) {
        subscribers.add(listener)
    }

    public fun unsubscribe(listener: (KmlEvent) -> Unit) {
        subscribers.remove(listener)
    }

    internal fun emit(event: KmlEvent) {
        subscribers.forEach { it(event) }
    }
}

internal interface KmlEventListener {
    fun onEvent(event: KmlEvent)
}

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
}