package ru.barabo.plastic.fio.service

import oracle.jdbc.OracleTypes
import ru.barabo.afina.AfinaOrm
import ru.barabo.afina.AfinaQuery
import ru.barabo.afina.clobToString
import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.fio.entity.FioChangeInfo
import ru.barabo.plastic.unnamed.data.unicFileName
import java.io.File
import java.nio.charset.Charset
import java.sql.Clob

object FioChangeService : StoreFilterService<FioChangeInfo>(AfinaOrm, FioChangeInfo::class.java) {

    fun sendBtrt30() {
        val client = selectedEntity()

        if(client?.idInProcCenter?.isEmpty() != false) throw Exception("Нет ID в ПЦ для этого клиента")

        if(client.id == null) throw Exception("Должен быть выбран клиент")

        createBtrt30File(CREATE_BTRT30_BY_CLIENT, client.id!!, client.codeWord?:"", client.phone?:"")
    }

    private const val CREATE_BTRT30_BY_CLIENT = "{ call od.PTKB_PLASTIC_AUTO.createBtrt30ForClient(?, ?, ?, ?, ?) }"

}

private fun createBtrt30File(query: String,
                             clientId: Long,
                             codeWord: String,
                             phone: String): File {
    val file = unicFileName("")

    val params: Array<Any?> = arrayOf(clientId, file.name, codeWord, phone)

    val clob = AfinaQuery.execute(query, params, intArrayOf(OracleTypes.CLOB))!![0] as Clob

    file.writeText(clob.clobToString(), charset = Charset.forName("cp1251"))

    return file
}