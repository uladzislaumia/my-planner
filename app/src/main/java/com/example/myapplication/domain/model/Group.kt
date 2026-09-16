package com.example.myapplication.domain.model

data class Group(
    val id: String,
    val title: String,
    val members: List<User>,
    val inviteCode: String
)
