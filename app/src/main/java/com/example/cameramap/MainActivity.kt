package com.example.cameramap

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync {googleMap ->

            // set initial position and zoom
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.56682420, 126.97865226), 16F))

            // set minimum zoom level
            googleMap.setMinZoomPreference(14F)

            // get camara map from json file
            val jsonString = this@MainActivity.assets.open("camera.json").bufferedReader().readText()
            val jsonType = object : TypeToken<CameraMap>() {}.type
            val cameraMap = Gson().fromJson(jsonString, jsonType) as CameraMap

            // add markers with blue for traffic and pink for security
            cameraMap.camera.forEach { camera ->
                val icon = if ("생활방범" == camera.type) BitmapDescriptorFactory.fromResource(R.drawable.poi_pink) else BitmapDescriptorFactory.fromResource(R.drawable.poi_blue)
                googleMap.addMarker(
                    MarkerOptions()
                        .icon(icon)
                        .position(LatLng(camera.latitude, camera.longitude))
                        .title(camera.type)
                        .snippet("${camera.latitude}, ${camera.longitude}")
                )?.let {
                    it.tag = camera
                }
            }

            // show camera details when click marker
            googleMap.setOnInfoWindowClickListener {
                val camera = it.tag as Camera
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
        }
    }
}