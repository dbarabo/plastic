import org.apache.log4j.Logger
import org.junit.Test
import ru.barabo.plastic.afina.VersionChecker
import java.io.File
import java.net.InetAddress

class AnyTest {

    val logger = Logger.getLogger(AnyTest::class.simpleName)!!

    @Test
    fun inetTest() {

        val address = InetAddress.getLocalHost()

        val ips = InetAddress.getAllByName(address.canonicalHostName)

        ips.forEach {
            logger.error(it)
        }

        logger.error("address.hostAddress=${address.hostAddress}")
        logger.error("address.address=${address.address}")
        logger.error("address.hostName=${address.hostName}")
        logger.error("address.canonicalHostName=${address.canonicalHostName}")

        logger.error("path=${File(VersionChecker::class.java.protectionDomain.codeSource.location.path).absoluteFile}")
    }
}