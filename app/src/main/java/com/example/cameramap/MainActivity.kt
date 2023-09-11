package com.example.cameramap

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.cameramap.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync {googleMap ->

            // set initial position and zoom
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.56682420, 126.97865226), 16F))

            // set minimum zoom level
            googleMap.setMinZoomPreference(4F)

            // get camara map from json file
            val jsonString = this@MainActivity.assets.open("camera.json").bufferedReader().readText()
            val jsonType = object : TypeToken<CameraMap>() {}.type
            val cameraMap = Gson().fromJson(jsonString, jsonType) as CameraMap

            // create cluster and add camera
            val clusterManager = ClusterManager<Camera>(this@MainActivity, googleMap)
            googleMap.setOnCameraIdleListener(clusterManager)
            googleMap.setOnMarkerClickListener(clusterManager)
            cameraMap.camera.forEach { clusterManager.addItem(it) }

            // start camera detail activity when user click item's info windows
            clusterManager.setOnClusterItemInfoWindowClickListener { camera ->
                val intent = Intent(this@MainActivity, CameraActivity::class.java).apply {
                    putExtra("owner", camera.owner)
                    putExtra("address", camera.address)
                    putExtra("old", camera.old)
                    putExtra("type", camera.type)
                    putExtra("count", camera.count)
                    putExtra("resolution", camera.resolution)
                    putExtra("angle", camera.angle)
                    putExtra("archive", camera.archive)
                    putExtra("installed", camera.installed)
                    putExtra("phone", camera.phone)
                    putExtra("latitude", camera.latitude)
                    putExtra("longitude", camera.longitude)
                }
                this@MainActivity.startActivity(intent)
            }

//            val cameraLatLngList  = ArrayList<LatLng>()
//            cameraMap.camera.forEach { if (it.type == "교통단속") cameraLatLngList.add(it.position) }
//            val colors = intArrayOf(
//                Color.rgb(102, 225, 0),  // green
//                Color.rgb(255, 0, 0) // red
//            )
//            val startPoints = floatArrayOf(0.2f, 1f)
//            val gradient = Gradient(colors, startPoints)
//            val provider = HeatmapTileProvider.Builder().data(cameraLatLngList).gradient(gradient).build()
//            googleMap.addTileOverlay(TileOverlayOptions().tileProvider(provider))
        }
    }
}