package com.panomc.plugins.slider.db.model

import com.panomc.platform.db.DBEntity

open class SliderItem(
    val id: Long = -1,
    val title: String = "",
    val subtitle: String? = null,
    val imageUrl: String = "",
    val imageFileName: String? = null,
    val linkUrl: String? = null,
    val openInNewTab: Boolean = false,
    val itemOrder: Int = 0,
    val active: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
) : DBEntity()
