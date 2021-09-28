package io.github.monull.battlefield.command

import io.github.monull.battlefield.field.BattleField
import io.github.monull.battlefield.map.CustomMapRenderer
import io.github.monull.battlefield.plugin.BattleFieldPlugin
import io.github.monull.dev.command.PluginKommand
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.map.MapView
import java.io.File

object BattleFieldKommand {
    fun register(kommand: PluginKommand, plugin: BattleFieldPlugin) {
        kommand.register("battlefield", "bf") {
            val worlds = dynamic { context, input ->
                Bukkit.getWorld(input)
            }.apply {
                suggests {
                suggest(Bukkit.getWorlds().map { it.name })
            }
            }
            then("map") {
                then("world" to worlds) {
                    executes {
                        giveMap(it["world"], plugin)
                    }
                }
            }
            then("create") {
                then("name" to string()) {
                    then("world" to worlds) {
                        executes {
                            val name: String = it["name"]
                            if (!File(plugin.dataFolder, "field").exists()) return@executes
                            plugin.field[name] = BattleField(name, it["world"]).apply {
                                load(File(File(plugin.dataFolder, "field"), "$name.yml"))
                            }
                        }
                    }
                }
            }
            then("start") {
                then("name" to string()) {
                    executes {
                        plugin.field[it["name"]]?.start(plugin)
                    }
                }
            }
            then("stop") {
                then("name" to string()) {
                    executes {
                        val name: String = it["name"]
                        plugin.field[name]?.let {
                            it.stop()
                            plugin.field.remove(name)
                        }
                    }
                }
            }
            then("phase") {

            }
            then("center") {

            }
        }
    }

    fun giveMap(world: World, plugin: BattleFieldPlugin) {
        Bukkit.getOnlinePlayers().forEach { player ->
            val map = ItemStack(Material.FILLED_MAP)
            val mapMeta = map.itemMeta as MapMeta
            mapMeta.mapView = Bukkit.getServer().createMap(world).apply {
                renderers.forEach(this::removeRenderer)
                mapMeta.isScaling = true
                val renderer = CustomMapRenderer(plugin.dataFolder)
                addRenderer(renderer)
                scale = MapView.Scale.FAR
                centerX = 0
                centerZ = 0
            }
            map.itemMeta = mapMeta
            player.inventory.addItem(map)
        }
    }
}