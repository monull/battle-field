package io.github.monull.battlefield.plugin

import io.github.monull.battlefield.command.BattleFieldKommand
import io.github.monull.battlefield.field.BattleField
import io.github.monull.dev.command.kommand
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class BattleFieldPlugin : JavaPlugin() {
    lateinit var command: BattleFieldKommand
    var field = HashMap<String, BattleField>()
    override fun onEnable() {
        dataFolder.mkdirs()
        kommand {
            command = BattleFieldKommand.apply {
                register(this@kommand, this@BattleFieldPlugin)
            }
        }
    }

    override fun onDisable() {
        val folder = File(dataFolder, "field").also { it.mkdirs() }
        field.values.forEach {
            val file = File(folder, "${it.name}.yml")
            val config = YamlConfiguration()
            it.save(config)
            config.save(file)
        }
    }
}