package ru.barabo.plastic.terminal.entity

import ru.barabo.db.annotation.*
import ru.barabo.plastic.schema.entity.account.SEQ_CLASSIFIED
import java.lang.Exception
import java.time.LocalTime

@SelectQuery("{ ? = call OD.PTKB_PLASTIC_TURN.getSchedulerByTerminal(?) }")
@TableName("OD.PTKB_POS_SCHEDULER")
data class Scheduler(
    @ColumnName("ID")
    @SequenceName(SEQ_CLASSIFIED)
    @ColumnType(java.sql.Types.BIGINT)
    var id: Long? = null,

    @ColumnName("STATE")
    var state: Long = 0,

    @ColumnName("TERMINAL_ID")
    var terminalId: String = "",

    @ColumnName("WAIT_TIME")
    var waitTime: Long = DEFAULT_WAIT_TIME,

    @ColumnName("END_TIME")
    var endTime: Long = 68400
) {
    var endTimeDate: String
        get() = LocalTime.ofSecondOfDay(endTime).toString()
        set(value) {
            endTime = LocalTime.parse(value).toSecondOfDay().toLong()
        }

    var isWorkEndTime: Boolean?
        get() = (waitTime != 0L)
        set(value) {
            waitTime = if(value == true) {
                DEFAULT_WAIT_TIME
            } else {
                0L
            }
        }

    var status: String
        get() {
            return when (state) {
                NEW -> "Новый"
                EXECUTE -> "Исполнен"
                else -> "Удален наверное"
            }
        }
        set(_) {}

    fun checkEndTime() {
        if(endTime + waitTime > MAX_SUM_TIME) {
            throw Exception("Максимальное время окончания рабочей смены не может превышать 22:00")
        }
    }
}

private const val NEW: Long = 0
private const val EXECUTE: Long = 1

private const val MAX_SUM_TIME = 86000L

private const val DEFAULT_WAIT_TIME: Long = 2*60*60
