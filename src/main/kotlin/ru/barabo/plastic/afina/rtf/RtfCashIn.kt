package ru.barabo.plastic.afina.rtf

import ru.barabo.total.report.rtf.RtfAfinaData

class RtfCashIn(private var idCashIn: Number) : RtfAfinaData {

    override fun procedureName(): String = "BBR_DOC_CASHINKO_1433U_318_14"

    override fun procedureCallSql(): String = "{ call BBR.BBR_DOC_CASHINKO_1433U_318_14(?) }"

    override fun paramCall(): Array<Any> = arrayOf(idCashIn)

    override fun bbrId(): Long = 1048755505L
}