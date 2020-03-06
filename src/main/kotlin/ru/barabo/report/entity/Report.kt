package ru.barabo.report.entity

import ru.barabo.db.annotation.*
import ru.barabo.plastic.schema.entity.account.SEQ_CLASSIFIED
import java.io.File
import java.sql.Timestamp
import java.util.*

@SelectQuery("""select r.id, r.directory, r.state, r.state, r.name, r.template_name, 
  r.version_id, r.creator, r.created, r.updater, r.updated
from od.xls_report r
where r.directory = ?""")
@TableName("OD.XLS_REPORT")
data class Report(
    @ColumnName("ID")
    @SequenceName(SEQ_CLASSIFIED)
    @ColumnType(java.sql.Types.BIGINT)
    var id: Long? = null,

    @ColumnName("DIRECTORY")
    @ColumnType(java.sql.Types.BIGINT)
    var directory: Long? = null,

    @ColumnName("STATE")
    var state: Long = 0,

    @ColumnName("NAME")
    var name: String = "",

    @ColumnName("TEMPLATE_NAME")
    var fileName: String = "",

    @ColumnName("VERSION_ID")
    var versionId: Long = 0,

    @ColumnName("CREATOR")
    var creator: String = "",

    @ColumnName("UPDATER")
    var updater: String = "",

    @ColumnName("CREATED")
    var created: Timestamp = Timestamp(Date().time),

    @ColumnName("UPDATED")
    var updated: Timestamp = Timestamp(Date().time),

    private var templateFile: File? = null,

    private var owner: Directory? = null
)

