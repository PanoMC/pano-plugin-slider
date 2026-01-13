package com.panomc.plugins.slider.log

import com.panomc.platform.db.model.PluginActivityLog
import io.vertx.core.json.JsonObject

class DeletedSliderItemLog(
    userId: Long,
    username: String,
    pluginId: String,
    id: Long,
    title: String,
) : PluginActivityLog(
    userId = userId,
    pluginId = pluginId,
    details = JsonObject().put("id", id).put("title", title).put("username", username)
)
