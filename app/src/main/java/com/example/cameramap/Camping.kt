package com.example.cameramap

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class Camping(
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val type: String
): ClusterItem {
    override fun getPosition(): LatLng {
        return LatLng(latitude, longitude)
    }

    override fun getTitle(): String? {
        return name
    }

    override fun getSnippet(): String? {
        return "$latitude, $longitude"
    }
}