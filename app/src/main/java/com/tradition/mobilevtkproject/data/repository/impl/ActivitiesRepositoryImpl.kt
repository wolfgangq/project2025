package com.tradition.mobilevtkproject.data.repository.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.UniversalRegionItem
import com.tradition.mobilevtkproject.data.repository.ActivitiesRepository
import kotlinx.coroutines.tasks.await


class ActivitiesRepositoryImpl(private val db: FirebaseFirestore = Firebase.firestore) : ActivitiesRepository {

    override suspend fun getItemList(regionName: String, collection: String): List<UniversalRegionItem>{
        var list = mutableListOf<UniversalRegionItem>()
        val snapshot = db.collection("regions").whereEqualTo("regionName", regionName).get().await()
        if (!snapshot.isEmpty) {
            val document = snapshot.documents[0]
            val itemSnapshot = document.reference.collection(collection).get().await()
            if (!itemSnapshot.isEmpty) {
                for (item in itemSnapshot.documents) {
                    val itemData = item.data!!
                    val id = null
                    val fullDescription = null
                    val curTitle = itemData["itemName"].toString()
                    val curDescr = itemData["itemDescription"]?.toString()
                    val coordinates = itemData["itemCoordinates"]?.toString()
                    val url = itemData["itemImageUrl"]?.toString()
                    val duration = itemData["itemDuration"]?.toString()
                    val groupSize = itemData["itemGroupSize"]?.toString()
                    val date = itemData["itemDate"]?.toString()
                    val startTime = itemData["itemStartTime"]?.toString()
                    val endTime = itemData["itemEndTime"]?.toString()
                    val location = itemData["itemLocation"]?.toString()


                    list.add(UniversalRegionItem(curTitle, curDescr, url, fullDescription, coordinates, location, duration, startTime, endTime, date, groupSize, id))
                    Log.d("Firestore", "Sight data: $itemData")
                }
                list.sortBy { it.title }
            }
        }
        return list
    }
}
