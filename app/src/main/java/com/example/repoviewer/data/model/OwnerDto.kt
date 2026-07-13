package com.example.repoviewer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OwnerDto(
    val login: String
)