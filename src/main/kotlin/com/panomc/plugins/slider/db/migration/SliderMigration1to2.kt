package com.panomc.plugins.slider.db.migration

import com.panomc.platform.annotation.Migration
import com.panomc.platform.db.DatabaseMigration
import com.panomc.platform.route.ApiPaths
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.sqlclient.SqlClient
import io.vertx.sqlclient.Tuple

@Migration
class SliderMigration1to2 : DatabaseMigration(1, 2, "Move stored slider image urls to /api/v1") {
    override val handlers: List<suspend (SqlClient) -> Unit> = listOf(
        moveImageUrls()
    )

    // `/api/slider/items/image/:fileName` -> `/api/plugins/pano-plugin-slider/items/image/:fileName`
    // (api/paths.old-new.json)
    private fun moveImageUrls(): suspend (sqlClient: SqlClient) -> Unit =
        { sqlClient: SqlClient ->
            val oldPrefix = ApiPaths.BASE + "/slider/items/image/"
            val newPrefix = ApiPaths.plugin("pano-plugin-slider", "/items/image/")

            sqlClient
                .preparedQuery(
                    "UPDATE `${getTablePrefix()}slider_item` " +
                            "SET `imageUrl` = CONCAT(?, SUBSTRING(`imageUrl`, ?)) " +
                            "WHERE `imageUrl` LIKE ?"
                )
                .execute(Tuple.of(newPrefix, oldPrefix.length + 1, "$oldPrefix%"))
                .coAwait()
        }
}
