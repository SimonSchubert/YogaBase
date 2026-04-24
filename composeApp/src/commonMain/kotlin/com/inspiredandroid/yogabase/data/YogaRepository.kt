package com.inspiredandroid.yogabase.data

import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import yogabase.composeapp.generated.resources.Res

@OptIn(ExperimentalResourceApi::class)
class YogaRepository {
    private val json = Json { ignoreUnknownKeys = true }
    private var posesCache: List<Pose>? = null
    private var categoriesCache: List<SessionCategory>? = null

    suspend fun loadAll() {
        if (posesCache != null && categoriesCache != null) return
        val posesBytes = Res.readBytes("files/poses.json")
        val categoriesBytes = Res.readBytes("files/categories.json")
        posesCache = json.decodeFromString(posesBytes.decodeToString())
        categoriesCache = json.decodeFromString(categoriesBytes.decodeToString())
    }

    fun poses(): List<Pose> = posesCache.orEmpty()
    fun categories(): List<SessionCategory> = categoriesCache.orEmpty()
    fun poseById(id: Int): Pose? = posesCache?.firstOrNull { it.id == id }
    fun categoryById(id: Int): SessionCategory? = categoriesCache?.firstOrNull { it.id == id }
    fun posesForCategory(c: SessionCategory): List<Pose> = c.poses.mapNotNull { poseById(it) }
}
