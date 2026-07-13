package com.example.repoviewer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ReadmeDto(
    val content: String,
    val encoding: String
)