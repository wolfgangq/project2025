package com.tradition.mobilevtkproject

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreRegionInitializer {
    private val db = Firebase.firestore
    private val TAG = "FirestoreRegionInitializer"

    fun initializeRegion(
        regionName: String,
        descReg: String,
        historyReg: String,
        excursionList: List<UniversalRegionItem>,
        eventList: List<UniversalRegionItem>,
        sightList: List<UniversalRegionItem>,
        competitionList: List<UniversalRegionItem>
    ) {
        db.collection("regions")
            .whereEqualTo("regionName", regionName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    val region = hashMapOf(
                        "regionName" to regionName,
                        "regionDescription" to descReg,
                        "regionHistory" to historyReg
                    )
                    db.collection("regions")
                        .add(region)
                        .addOnSuccessListener { documentReference ->
                            Log.d(ContentValues.TAG, "DocumentSnapshot added with regionName: $regionName")

                            for(item in excursionList){
                                val excursion = hashMapOf(
                                    "itemName" to item.title,
                                    "itemDescription" to item.description,
                                    "itemImageUrl" to item.imageUrl,
                                    "itemFullDescription" to item.fullDescription,
                                    "itemCoordinates" to item.coordinates
                                )
                                documentReference.collection("excursions")
                                    .add(excursion)
                            }
                            for(item in eventList){
                                val event = hashMapOf(
                                    "itemName" to item.title,
                                    "itemDescription" to item.description,
                                    "itemImageUrl" to item.imageUrl,
                                    "itemFullDescription" to item.fullDescription,
                                    "itemCoordinates" to item.coordinates
                                )
                                documentReference.collection("events")
                                    .add(event)
                            }
                            for(item in sightList){
                                val sight = hashMapOf(
                                    "itemName" to item.title,
                                    "itemDescription" to item.description,
                                    "itemImageUrl" to item.imageUrl,
                                    "itemFullDescription" to item.fullDescription,
                                    "itemCoordinates" to item.coordinates
                                )
                                documentReference.collection("sights")
                                    .add(sight)
                            }
                            for(item in competitionList){
                                val competition = hashMapOf(
                                    "itemName" to item.title,
                                    "itemDescription" to item.description,
                                    "itemImageUrl" to item.imageUrl,
                                    "itemFullDescription" to item.fullDescription,
                                    "itemCoordinates" to item.coordinates
                                )
                                documentReference.collection("competitions")
                                    .add(competition)
                            }
                        }
                } else {
                    Log.d(TAG, "Region $regionName already exists")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error checking region existence", e)
            }
    }
}