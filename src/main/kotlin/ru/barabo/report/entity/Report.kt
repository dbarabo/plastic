package ru.barabo.report.entity

import ru.barabo.db.annotation.*
import ru.barabo.plastic.afina.AfinaQuery
import ru.barabo.plastic.schema.entity.account.SEQ_CLASSIFIED
import ru.barabo.report.service.DirectoryService
import ru.barabo.total.report.rtf.RtfReport
import java.io.File
import java.sql.Timestamp
import java.util.*

@SelectQuery("""select r.id, r.directory, r.state, r.name, r.template_name, 
  r.version_id, r.creator, r.created, r.updater, r.updated,
  (select count(*) from od.xls_history_run h where h.report = r.id) COUNT_
from od.xls_report r
where r.directory = ?
order by r.id""")
@TableName("OD.XLS_REPORT")
data class Report (
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

    @ColumnName("COUNT_")
    @ReadOnly
    var count: Long = 0,

    var templateFile: File? = null,

    var owner: Directory? = null
) {
    val nameWithCount: String
    get() = "$name ($count)"

    fun getTemplate(saveDirectory: File = defaultTemplateDirectory() ): File {
        if(id == null || fileName.isBlank()) throw Exception("must be report.id is not null and report.template is not empty")

        templateFile = File("$saveDirectory/$fileName")

        owner = DirectoryService.directoryById(directory)

        return AfinaQuery.selectBlobToFile(SELECT_BLOB_TEMPLATE_REPORT, arrayOf(id), templateFile!!)
    }

    fun uploadFile() {
        if(id == null ||templateFile?.exists() != true) throw Exception("must be exists template file $templateFile")

        AfinaQuery.execute(UPDATE_BLOB_BY_FILE, arrayOf(templateFile, id))
    }
}

private const val UPDATE_BLOB_BY_FILE = "update OD.XLS_REPORT r set TEMPLATE = ? where r.id = ?"

private const val SELECT_BLOB_TEMPLATE_REPORT = "select r.TEMPLATE from OD.XLS_REPORT r where r.id = ?"

fun defaultTemplateDirectory(): File = defaultDirectory("temp")

fun defaultReportDirectory(): File = defaultDirectory("xls")

private fun defaultDirectory(dirName: String): File {
    val directory = File("${RtfReport.getDefaultToDirectory().absolutePath}/$dirName")

    if(!directory.exists()) {
        directory.mkdirs()
    }

    return directory
}
