import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.log4j.Logger
import org.junit.Test
import ru.barabo.db.annotation.QuerySelect
import ru.barabo.plastic.schema.entity.selector.SelectAccount
import ru.barabo.plastic.schema.entity.selector.SqlFilterEntity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.URL
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDateTime
import java.util.*
import java.util.regex.Pattern
import javax.net.ssl.HttpsURLConnection

import org.apache.http.conn.ssl.*
import org.apache.http.impl.client.HttpClientBuilder


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
    fun testPdfReader() {

        //val file = File("C:/report/12304241210320002.pdf")
        //val document: PDDocument = PDDocument.load(file)
        //val stripper = PDFTextStripper()
        //val text: String = stripper.getText(document)
        //logger.error(text)
        //document.close()

        /*val inputFile = File("C:/report/12304241210320002.pdf")
        val reader = PdfReader(inputFile.canonicalPath)

        for(page in 1..reader.numberOfPages) {
            val strategy = SimpleTextExtractionStrategy()

            val text = PdfTextExtractor.getTextFromPage(reader, page, strategy)

            logger.error(text)
        }*/
    }

    //@Test
    fun testPattern() {
        val pattern = Pattern.compile("\\[([0-9]+)\\]")

        val matcher = pattern.matcher("3] ATM Выдача наличных")

        if(matcher.find()) {
            logger.error("group=${matcher.group(1)}")
        } else {
            logger.error("no find")
        }
    }

    //@Test
    fun testDecimalFormat() {

        val otherSymbols = DecimalFormatSymbols().apply {
            decimalSeparator = ','
            groupingSeparator = ' '
        }
        val format = DecimalFormat("#,###.##", otherSymbols)

        //val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat

        val value = "1 876 453,30"

        val numb = format.parse(value)

        logger.error("numb=$numb")

        //val value: Long= 100048
    }

    //@Test
    fun testPrimitive() {

        val dayByMoscow = "%02d".format( LocalDateTime.now().minusHours(7).minusDays(11).dayOfMonth )

        logger.error("dayByMoscow=$dayByMoscow")


//        logger.error(Long::class.javaObjectType)
//        val javaType = 0L.javaClass
//        logger.error("valueToJava javaType=${javaType.isPrimitive} ${javaType.typeName}  ${javaType.name}}")
    }

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

        val x = select.uppercase(Locale.getDefault()).replaceFirst("\\sWHERE\\s".toRegex(), "\nWHERE id = ?\n and ")

        val y = select.uppercase(Locale.getDefault()).replaceFirst("\\sORDER\\sBY\\s".toRegex(), "\nWHERE id = ? \nORDER BY ")

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

    //@Test
    fun substringTest() {
        val x = "F1027700466640_060921_P_0038".substringAfter("_Z_")

        logger.error("x=$x")
    }

    @Test
    fun tlsTest() {
        System.setProperty("javax.net.ssl.keyStore", "E:/kotlin/sert/bki.jks")
        System.setProperty("javax.net.ssl.keyStorePassword", "123456")


        val httpsURL = "https://reports.nbki.ru/qbch/"


        val sslConnectionSocketFactory = SSLConnectionSocketFactory(SSLContexts.createDefault(), arrayOf("TLSv1.2"),
        null, SSLConnectionSocketFactory.getDefaultHostnameVerifier())

        val httpClient = HttpClientBuilder
            .create()
            .setSSLSocketFactory(sslConnectionSocketFactory)
            .build()

        val request = HttpGet(httpsURL)
        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:63.0) Gecko/20100101 Firefox/63.0")
        //request.setEntity(se)
        try {
                val response = httpClient.execute(request)
        } catch (e: Exception) {

            logger.error("inputLine", e)

            e.printStackTrace()
        }


/*        val myUrl: URL = URL(httpsURL)
        val conn: HttpsURLConnection = myUrl.openConnection() as HttpsURLConnection

        conn.inputStream.use { tt ->
            val isr = InputStreamReader(tt)
            val br = BufferedReader(isr)

            var inputLine: String?
            while ((br.readLine().also { inputLine = it }) != null) {
                logger.error(inputLine)
            }
        }*/
    }
}
