package com.inspiredandroid.yogabase.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionCategory(
    val id: Int,
    @SerialName("category_name") val categoryName: String,
    @SerialName("category_description") val categoryDescription: String,
    val poses: List<Int>,
    val difficulty: String,
)
