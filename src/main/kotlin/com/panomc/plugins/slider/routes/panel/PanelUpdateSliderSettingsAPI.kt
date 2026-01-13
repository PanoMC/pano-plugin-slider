package com.panomc.plugins.slider.routes.panel

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.auth.AuthProvider
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.model.PanelApi
import com.panomc.platform.model.Path
import com.panomc.platform.model.Result
import com.panomc.platform.model.RouteType
import com.panomc.platform.model.Successful
import com.panomc.plugins.slider.SliderPlugin
import com.panomc.plugins.slider.db.dao.SliderSettingsDao
import com.panomc.plugins.slider.permission.ManageSliderPermission
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.Bodies
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository
import io.vertx.json.schema.common.dsl.Schemas.booleanSchema
import io.vertx.json.schema.common.dsl.Schemas.numberSchema
import io.vertx.json.schema.common.dsl.Schemas.objectSchema
import io.vertx.json.schema.common.dsl.Schemas.stringSchema

@Endpoint
class PanelUpdateSliderSettingsAPI(
    private val plugin: SliderPlugin,
    private val sliderSettingsDao: SliderSettingsDao
) : PanelApi() {
    override val paths = listOf(Path("/api/panel/slider/settings", RouteType.POST))

    private val authProvider by lazy {
        plugin.applicationContext.getBean(AuthProvider::class.java)
    }

    private val databaseManager by lazy {
        plugin.applicationContext.getBean(DatabaseManager::class.java)
    }

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository)
            .body(
                Bodies.json(
                    objectSchema()
                        .requiredProperty("renderHook", stringSchema())
                        .requiredProperty("homepageOnly", booleanSchema())
                        .requiredProperty("autoSlide", booleanSchema())
                        .requiredProperty("interval", numberSchema())
                        .requiredProperty("pauseOnHover", booleanSchema())
                        .requiredProperty("wrap", booleanSchema())
                        .requiredProperty("indicators", booleanSchema())
                        .requiredProperty("controls", booleanSchema())
                        .requiredProperty("fade", booleanSchema())
                        .requiredProperty("titleColor", stringSchema())
                        .requiredProperty("subtitleColor", stringSchema())
                        .requiredProperty("captionBackground", stringSchema())
                        .requiredProperty("captionOpacity", numberSchema())
                        .requiredProperty("blurAmount", numberSchema())
                        .requiredProperty("captionStyle", stringSchema())
                )
            )
            .build()

    override suspend fun handle(context: RoutingContext): Result {
        authProvider.requirePermission(ManageSliderPermission(), context)

        val body = context.body().asJsonObject()
        val renderHook = body.getString("renderHook")
        val homepageOnly = body.getBoolean("homepageOnly")
        val autoSlide = body.getBoolean("autoSlide")
        val interval = body.getInteger("interval")
        val pauseOnHover = body.getBoolean("pauseOnHover")
        val wrap = body.getBoolean("wrap")
        val indicators = body.getBoolean("indicators")
        val controls = body.getBoolean("controls")
        val fade = body.getBoolean("fade")
        val titleColor = body.getString("titleColor")
        val subtitleColor = body.getString("subtitleColor")
        val captionBackground = body.getString("captionBackground")
        val captionOpacity = body.getDouble("captionOpacity")
        val blurAmount = body.getInteger("blurAmount")
        val captionStyle = body.getString("captionStyle")

        val sqlClient = databaseManager.getSqlClient()
        
        sliderSettingsDao.updateSetting("renderHook", renderHook, sqlClient)
        sliderSettingsDao.updateSetting("homepageOnly", homepageOnly.toString(), sqlClient)
        sliderSettingsDao.updateSetting("autoSlide", autoSlide.toString(), sqlClient)
        sliderSettingsDao.updateSetting("interval", interval.toString(), sqlClient)
        sliderSettingsDao.updateSetting("pauseOnHover", pauseOnHover.toString(), sqlClient)
        sliderSettingsDao.updateSetting("wrap", wrap.toString(), sqlClient)
        sliderSettingsDao.updateSetting("indicators", indicators.toString(), sqlClient)
        sliderSettingsDao.updateSetting("controls", controls.toString(), sqlClient)
        sliderSettingsDao.updateSetting("fade", fade.toString(), sqlClient)
        sliderSettingsDao.updateSetting("titleColor", titleColor, sqlClient)
        sliderSettingsDao.updateSetting("subtitleColor", subtitleColor, sqlClient)
        sliderSettingsDao.updateSetting("captionBackground", captionBackground, sqlClient)
        sliderSettingsDao.updateSetting("captionOpacity", captionOpacity.toString(), sqlClient)
        sliderSettingsDao.updateSetting("blurAmount", blurAmount.toString(), sqlClient)
        sliderSettingsDao.updateSetting("captionStyle", captionStyle, sqlClient)

        return Successful()
    }
}
