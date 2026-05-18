package com.example.feedlypet.ui.common

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {
    data class Raw(val value: String) : UiText()
    data class Res(@StringRes val id: Int) : UiText()

    fun resolve(context: Context): String = when (this) {
        is Raw -> value
        is Res -> context.getString(id)
    }
}
