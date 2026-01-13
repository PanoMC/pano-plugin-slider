package com.panomc.plugins.slider.log

import com.panomc.platform.db.model.PluginActivityLog
import io.vertx.core.json.JsonObject

class ReorderedSliderItemsLog(
    userId: Long,
    username: String,
    pluginId: String,
    changes: JsonObject
) : PluginActivityLog(
    userId = userId,
    pluginId = pluginId,
    details = JsonObject().put("username", username).put("changes", changes)
)
