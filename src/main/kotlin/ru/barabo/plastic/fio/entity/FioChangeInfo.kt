package ru.barabo.plastic.fio.entity

import ru.barabo.db.annotation.ColumnName
import ru.barabo.db.annotation.ParamsSelect
import ru.barabo.db.annotation.SelectQuery
import ru.barabo.plastic.fio.service.FioChangeService

@SelectQuery("""
    select c.CLASSIFIED, od.GetQuestCodeValue('Form', 'LastName', c.CLASSIFIED) LAST_NAME,
    od.GetQuestCodeValue('Form', 'FirstName', c.CLASSIFIED) FIRST_NAME,
    od.GetQuestCodeValue('Form', 'SecondName', c.CLASSIFIED) MIDDLE_NAME,

    od.PTKB_PLASTIC.getAddressByPCFormat(c.CLASSIFIED) ADDRESS,

    od.GetJurQuestCodeValue(od.PTKB_PLASTIC.getCodeDocumentTypeAfina(c.CLASSIFIED), 'Series', c.CLASSIFIED) LINE_DOC,
    od.GetJurQuestCodeValue(od.PTKB_PLASTIC.getCodeDocumentTypeAfina(c.CLASSIFIED), 'Number', c.CLASSIFIED) NUMBER_DOC,

    (select label from od.juridicalbook where CODE = od.PTKB_PLASTIC.getCodeDocumentTypeAfina(c.CLASSIFIED) ) TYPE_DOC,

    od.GetQuestCodeValue('CardAppPersonForm', 'M_MaidName', c.CLASSIFIED) CODE_WORD,

    od.PTKB_PLASTIC.getMobilePhoneByPerson(c.CLASSIFIED) PHONE,

    od.PTKB_PLASTIC_AUTO.getPcPersonOnly(c.CLASSIFIED) ID_PC

     from od.client c
    where c.classified = ?
""")
data class FioChangeInfo(
    @ColumnName("CLASSIFIED")
    var id: Long? = null,

    @ColumnName("LAST_NAME")
    var surName: String? = null,

    @ColumnName("FIRST_NAME")
    var name: String? = null,

    @ColumnName("MIDDLE_NAME")
    var middleName: String? = null,

    @ColumnName("ADDRESS")
    var address: String? = null,

    @ColumnName("TYPE_DOC")
    var documentType: String? = null,

    @ColumnName("LINE_DOC")
    var documentLine: String? = null,

    @ColumnName("NUMBER_DOC")
    var documentNumber: String? = null,

    @ColumnName("CODE_WORD")
    var codeWord: String? = null,

    @ColumnName("PHONE")
    var phone: String? = null,

    @ColumnName("ID_PC")
    var idInProcCenter: String? = null

) : ParamsSelect {

    override fun selectParams(): Array<Any?>? = arrayOf(selectClientId?:Long::class.javaObjectType)


    companion object {
        var selectClientId: Long? = null
            set(value) {
                field = value

                FioChangeService.initData()
            }
    }
}