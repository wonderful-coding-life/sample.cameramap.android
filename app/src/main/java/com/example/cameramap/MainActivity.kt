package com.example.cameramap

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.cameramap.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync {googleMap ->

            // set initial position and zoom
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.56682420, 126.97865226), 8F))

            // set minimum zoom level
            //googleMap.setMinZoomPreference(4F)

            // get camara map from json file
            val jsonString = this@MainActivity.assets.open("camping.json").bufferedReader().readText()
            val jsonType = object : TypeToken<CampingMap>() {}.type
            val campingMap = Gson().fromJson(jsonString, jsonType) as CampingMap

            // create cluster and add camera
//            val clusterManager = ClusterManager<Camping>(this@MainActivity, googleMap)
//            googleMap.setOnCameraIdleListener(clusterManager)
//            googleMap.setOnMarkerClickListener(clusterManager)
//            campingMap.camping.forEach { clusterManager.addItem(it) }

            // create heat map
            val campingLatLngList  = ArrayList<LatLng>()
            campingMap.camping.forEach { campingLatLngList.add(it.position) }
            Log.i(TAG, "spiderman count = ${campingMap.camping.size}")
            val provider = HeatmapTileProvider.Builder().data(campingLatLngList).build()
            googleMap.addTileOverlay(TileOverlayOptions().tileProvider(provider))
        }
    }
}