package ru.barabo.plastic.main.resources

import org.apache.log4j.Logger
import java.net.URL
import javax.swing.ImageIcon

object ResourcesManager {
    //val logger = LoggerFactory.getLogger(ResourcesManager::class.java)!!

    val logger = Logger.getLogger(ResourcesManager::class.simpleName)!!

    private val icoHash :HashMap<String, ImageIcon> = HashMap()

    private const val ICO_PATH = "/ico/"

    @JvmStatic
    fun getIcon(icoName: String): ImageIcon {

        val ico = icoHash[icoName]

        if(ico != null) {
            return ico
        }

        val newIco =  loadIcon(icoName)

        logger.info("ResourcesManager newIco=$newIco")

        icoHash[icoName] = newIco

        return newIco
    }

    private fun loadIcon(icoName :String) : ImageIcon = ImageIcon(pathResource("$ICO_PATH$icoName.png"))

    private fun pathResource(fullPath: String): URL {

        val path = ResourcesManager::class.java.getResource(fullPath).toExternalForm()

        //val path = ResourcesManager::class.java.getResource(fullPath).toURI().toString()
        logger.info("ResourcesManager pathResource=$path")
        return URL(path)
    }
}