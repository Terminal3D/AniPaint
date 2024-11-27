package com.example.coursework.features.navigation.route

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.coursework.R
import kotlinx.serialization.Serializable

@Serializable
sealed class MainRoutes(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
) {

    @Serializable
    data object Home : MainRoutes(
        title = R.string.bottom_bar_home_title,
        icon = R.drawable.baseline_home_24
    )

    @Serializable
    data object Paint : MainRoutes(
        title = R.string.bottom_bar_paint_title,
        icon = R.drawable.baseline_draw_24
    )

    @Serializable
    data object Gallery : MainRoutes(
        title = R.string.bottom_bar_title_gallery,
        icon = R.drawable.baseline_photo_library_24
    )
}
