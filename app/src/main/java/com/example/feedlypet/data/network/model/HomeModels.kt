package com.example.feedlypet.data.network.model

data class HomeData(
    val petsCount: Int,
    val devicesOnline: Int,
    val devicesOffline: Int,
    val unreadNotifications: Int,
    val devices: List<DeviceDto>,
    val recentEvents: List<FeedingEventDto>
)
