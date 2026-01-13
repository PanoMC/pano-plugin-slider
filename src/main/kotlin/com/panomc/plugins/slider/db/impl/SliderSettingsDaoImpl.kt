package com.panomc.plugins.slider.db.impl

import com.panomc.platform.annotation.Dao
import com.panomc.plugins.slider.db.dao.SliderSettingsDao
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.sqlclient.SqlClient
import io.vertx.sqlclient.Tuple
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Scope

@Dao
@Lazy
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
class SliderSettingsDaoImpl : SliderSettingsDao() {

    override suspend fun init(sqlClient: SqlClient) {
        sqlClient
            .query(
                """
                            CREATE TABLE IF NOT EXISTS `${getTablePrefix() + tableName}` (
                              `key` VARCHAR(255) NOT NULL,
                              `value` MEDIUMTEXT NOT NULL,
                              PRIMARY KEY (`key`)
                            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Slider settings table.';
                        """
            )
            .execute()
            .coAwait()
    }

    override suspend fun updateSetting(key: String, value: String, sqlClient: SqlClient) {
        val query = "INSERT INTO `${getTablePrefix() + tableName}` (`key`, `value`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `value` = ?"

        sqlClient
            .preparedQuery(query)
            .execute(Tuple.of(key, value, value))
            .coAwait()
    }

    override suspend fun getSetting(key: String, defaultValue: String, sqlClient: SqlClient): String {
        val query = "SELECT `value` FROM `${getTablePrefix() + tableName}` WHERE `key` = ?"

        val rows = sqlClient
            .preparedQuery(query)
            .execute(Tuple.of(key))
            .coAwait()

        return rows.firstOrNull()?.getString("value") ?: defaultValue
    }

    override suspend fun uninstall(sqlClient: SqlClient) {
        sqlClient
            .query("DROP TABLE IF EXISTS `${getTablePrefix() + tableName}`")
            .execute()
            .coAwait()
    }
}
