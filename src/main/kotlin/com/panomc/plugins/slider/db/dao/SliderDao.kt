package com.panomc.plugins.slider.db.dao

import com.panomc.platform.db.Dao
import com.panomc.plugins.slider.db.model.SliderItem
import io.vertx.sqlclient.SqlClient

abstract class SliderDao : Dao<SliderItem>(SliderItem::class.java) {
    abstract suspend fun add(sliderItem: SliderItem, sqlClient: SqlClient): Long

    abstract suspend fun update(sliderItem: SliderItem, sqlClient: SqlClient)

    abstract suspend fun deleteById(id: Long, sqlClient: SqlClient)

    abstract suspend fun getById(id: Long, sqlClient: SqlClient): SliderItem?

    abstract suspend fun getAll(sqlClient: SqlClient): List<SliderItem>

    abstract suspend fun getAllActive(sqlClient: SqlClient): List<SliderItem>

    abstract suspend fun getByImageFileName(imageFileName: String, sqlClient: SqlClient): SliderItem?

    abstract suspend fun updateOrder(id: Long, order: Int, sqlClient: SqlClient)

    abstract suspend fun incrementAllOrders(sqlClient: SqlClient)
}
