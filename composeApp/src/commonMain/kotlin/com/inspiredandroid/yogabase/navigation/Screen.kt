package com.inspiredandroid.yogabase.navigation

import kotlinx.serialization.Serializable

@Serializable
object MainMenu

@Serializable
data class Practice(val categoryId: Int)

@Serializable
data class Finish(val categoryId: Int)
