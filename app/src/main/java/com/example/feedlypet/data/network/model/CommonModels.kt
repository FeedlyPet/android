package com.example.feedlypet.data.network.model

data class PaginatedResponse<T>(
    val data: List<T>,
    val meta: PaginationMeta
)

data class PaginationMeta(
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean
)
