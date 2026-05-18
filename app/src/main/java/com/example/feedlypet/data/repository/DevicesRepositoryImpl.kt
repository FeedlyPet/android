package com.example.feedlypet.data.repository

import com.example.feedlypet.data.network.DevicesApiService
import com.example.feedlypet.data.network.bodyOrError
import com.example.feedlypet.data.network.model.CreateDeviceRequest
import com.example.feedlypet.data.network.model.DeviceDto
import com.example.feedlypet.data.network.model.DeviceWithPasswordDto
import com.example.feedlypet.data.network.model.FoodLevelDto
import com.example.feedlypet.data.network.model.ManualFeedRequest
import com.example.feedlypet.data.network.model.ManualFeedResponse
import com.example.feedlypet.data.network.model.PaginatedResponse
import com.example.feedlypet.data.network.model.UpdateDeviceRequest
import com.example.feedlypet.data.network.safeApiCall
import com.example.feedlypet.domain.model.AuthResult
import javax.inject.Inject

class DevicesRepositoryImpl @Inject constructor(
    private val apiService: DevicesApiService
) : DevicesRepository {

    override suspend fun getDevices(page: Int, limit: Int): AuthResult<PaginatedResponse<DeviceDto>> =
        safeApiCall { apiService.getDevices(page, limit).bodyOrError() }

    override suspend fun getDevice(id: String): AuthResult<DeviceDto> =
        safeApiCall { apiService.getDevice(id).bodyOrError() }

    override suspend fun createDevice(request: CreateDeviceRequest): AuthResult<DeviceWithPasswordDto> =
        safeApiCall { apiService.createDevice(request).bodyOrError() }

    override suspend fun updateDevice(id: String, request: UpdateDeviceRequest): AuthResult<DeviceDto> =
        safeApiCall { apiService.updateDevice(id, request).bodyOrError() }

    override suspend fun deleteDevice(id: String): AuthResult<Unit> =
        safeApiCall { apiService.deleteDevice(id).bodyOrError() }

    override suspend fun feed(id: String, portionSize: Int): AuthResult<ManualFeedResponse> =
        safeApiCall { apiService.feed(id, ManualFeedRequest(portionSize)).bodyOrError() }

    override suspend fun regeneratePassword(id: String): AuthResult<DeviceWithPasswordDto> =
        safeApiCall { apiService.regeneratePassword(id).bodyOrError() }

    override suspend fun getFoodLevel(deviceId: String): AuthResult<FoodLevelDto> =
        safeApiCall { apiService.getLatestFoodLevel(deviceId).bodyOrError() }
}
