package com.example.feedlypet.data.network.model

import com.google.gson.annotations.SerializedName

data class StatisticsDto(
    val totalFeedings: Int,
    val totalFood: Int,
    @SerializedName("averagePortion") val avgPortion: Double,
    val successfulFeedings: Int,
    val failedFeedings: Int,
    @SerializedName("automaticFeedings") val autoFeedings: Int,
    val manualFeedings: Int,
    @SerializedName("dailyBreakdown") val chartData: List<ChartDataPoint>,
    val comparison: StatisticsComparison?
) {
    val successRate: Double
        get() = if (totalFeedings > 0) successfulFeedings.toDouble() / totalFeedings else 0.0

    val changePercent: Double?
        get() = comparison?.feedingsChange?.toDouble()
}

data class ChartDataPoint(
    val date: String,
    val feedings: Int,
    val food: Int
)

data class StatisticsComparison(
    val previousFeedings: Int,
    val previousFood: Int,
    val feedingsChange: Int,
    val foodChange: Int
)
