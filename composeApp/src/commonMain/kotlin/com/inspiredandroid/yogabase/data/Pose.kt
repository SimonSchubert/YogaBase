package com.inspiredandroid.yogabase.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pose(
    val id: Int,
    @SerialName("english_name") val englishName: String,
    @SerialName("sanskrit_name_adapted") val sanskritNameAdapted: String,
    @SerialName("sanskrit_name") val sanskritName: String,
    @SerialName("translation_name") val translationName: String,
    @SerialName("pose_description") val poseDescription: String,
    @SerialName("pose_benefits") val poseBenefits: String,
    val difficulty: String,
    @SerialName("base_time") val baseTime: Int,
    @SerialName("has_two_sides") val hasTwoSides: Boolean,
)
