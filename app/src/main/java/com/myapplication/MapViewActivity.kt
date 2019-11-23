package com.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_map_view.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.ArrayList

class MapViewActivity : AppCompatActivity() {
    private var coordinatesLong: String? = null
    private var coordinatesLat: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_view)
        val extras = intent.extras
        coordinatesLong = extras?.getString("coordinatesLong")
        coordinatesLat = extras?.getString("coordinatesLat")


        //val longitude = lines[0].split(":".toRegex()).dropLastWhile({ it.isEmpty() })
        val longitude = coordinatesLong
        Log.e("longitude %s" , longitude.toString())
       // val latitude = lines[1].split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        val latitude = coordinatesLat
        Log.e("latitude %s" , latitude.toString())

        mapview_item.setTileSource(TileSourceFactory.MAPNIK)
        mapview_item.setBuiltInZoomControls(true)
        mapview_item.setMultiTouchControls(false)
        val mapController = mapview_item.getController()
        mapController.setZoom(18.0)
        val startPoint = GeoPoint(
            java.lang.Double.parseDouble(latitude.toString()!!),
            java.lang.Double.parseDouble(longitude.toString()!!)
        )
        mapController.setCenter(startPoint)
        val overlayItems = ArrayList<OverlayItem>()
        val myLocationOverlayItem = OverlayItem("Here", "Current Position", startPoint)
        overlayItems.add(myLocationOverlayItem)
        val mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this@MapViewActivity), mapview_item)
        mLocationOverlay.enableMyLocation()
        val marker = Marker(mapview_item)
        marker.position = startPoint
        mapview_item.getOverlays().add(marker)
    }
}
