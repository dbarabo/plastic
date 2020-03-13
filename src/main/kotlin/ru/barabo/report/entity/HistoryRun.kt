package ru.barabo.report.entity

import ru.barabo.db.annotation.*
import ru.barabo.plastic.schema.entity.account.SEQ_CLASSIFIED
import java.sql.Timestamp
import java.util.*

@SelectQuery("""select h.id, h.report, h.state, h.VERSION_ID, h.RUNNER, h.RUNNED, h.ERROR, h.FILE_NAME, h.WORK_PLACE
from od.XLS_HISTORY_RUN h
where h.REPORT = ?
order by h.id desc""")
@TableName("OD.XLS_HISTORY_RUN")
data class HistoryRun(
    @ColumnName("ID")
    @SequenceName(SEQ_CLASSIFIED)
    @ColumnType(java.sql.Types.BIGINT)
    var id: Long? = null,

    @ColumnName("REPORT")
    @ColumnType(java.sql.Types.BIGINT)
    var report: Long? = null,

    @ColumnName("STATE")
    var state: Long = 0,

    @ColumnName("VERSION_ID")
    var versionId: Long = 0,

    @ColumnName("RUNNER")
    var runner: String = "",

    @ColumnName("RUNNED")
    var runned: Timestamp = Timestamp(Date().time),

    @ColumnName("ERROR")
    var error: String = "",

    @ColumnName("FILE_NAME")
    var fileName: String = ""
) {
    var info: String
    get() = "<html>${dateTimeFormat.format(runned)}<br>$runner<br>${fileName.substringAfterLast('/')}</html>"
    set(value) {}
}

private val dateTimeFormat = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss")