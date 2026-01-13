package com.panomc.plugins.slider.db.impl

import com.panomc.platform.annotation.Dao
import com.panomc.plugins.slider.db.dao.SliderDao
import com.panomc.plugins.slider.db.model.SliderItem
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.mysqlclient.MySQLClient
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.SqlClient
import io.vertx.sqlclient.Tuple
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Scope

@Dao
@Lazy
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
class SliderDaoImpl : SliderDao() {

    override suspend fun init(sqlClient: SqlClient) {
        sqlClient
            .query(
                """
                            CREATE TABLE IF NOT EXISTS `${getTablePrefix() + tableName}` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `title` MEDIUMTEXT NOT NULL,
                              `subtitle` MEDIUMTEXT,
                              `imageUrl` MEDIUMTEXT NOT NULL,
                              `imageFileName` VARCHAR(255),
                              `linkUrl` MEDIUMTEXT,
                              `openInNewTab` TINYINT(1) NOT NULL DEFAULT 0,
                              `itemOrder` int NOT NULL DEFAULT 0,
                              `active` TINYINT(1) NOT NULL DEFAULT 1,
                              `createdAt` BIGINT(20) NOT NULL,
                              `updatedAt` BIGINT(20) NOT NULL,
                              PRIMARY KEY (`id`)
                            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Slider table.';
                        """
            )
            .execute()
            .coAwait()
    }

    override suspend fun add(sliderItem: SliderItem, sqlClient: SqlClient): Long {
        val query =
            "INSERT INTO `${getTablePrefix() + tableName}` (`title`, `subtitle`, `imageUrl`, `imageFileName`, `linkUrl`, `openInNewTab`, `itemOrder`, `active`, `createdAt`, `updatedAt`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"

        val rows: RowSet<Row> = sqlClient
            .preparedQuery(query)
            .execute(
                Tuple.of(
                    sliderItem.title,
                    sliderItem.subtitle,
                    sliderItem.imageUrl,
                    sliderItem.imageFileName,
                    sliderItem.linkUrl,
                    sliderItem.openInNewTab,
                    sliderItem.itemOrder,
                    sliderItem.active,
                    sliderItem.createdAt,
                    sliderItem.updatedAt
                )
            )
            .coAwait()

        return rows.property(MySQLClient.LAST_INSERTED_ID)
    }

    override suspend fun update(sliderItem: SliderItem, sqlClient: SqlClient) {
        val query =
            "UPDATE `${getTablePrefix() + tableName}` SET `title` = ?, `subtitle` = ?, `imageUrl` = ?, `imageFileName` = ?, `linkUrl` = ?, `openInNewTab` = ?, `itemOrder` = ?, `active` = ?, `updatedAt` = ? WHERE `id` = ?"

        sqlClient
            .preparedQuery(query)
            .execute(
                Tuple.of(
                    sliderItem.title,
                    sliderItem.subtitle,
                    sliderItem.imageUrl,
                    sliderItem.imageFileName,
                    sliderItem.linkUrl,
                    sliderItem.openInNewTab,
                    sliderItem.itemOrder,
                    sliderItem.active,
                    sliderItem.updatedAt,
                    sliderItem.id
                )
            )
            .coAwait()
    }

    override suspend fun deleteById(id: Long, sqlClient: SqlClient) {
        val query = "DELETE FROM `${getTablePrefix() + tableName}` WHERE `id` = ?"

        sqlClient
            .preparedQuery(query)
            .execute(Tuple.of(id))
            .coAwait()
    }

    override suspend fun getById(id: Long, sqlClient: SqlClient): SliderItem? {
        val query = "SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}` WHERE `id` = ?"

        val rows: RowSet<Row> = sqlClient
            .preparedQuery(query)
            .execute(Tuple.of(id))
            .coAwait()

        return rows.toEntities().getOrNull(0)
    }

    override suspend fun getAll(sqlClient: SqlClient): List<SliderItem> {
        val query = "SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}` ORDER BY `itemOrder` ASC, `id` DESC"

        val rows: RowSet<Row> = sqlClient
            .query(query)
            .execute()
            .coAwait()

        return rows.toEntities()
    }

    override suspend fun getAllActive(sqlClient: SqlClient): List<SliderItem> {
        val query = "SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}` WHERE `active` = 1 ORDER BY `itemOrder` ASC, `id` DESC"

        val rows: RowSet<Row> = sqlClient
            .query(query)
            .execute()
            .coAwait()

        return rows.toEntities()
    }

    override suspend fun getByImageFileName(imageFileName: String, sqlClient: SqlClient): SliderItem? {
        val query = "SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}` WHERE `imageFileName` = ?"

        val rows: RowSet<Row> = sqlClient
            .preparedQuery(query)
            .execute(Tuple.of(imageFileName))
            .coAwait()

        return rows.toEntities().getOrNull(0)
    }

    override suspend fun updateOrder(id: Long, order: Int, sqlClient: SqlClient) {
        val query = "UPDATE `${getTablePrefix() + tableName}` SET `itemOrder` = ?, `updatedAt` = ? WHERE `id` = ?"

        sqlClient
            .preparedQuery(query)
            .execute(Tuple.of(order, System.currentTimeMillis(), id))
            .coAwait()
    }

    override suspend fun uninstall(sqlClient: SqlClient) {
        sqlClient
            .query("DROP TABLE IF EXISTS `${getTablePrefix() + tableName}`")
            .execute()
            .coAwait()
    }

    override suspend fun incrementAllOrders(sqlClient: SqlClient) {
        val query = "UPDATE `${getTablePrefix() + tableName}` SET `itemOrder` = `itemOrder` + 1"

        sqlClient
            .query(query)
            .execute()
            .coAwait()
    }
}
