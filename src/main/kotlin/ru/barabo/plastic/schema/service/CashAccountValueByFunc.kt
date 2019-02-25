package ru.barabo.plastic.schema.service

import org.apache.log4j.Logger
import ru.barabo.plastic.afina.AfinaQuery

interface CashAccountValueByFunc {

    val cashedAccountParamsFuncList: MutableMap<String, Long>

    fun calcValueAccountByFunc(funcName: String, paramValues: Array<Any?>): Long {

        val keyString = toKeyValues(funcName, paramValues)

        return cashedAccountParamsFuncList[keyString]?.let { it } ?: addToMap(
            keyString,
            funcName,
            paramValues
        )
    }

    private fun addToMap(keyString: String, funcName: String, paramValues: Array<Any?>): Long {
        cashedAccountParamsFuncList[keyString] = execFunc(funcName, paramValues)

        return cashedAccountParamsFuncList[keyString] ?: -1L
    }

    private fun toKeyValues(funcName: String, paramValues: Array<Any?>): String ="@$funcName@" + paramValues.joinToString("|")

    private fun execFunc(funcName: String, paramValues: Array<Any?>): Long {

        val select = queryFunc(funcName, paramValues)

        val value = try {
            AfinaQuery.selectValue ( select,  paramValues ) as? Number
        } catch (e: Exception) {
            logger.error(e)
            null
        }

        return value?.toLong() ?: -1L
    }

    private fun queryFunc(funcName: String, params: Array<Any?>) =
        "select od.$PACKAGE_FUNC.$funcName( ${params.toQuest()} ) from dual"

    private fun Array<Any?>.toQuest() = joinToString(", ") { "?" }
}

private val logger = Logger.getLogger(CashAccountValueByFunc::class.java.name)