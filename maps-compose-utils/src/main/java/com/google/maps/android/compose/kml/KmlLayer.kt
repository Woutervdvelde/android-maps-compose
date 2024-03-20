package com.google.maps.android.compose.kml

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.kml.parser.KmlParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedInputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

@Composable
@GoogleMapComposable
public fun KmlLayer(
    stream: InputStream
) {
    val images = HashMap<String, Bitmap>()
    var parser: KmlParser? = null

    val bis = BufferedInputStream(stream)
    bis.mark(1024)
    val zip = ZipInputStream(bis)
    Log.e("KmlLayer", "Started - ${System.currentTimeMillis()}")

    try {
        var entry = zip.nextEntry
        if (entry != null) { // is a KMZ zip file
            while (entry != null) {
                if (parser == null && entry.name.lowercase().endsWith(".kml")) {
                    parser = parseKml(zip)
                } else {
                    val bitmap = BitmapFactory.decodeStream(zip)
                    if (bitmap != null) {
                        images[entry.name] = bitmap
                    } else {
                        Log.w(
                            "KmlLayer",
                            "Unsupported KMZ contents file type: ${entry.name}"
                        )
                    }
                }
                entry = zip.nextEntry
            }

            if (parser == null) {
                throw IllegalArgumentException("KML not found in InputStream")
            }
        } else {
            bis.reset()
            parser = parseKml(bis)
        }
    } finally {
        stream.close()
        bis.close()
        zip.close()
    }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            parser?.applyStyles(images)
        }
    }
    parser?.container?.Render()
}

private fun parseKml(stream: InputStream): KmlParser {
    val xmlPullParser = createXmlParser(stream)
    val parser = KmlParser(xmlPullParser)
    parser.parseKml()
    return parser
}

private fun createXmlParser(stream: InputStream): XmlPullParser {
    val factory = XmlPullParserFactory.newInstance()
    factory.isNamespaceAware = true
    val parser = factory.newPullParser()
    parser.setInput(stream, null)
    return parser;
}