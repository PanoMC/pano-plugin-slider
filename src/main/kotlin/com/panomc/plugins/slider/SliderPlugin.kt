package com.panomc.plugins.slider

import com.panomc.platform.api.PanoPlugin
import com.panomc.platform.api.PluginDatabaseManager
import com.panomc.platform.setup.SetupManager
import java.io.File

class SliderPlugin : PanoPlugin() {
    private val pluginDatabaseManager by lazy {
        applicationContext.getBean(PluginDatabaseManager::class.java)
    }

    private val setupManager by lazy {
        applicationContext.getBean(SetupManager::class.java)
    }

    val uploadsDir: File by lazy {
        File(pluginDataFolder, "uploads")
    }

    private var isInitialized = false

    override suspend fun onStart() {
        logger.info("Starting...")

        if (!setupManager.isSetupDone()) {
            logger.info("Setup is not finished, waiting for setup completion...")
            return
        }

        startPlugin()
    }

    internal suspend fun startPlugin() {
        if (isInitialized) return
        isInitialized = true

        pluginDatabaseManager.initialize(this)

        if (!uploadsDir.exists()) {
            uploadsDir.mkdirs()
        }
    }

    override suspend fun onEnable() {
        logger.info("Enabled!")
    }

    override suspend fun onUninstall() {
        logger.info("Uninstalling...")

        pluginDatabaseManager.uninstall(this)

        if (uploadsDir.exists()) {
            uploadsDir.deleteRecursively()
        }
    }
}
