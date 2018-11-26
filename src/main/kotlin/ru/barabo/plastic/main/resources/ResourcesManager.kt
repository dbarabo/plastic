package ru.barabo.plastic.main.resources

import java.net.URL
import javax.swing.ImageIcon

object ResourcesManager {
    private val icoHash :HashMap<String, ImageIcon> = HashMap()

    private const val ICO_PATH = "/ico/"

    @JvmStatic
    fun getIcon(icoName: String): ImageIcon? =
        icoHash[icoName] ?:  loadIcon(icoName)?.apply { icoHash[icoName] = this }

    private fun loadIcon(icoName :String): ImageIcon? = pathResource("$ICO_PATH$icoName.png")?.let { ImageIcon(it) }

    private fun pathResource(fullPath: String): URL? {

        val path = ResourcesManager::class.java.getResource(fullPath)?.toExternalForm()

        return path?.let{ URL(it) }
    }
}