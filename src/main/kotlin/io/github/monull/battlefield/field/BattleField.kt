package io.github.monull.battlefield.field

import io.github.monull.battlefield.plugin.BattleFieldPlugin
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.scheduler.BukkitTask
import java.io.File

class BattleField(val name: String, val world: World) {
    var phase = 0
    var totalPhase = 0
    var config: ConfigurationSection? = null
    var task: BukkitTask? = null

    fun save(config: ConfigurationSection) {
        config.createSection("phase")
    }
    fun phase(phase: Int) {

    }
    fun load(file: File) {
        val config = YamlConfiguration.loadConfiguration(file)
        this.config = config
    }
    fun start(plugin: BattleFieldPlugin) {
        task = Bukkit.getScheduler().runTaskTimer(plugin, BattleFieldScheduler(config!!, this), 0L, 1L)
    }
    fun stop() {
        task?.cancel()
    }
}

class BattleFieldScheduler(val config: ConfigurationSection, val battleField: BattleField) : Runnable {
    private var ticks = 0
    private var phase = 0
    private var totalTime = 1000
    private var bossBar = Bukkit.createBossBar("전장 축소까지", BarColor.BLUE, BarStyle.SEGMENTED_10).apply {
        isVisible = true
        Bukkit.getOnlinePlayers().forEach(this::addPlayer)
    }
    override fun run() {
        ticks++
        when (ticks) {
            1 -> {
                phase = battleField.phase
                totalTime = config.getConfigurationSection(phase.toString())?.getInt("total-time")!!
            }
            totalTime -> {
                ticks = 0
                phase++
            }
        }
        val config = config.getConfigurationSection(phase.toString())!!
        val shrink = config.getBoolean("shrink")

        if (shrink) {
            val totalTime = config.getInt("total-time")
            bossBar.setTitle("전장 축소 중... ${(totalTime - ticks) / 2000}.${(totalTime - ticks) / 200}")
            battleField.world.worldBorder.let {
                it.setSize(it.size - 1, 5)
            }
            bossBar.style = BarStyle.SOLID
            bossBar.color = BarColor.RED
            bossBar.progress = ((totalTime - ticks) / totalTime).toDouble().coerceIn(0.0, 1.0)
        } else {
            bossBar.style = BarStyle.SEGMENTED_10
            bossBar.color = BarColor.BLUE
            bossBar.setTitle("전장 축소까지 ${(ticks / 20)}.${(ticks / 2) % 10}초")
            bossBar.progress = (ticks.toDouble() / config.getInt("total-time")).coerceIn(0.0, 1.0)
        }
    }
}