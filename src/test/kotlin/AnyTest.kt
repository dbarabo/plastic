import org.apache.log4j.Logger
import org.junit.Test
import ru.barabo.plastic.afina.VersionChecker
import ru.barabo.plastic.schema.entity.selector.SelectAccount
import ru.barabo.plastic.schema.entity.selector.SqlFilterEntity
import java.io.File
import java.net.InetAddress

class AnyTest {

    private val logger = Logger.getLogger(AnyTest::class.simpleName)!!

    @Test
    fun filterTest() {


        val filter = SqlFilterEntity(SelectAccount())

       // logger.info("filter=${filter.filteredPairs}")

        //logger.info("params=${filter.getSqlParams()}")

        filter.getSqlParams().forEach { logger.info(it) }
        logger.info("-----")
        logger.info(Int::class.javaObjectType)
        logger.info(Long::class.javaObjectType)
        logger.info(String::class.javaObjectType)
    }

    //@Test
    fun inetTest() {

        val address = InetAddress.getLocalHost()

//        ips.forEach {
//            logger.error(it)
//        }

        logger.error("address.hostAddress=${address.hostAddress}")
       // logger.error("address.address=${address.address}")
        logger.error("address.hostName=${address.hostName}")
    //    logger.error("address.canonicalHostName=${address.canonicalHostName}")

        logger.error("path=${File(VersionChecker::class.java.protectionDomain.codeSource.location.path).absoluteFile}")
    }

    //@Test
    fun regexpTest() {
        val rep = "*  *   *".replace(" +".toRegex(), "%")
            .replace("\\*+".toRegex(), "%").replace("%%+".toRegex(), "%")

        logger.error("[$rep]")


       // logger.error(/*"01 2".replace(" +".toRegex(), "")*/(null as String?).toLongOrNull())

         //logger.error("12 34   55  5656 6767  .".replace(" +".toRegex(), "%")) //  replace("\\b(?:(%%)(?!\\1))+\\b".toRegex(), ""))

       // "\\b(?:(%)(?!\\1))+\\b"
    }


}