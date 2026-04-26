package com.example.feedlypet.data.network.model

data class StatisticsDto(
    val totalFeedings: Int,
    val totalFood: Int,
    val avgPortion: Double,
    val successRate: Double,
    val autoFeedings: Int,
    val manualFeedings: Int,
    val changePercent: Double?,
    val chartData: List<ChartDataPoint>
)

data class ChartDataPoint(
    val date: String,
    val feedings: Int,
    val food: Int
)
