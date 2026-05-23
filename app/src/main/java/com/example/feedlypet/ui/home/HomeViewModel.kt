package com.example.feedlypet.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedlypet.R
import com.example.feedlypet.data.repository.DevicesRepository
import com.example.feedlypet.data.repository.HistoryRepository
import com.example.feedlypet.data.repository.NotificationsRepository
import com.example.feedlypet.data.repository.PetsRepository
import com.example.feedlypet.data.repository.StatisticsRepository
import com.example.feedlypet.domain.model.AuthResult
import com.example.feedlypet.ui.common.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val petsRepository: PetsRepository,
    private val devicesRepository: DevicesRepository,
    private val notificationsRepository: NotificationsRepository,
    private val historyRepository: HistoryRepository,
    private val statisticsRepository: StatisticsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Fetch all devices (not just 5) for accurate online/offline counts
            val petsDeferred = async { petsRepository.getPets(page = 1, limit = 1) }
            val devicesDeferred = async { devicesRepository.getDevices(page = 1, limit = 100) }
            val notificationsDeferred = async { notificationsRepository.getNotifications(page = 1, limit = 1, unreadOnly = true) }

            val petsResult = petsDeferred.await()
            val devicesResult = devicesDeferred.await()
            val notificationsResult = notificationsDeferred.await()

            val devices = if (devicesResult is AuthResult.Success) devicesResult.data.data else emptyList()
            val petsCount = if (petsResult is AuthResult.Success) petsResult.data.meta.total else 0
            val devicesOnline = devices.count { it.isOnline }
            val devicesOffline = devices.count { !it.isOnline }
            val unreadNotifications = if (notificationsResult is AuthResult.Success) notificationsResult.data.meta.total else 0

            // Fetch food levels, recent events, week stats in parallel
            val foodLevels: Map<String, Int>
            val recentEvents: List<com.example.feedlypet.data.network.model.FeedingEventDto>
            val weekStats: com.example.feedlypet.data.network.model.StatisticsDto?

            coroutineScope {
                val foodDeferred = async {
                    devices.map { device ->
                        async {
                            val fr = devicesRepository.getFoodLevel(device.id)
                            if (fr is AuthResult.Success) device.id to fr.data.foodLevel else null
                        }
                    }.mapNotNull { it.await() }.toMap()
                }
                val eventsDeferred = async {
                    if (devices.isNotEmpty()) {
                        val r = historyRepository.getEvents(deviceId = devices.first().id, page = 1, limit = 5)
                        if (r is AuthResult.Success) r.data.data else emptyList()
                    } else emptyList()
                }
                val statsDeferred = async {
                    if (devices.isNotEmpty()) {
                        val r = statisticsRepository.getStatistics(devices.first().id, "week")
                        if (r is AuthResult.Success) r.data else null
                    } else null
                }
                foodLevels = foodDeferred.await()
                recentEvents = eventsDeferred.await()
                weekStats = statsDeferred.await()
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    petsCount = petsCount,
                    devicesOnline = devicesOnline,
                    devicesOffline = devicesOffline,
                    unreadNotifications = unreadNotifications,
                    devices = devices,
                    foodLevels = foodLevels,
                    recentEvents = recentEvents,
                    weekStats = weekStats,
                    error = if (devicesResult !is AuthResult.Success) UiText.Res(R.string.common_error_network) else null
                )
            }
        }
    }

    fun feed(deviceId: String, portionSize: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isFeedingLoading = true) }
            when (devicesRepository.feed(deviceId, portionSize)) {
                is AuthResult.Success -> _uiState.update {
                    it.copy(isFeedingLoading = false, feedingSuccess = UiText.Res(R.string.device_feed_command_sent))
                }
                else -> _uiState.update {
                    it.copy(isFeedingLoading = false, error = UiText.Res(R.string.common_error_network))
                }
            }
        }
    }

    fun clearFeedingSuccess() {
        _uiState.update { it.copy(feedingSuccess = null) }
    }
}
