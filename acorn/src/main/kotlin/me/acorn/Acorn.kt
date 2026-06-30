package me.acorn

import me.acorn.features.impl.misc.AutoReactModule
import me.melinoe.config.ModuleConfig
import me.melinoe.features.Category
import me.melinoe.features.ModuleManager
import net.fabricmc.api.ClientModInitializer

object Acorn : ClientModInitializer {

    val CATEGORY: Category = Category.custom("Acorn")

    override fun onInitializeClient() {

        ModuleManager.registerModules(
            ModuleConfig("acorn-config.json"),
            AutoReactModule
        )
    }
}
