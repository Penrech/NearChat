package com.enrech.nearchat.models

import com.google.firebase.firestore.GeoPoint

data class EventUserLocation(
    var userID: String?,
    var lastKnownLocation: GeoPoint?
) {
}