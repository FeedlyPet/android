package com.example.feedlypet.data.repository

import com.example.feedlypet.data.network.model.CreateDeviceRequest
import com.example.feedlypet.data.network.model.DeviceDto
import com.example.feedlypet.data.network.model.DeviceWithPasswordDto
import com.example.feedlypet.data.network.model.FoodLevelDto
import com.example.feedlypet.data.network.model.ManualFeedResponse
import com.example.feedlypet.data.network.model.PaginatedResponse
import com.example.feedlypet.data.network.model.UpdateDeviceRequest
import com.example.feedlypet.domain.model.AuthResult

interface DevicesRepository {
    suspend fun getDevices(page: Int = 1, limit: Int = 20): AuthResult<PaginatedResponse<DeviceDto>>
    suspend fun getDevice(id: String): AuthResult<DeviceDto>
    suspend fun createDevice(request: CreateDeviceRequest): AuthResult<DeviceWithPasswordDto>
    suspend fun updateDevice(id: String, request: UpdateDeviceRequest): AuthResult<DeviceDto>
    suspend fun deleteDevice(id: String): AuthResult<Unit>
    suspend fun feed(id: String, portionSize: Int): AuthResult<ManualFeedResponse>
    suspend fun regeneratePassword(id: String): AuthResult<DeviceWithPasswordDto>
    suspend fun getFoodLevel(deviceId: String): AuthResult<FoodLevelDto>
}
