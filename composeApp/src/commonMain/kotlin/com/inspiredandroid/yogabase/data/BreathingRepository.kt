package com.inspiredandroid.yogabase.data

import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import yogabase.composeapp.generated.resources.Res

@OptIn(ExperimentalResourceApi::class)
class BreathingRepository {
    private val json = Json { ignoreUnknownKeys = true }
    private var techniquesCache: List<BreathingTechnique>? = null

    suspend fun loadAll() {
        if (techniquesCache != null) return
        val bytes = Res.readBytes("files/breathing_techniques.json")
        techniquesCache = json.decodeFromString(bytes.decodeToString())
    }

    fun techniques(): List<BreathingTechnique> = techniquesCache.orEmpty()
    fun techniqueById(id: String): BreathingTechnique? = techniquesCache?.firstOrNull { it.id == id }
}
