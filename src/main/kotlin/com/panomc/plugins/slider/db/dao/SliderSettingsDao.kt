package com.panomc.plugins.slider.db.dao

import com.panomc.platform.db.Dao
import com.panomc.plugins.slider.db.model.SliderSetting
import io.vertx.sqlclient.SqlClient

abstract class SliderSettingsDao : Dao<SliderSetting>(SliderSetting::class.java) {
    abstract suspend fun getSetting(key: String, defaultValue: String, sqlClient: SqlClient): String

    abstract suspend fun updateSetting(key: String, value: String, sqlClient: SqlClient)
}
