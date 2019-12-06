import org.apache.log4j.Logger
import ru.barabo.db.annotation.QuerySelect
import ru.barabo.plastic.afina.VersionChecker
import ru.barabo.plastic.schema.entity.selector.SelectAccount
import ru.barabo.plastic.schema.entity.selector.SqlFilterEntity
import java.io.File
import java.net.InetAddress


abstract class A() {
    fun testAssign(): Boolean {
        return QuerySelect::class.java.isAssignableFrom(this::class.java)
    }
}

class B : A(), QuerySelect {
    override fun selectQuery(): String = ""
}

class AnyTest {

    private val logger = Logger.getLogger(AnyTest::class.simpleName)!!

    private val select = """
select t.id, t.NAME, t.HEADER_NAME, t.TRANS_TYPE, t.IS_OUT_TYPE, s.condition, v.name condition_name
from od.ptkb_plastic_transact_type t,
od.ptkb_transact_schema s,
od.ptkb_transact_variable v
where t.HEADER_NAME = ?
and s.TRANSACT_TYPE(+) = t.TRANS_TYPE
and v.id(+) = s.condition
and (s.id is null or s.id = (select min(s2.id) from od.ptkb_transact_schema s2 where s2.transact_type = t.TRANS_TYPE))
order by t.id
"""

    //@Test
    fun testRegExDigit() {
        val value = "234 3434 535 *567%5567 "

        // val reg = "[0-9\\%\\*]"

        //logger.info(Pattern.compile("^(?!.*[0-9]).*$").matcher(value).find())


        val x = value.replace("[\\d\\s*%]+".toRegex(), "")


        logger.info("|$x|")

        //value.replace("\\D+".toRegex(), "")
    }

    //@Test
    fun testAssignClass() {
        val b = B()

        logger.error("b=${b.testAssign()}")
    }

    //@Test
    fun reqexpTestReplace() {



        val x = select.toUpperCase().replaceFirst("\\sWHERE\\s".toRegex(), "\nWHERE id = ?\n and ")

        val y = select.toUpperCase().replaceFirst("\\sORDER\\sBY\\s".toRegex(), "\nWHERE id = ? \nORDER BY ")

        logger.error(x)

        logger.error(y)
    }

    //@Test
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
    fun regExpFindFirst() {
        val find = "{ ? = call od.PTKB_PLASTIC_AUTO.getAppCardItem( ? ) }"

        val rep = find.replace("(\\s|\\{)+".toRegex(), "")

        //val x = find.matches("([{?=]){3}".toRegex())

        logger.error("rep=$rep")
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