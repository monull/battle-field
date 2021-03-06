package io.github.monull.battlefield.map

import org.bukkit.entity.Player
import org.bukkit.map.*
import java.io.File
import javax.imageio.ImageIO

class CustomMapRenderer(val dataFolder: File) : MapRenderer() {
    override fun render(map: MapView, canvas: MapCanvas, player: Player) {
        val location = player.location
        val cursorCollection = MapCursorCollection()
        val x = location.x / 500 * 128
        val z = location.z / 500 * 128
        val yaw = player.location.yaw
        val mapYaw = if(yaw >= 0) (yaw / 360 * 15).toInt().toByte() else (15 + (yaw / 360 * 15)).toInt().toByte()
        if(cursorCollection.size() == 0) {
            cursorCollection.addCursor(x.toInt(), z.toInt(), mapYaw, MapCursor.Type.WHITE_POINTER.value, true)
        }
        val mapCursor = cursorCollection.getCursor(0)
        mapCursor.x = x.toInt().toByte()
        mapCursor.y = z.toInt().toByte()
        mapCursor.direction = mapYaw
        canvas.cursors = cursorCollection

        val folder = File(dataFolder, "map").also { it.mkdirs() }
        val image = ImageIO.read(File(folder, "map.png"))
        canvas.drawImage(0, 0, image.getSubimage(0, 0, 128, 128))
    }
}