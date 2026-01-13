package com.panomc.plugins.slider.db.model

import com.panomc.platform.db.DBEntity

class SliderSetting(
    val key: String,
    val value: String
) : DBEntity()
