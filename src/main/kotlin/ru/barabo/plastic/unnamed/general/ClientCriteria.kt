package ru.barabo.plastic.unnamed.general

import java.sql.Date
import java.text.SimpleDateFormat

data class ClientCriteria(val id: Long? = null,
                          val fio: String = "",
                          val address: String = "",
                          val birthDay: Date? = null,
                          val passportLine: String = "",
                          val passportNumber: String = "",
                          val phone: String = "") {

    fun toSqlArray(): Array<Any?> =
        arrayOf(toSql<Long>(id), fio.bySqlLikePlus(), address.bySqlLike2Plus(), toSql<Date>(birthDay),
            passportLine.parsePasportLine(), passportNumber.bySqlLikePlus(), phone.bySqlLike2Plus())

    fun isEmptyData(): Boolean =
        id == null &&
        fio.trim().length < 4 &&
        address.trim().length < 10 &&
        birthDay == null &&
        passportLine.trim().length < 4 &&
        passportNumber.trim().length < 5 &&
        phone.trim().length <= 5

    companion object {
        val emptyClientCriteria = ClientCriteria()

        fun fromStrings(id: String?, fio: String?, address: String?, birthDay: String?, passportLine: String?,
                        passportNumber: String?, phone: String?): ClientCriteria =
            ClientCriteria(id?.parseLong(), fio?:"", address?:"", birthDay?.parseDate(),
                passportLine?:"", passportNumber?:"", phone?:"")
    }
}

inline fun <reified T> toSql(value: Any?): Any = value?.let { it } ?: T::class.java

private fun String.parseLong(): Long? = this.replace(" +".toRegex(), "").toLongOrNull()

private fun String.parseDate(): Date? {

    val date= replace("\\D+".toRegex(), "").let { dat ->
        when {
            dat.length < 4 -> return null

            dat.length in 4..5 -> dat.let { it.substring(0..1) +  "." + it.substring(2..3) + ".2000" }

            dat.length in 6..7 -> dat.let { it.substring(0..1) +  "." + it.substring(2..3) + ".20" + it.substring(4..5) }

            else -> dat.let { it.substring(0..1) +  "." + it.substring(2..3) + "." + it.substring(4..7) }
        }
    }

    return try {
        Date(SimpleDateFormat("dd.MM.yyyy").parse(date).time)
    } catch (e: Exception) {
        null
    }
}

private fun String.parsePasportLine(): String = toDigitsOnly().let {line ->
        when {
            line.length < 2   -> ""
            line.length == 2  -> "$line%"
            else -> line.let {  it.substring(0..1) + "%" + it.substring(2..3) }
        }
    }

fun String.bySqlLike() = replace(" +".toRegex(), "%")
    .replace("\\*+".toRegex(), "%").replace("%%+".toRegex(), "%")

fun String.bySqlLikePlus() = bySqlLike().let { if(it.isEmpty() || it == "%")"" else "$it%"  }

fun String.bySqlLike2Plus() = bySqlLike().let { if(it.isEmpty() || it == "%")"" else "%$it%"  }

fun String.toDigitsOnly() = replace("\\D+".toRegex(), "")
