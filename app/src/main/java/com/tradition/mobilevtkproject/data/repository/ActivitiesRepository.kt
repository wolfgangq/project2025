package com.tradition.mobilevtkproject.data.repository

import com.tradition.mobilevtkproject.UniversalRegionItem

interface ActivitiesRepository {
    suspend fun getItemList(regionName: String, collection: String): List<UniversalRegionItem>
}