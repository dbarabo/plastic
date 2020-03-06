package ru.barabo.plastic.card.service

import oracle.jdbc.OracleTypes
import ru.barabo.db.EditType
import ru.barabo.db.annotation.ParamsSelect
import ru.barabo.db.annotation.QuerySelect
import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.afina.AfinaQuery
import ru.barabo.plastic.afina.TimerThreadData
import ru.barabo.plastic.card.entity.Card
import ru.barabo.plastic.release.ivr.xml.IvrInfo
import ru.barabo.plastic.release.ivr.xml.IvrXml
import ru.barabo.plastic.release.packet.data.DBStorePacket
import ru.barabo.plastic.release.packet.data.DBStorePacketContent
import ru.barabo.plastic.release.packet.data.PlatinaCashIn
import ru.barabo.plastic.release.packet.data.StatePlasticPacket
import ru.barabo.plastic.release.sms.packet.data.DBStoreSmsPacket.CREATE_FILE_SMS
import ru.barabo.plastic.release.sms.packet.data.DBStoreSmsPacketContent.UPD_PHONE_CARD
import ru.barabo.plastic.schema.entity.selector.SqlFilterEntity
import ru.barabo.plastic.unnamed.data.DBStoreInPath
import ru.barabo.plastic.unnamed.data.sendIvr
import ru.barabo.plastic.unnamed.general.bySqlLike

abstract class StoreCardService(private val modeService: ModeService) :  StoreFilterService<Card>(AfinaOrm, Card::class.java), ParamsSelect, CardService {

    val filter = SqlFilterEntity( Card() )

    init {
        filter.initStoreChecker(this)
    }

    override var filterMode: FilterMode = FilterMode.None
    set(value) {
        field = value
        initData()
    }

    override fun changePin() {

        val ivrInfo = IvrInfo.create(selectedEntity()?.newCardId) ?: throw Exception(ERROR_INCORRECT_CHANGE_PIN)

        IvrXml.startIvrProccessByDb(ivrInfo)
    }

    override fun deleteContent() {
        AfinaQuery.execute(DBStorePacketContent.DELETE_APP_CONTENT, arrayOf(selectedEntity()?.id))

        delete( selectedEntity()!! )
    }

    override fun smsInfoAddRemove(newPhone: String, isAddSmsInfo: Boolean) {
        checkAndSavePhone(newPhone)

        createSmsFile(isAddSmsInfo)

        val plusMinus = if(isAddSmsInfo) "(+)" else "(-)"

        selectedEntity()?.phone = "$newPhone$plusMinus"
        selectedEntity()?.phonePerson = newPhone
        sentRefreshAllListener(EditType.EDIT)
    }

    private fun createSmsFile(isAddSmsInfo: Boolean) {

        val smsType = if(isAddSmsInfo) 1 else 0

        val smsPacket = AfinaQuery.execute(CREATE_SMS, arrayOf(selectedEntity()?.newCardId, smsType),
            intArrayOf(OracleTypes.NUMBER))?.get(0) as? Number ?: throw Exception(DBStorePacket.STATE_NONE_ISSUE)

        DBStoreInPath.saveFilePacket(smsPacket, CREATE_FILE_SMS)
    }

    private fun checkAndSavePhone(newPhone: String) {
        if(newPhone == selectedEntity()?.phonePerson) return

        AfinaQuery.execute(UPD_PHONE_CARD, arrayOf(newPhone, selectedEntity()?.newCardId))
    }

    override fun changeProduct(newProductName: String) {
        if(selectedEntity()?.statePlasticPacket !in listOf(StatePlasticPacket.NEW, StatePlasticPacket.OUT))
            throw Exception(DBStorePacket.ERROR_CHANGE_PRODUCT_STATE)

        AfinaQuery.execute(DBStorePacket.UPD_CHANGE_PRODUCT,
            arrayOf(selectedEntity()?.plasticPack, selectedEntity()?.productName, newProductName))

        selectedEntity()?.productName = newProductName
        sentRefreshAllListener(EditType.EDIT)
    }

    override fun reIssueCards() {

        if(selectedEntity()?.statePlasticPacket != StatePlasticPacket.NEW) throw Exception(DBStorePacket.STATE_NONE_NEW)

        AfinaQuery.execute(DBStorePacket.REISSUE_CARDS, arrayOf(selectedEntity()?.plasticPack))

        selectedEntity()?.state = StatePlasticPacket.OUT.dbValue

        initData()
    }

    override fun createFile() {
        if(selectedEntity()?.statePlasticPacket != StatePlasticPacket.OUT) throw Exception(DBStorePacket.STATE_NONE_ISSUE)

        selectedEntity()?.plasticPack?.let { DBStoreInPath.saveFilePacket(it) } ?: throw Exception(DBStorePacket.STATE_NONE_ISSUE)

        selectedEntity()?.state = StatePlasticPacket.SENT.dbValue

        sentRefreshAllListener(EditType.EDIT)
    }

    override fun createFileForAll() {
        val allPacket = AfinaQuery.execute(EXEC_ALL_OUT_TO_PACKET, null,
            intArrayOf(OracleTypes.NUMBER))?.get(0) as? Number

        allPacket?.let { DBStoreInPath.saveFilePacket(it) } ?: throw Exception(DBStorePacket.STATE_NONE_ISSUE)

        initData()
    }

    override fun toCreateFileState() {

        selectedEntity()?.plasticPack ?: throw Exception(DBStorePacket.PACKET_NOT_SELECTED)

        val stateFrom = if (selectedEntity()?.statePlasticPacket == StatePlasticPacket.SENT_ERROR)
            StatePlasticPacket.SENT_ERROR else StatePlasticPacket.RESPONSE_ERROR_ALL

        val stateToOk = if (selectedEntity()?.statePlasticPacket == StatePlasticPacket.SENT_ERROR)
            StatePlasticPacket.SENT_OK else StatePlasticPacket.RESPONSE_OK_ALL

        AfinaQuery.execute(DBStorePacket.TO_CREATE_FILE_STATE,
            arrayOf(selectedEntity()?.plasticPack, StatePlasticPacket.OUT.dbValue, stateToOk.dbValue, stateFrom.dbValue) )

        selectedEntity()?.state = stateToOk.dbValue

        sentRefreshAllListener(EditType.EDIT)
    }

    override fun toSmsState() {

        selectedEntity()?.plasticPack ?: throw Exception(DBStorePacket.PACKET_NOT_SELECTED)

        AfinaQuery.execute(DBStorePacket.TO_SMS_STATE,
            arrayOf(selectedEntity()?.plasticPack, StatePlasticPacket.OCI_ALL.dbValue, selectedEntity()?.state))

        selectedEntity()?.state = StatePlasticPacket.OCI_ALL.dbValue

        sentRefreshAllListener(EditType.EDIT)
    }

    override fun contentCount(plasticPack: Long?): Int {
        val count = AfinaQuery.selectValue(SELECT_COUNT_CONTENT, arrayOf(plasticPack)) as? Number ?: 0

        return count.toInt()
    }

    override fun goHomeState(): Long? {
        if(selectedEntity()?.statePlasticPacket != StatePlasticPacket.SMS_RESPONSE_OK_ALL_OIA)
            throw Exception(DBStorePacket.STATE_NONE_SMS_OIA)

        val plasticPack = selectedEntity()?.plasticPack

        AfinaQuery.execute(DBStorePacket.UPD_GO_HOME_PACKET, arrayOf(plasticPack))

        selectedEntity()?.state = StatePlasticPacket.CARD_GO.dbValue

        sentRefreshAllListener(EditType.EDIT)

        return plasticPack
    }

    override fun toDopikIsSuccess(): Boolean {
        if(selectedEntity()?.statePlasticPacket != StatePlasticPacket.CARD_GO)
            throw Exception(DBStorePacket.STATE_NONE_GO_HOME)

        AfinaQuery.execute(DBStorePacket.UPD_TO_DOPIK_PACKET,
            arrayOf(selectedEntity()?.plasticPack, selectedEntity()?.departmentName?.toUpperCase() ) )


        val reselect = AfinaQuery.select(SELECT_STATE_CONTENT, arrayOf(selectedEntity()?.id))
        if(reselect.isNotEmpty()) {
            selectedEntity()?.state = (reselect[0][0] as? Number)?.toInt()
            selectedEntity()?.plasticPack = (reselect[0][1] as? Number)?.toLong()
        }

        val statePlastik = selectedEntity()?.statePlasticPacket
        if(statePlastik in listOf(StatePlasticPacket.CARD_SENT_OFFICCES, StatePlasticPacket.CARD_HOME_OFFICCES)) {
            sentRefreshAllListener(EditType.EDIT)
        }

        return statePlastik in listOf(StatePlasticPacket.CARD_SENT_OFFICCES, StatePlasticPacket.CARD_HOME_OFFICCES)
    }

    override fun toGetFromOfficeIsSuccess(): Boolean {
        if(selectedEntity()?.statePlasticPacket != StatePlasticPacket.CARD_SENT_OFFICCES)
            throw Exception(DBStorePacket.STATE_NONE_TO_DOPIKI)

        AfinaQuery.execute(DBStorePacket.UPD_GET_HOMES_PACKET, arrayOf(selectedEntity()?.plasticPack))

        selectedEntity()?.state = (AfinaQuery.selectValue(SELECT_STATE_CONTENT,  arrayOf(
            selectedEntity()?.id) ) as? Number)?.toInt()

        if(selectedEntity()?.statePlasticPacket == StatePlasticPacket.CARD_HOME_OFFICCES) {
            sentRefreshAllListener(EditType.EDIT)
        }

        return selectedEntity()?.statePlasticPacket == StatePlasticPacket.CARD_HOME_OFFICCES
    }

    override fun outClientOnlyIsSuccess(): Boolean {
        if(selectedEntity()?.statePlasticPacket !in listOf(StatePlasticPacket.CARD_HOME_OFFICCES, StatePlasticPacket.PREPARE_CARD_TO_OUT) )
            throw Exception(DBStorePacket.STATE_NONE_GET_HOMES)

        AfinaQuery.execute(DBStorePacketContent.UPD_OUT_CLIENT_CONTENT, arrayOf(selectedEntity()?.id))

        selectedEntity()?.state = (AfinaQuery.selectValue(SELECT_STATE_CONTENT,  arrayOf(
            selectedEntity()?.id) ) as? Number)?.toInt()

        if(selectedEntity()?.statePlasticPacket != StatePlasticPacket.CARD_TO_CLIENT) {
            return false
        }

        selectedEntity()?.id?.let { sendIvr(it) }
        sentRefreshAllListener(EditType.EDIT)

        return true
    }

    override fun addToRegisterCard() {
        if(selectedEntity()?.isCloseAccount != false) throw Exception("Невозможно перевыпустить карту, счет уже закрыт")

        AfinaQuery.execute(CREATE_REISSUE, arrayOf(selectedEntity()?.newCardId))
    }

    override fun prepareCreditCardOut(limit: Number) {
        if(selectedEntity()?.statePlasticPacket != StatePlasticPacket.CARD_HOME_OFFICCES)
            throw Exception(DBStorePacket.STATE_NONE_GET_HOMES)

        AfinaQuery.execute(DBStorePacketContent.PREPARE_OUT_CARD, arrayOf(selectedEntity()?.id, limit))

        selectedEntity()?.state = StatePlasticPacket.PREPARE_CARD_TO_OUT.dbValue
        sentRefreshAllListener(EditType.EDIT)
    }

    override fun beforePreparePlatinaCardOut(): PlatinaCashIn {
        if(selectedEntity()?.statePlasticPacket != StatePlasticPacket.CARD_HOME_OFFICCES)
            throw Exception(DBStorePacket.STATE_NONE_GET_HOMES)

        val outParam = AfinaQuery.executeOut(
            DBStorePacketContent.BEFORE_PREPARE_PLATINA_OUT,
            arrayOf(selectedEntity()?.id),
            intArrayOf(
                OracleTypes.VARCHAR,
                OracleTypes.NUMBER,
                OracleTypes.VARCHAR,
                OracleTypes.VARCHAR,
                OracleTypes.VARCHAR,
                OracleTypes.VARCHAR
            )
        )

        val platinaCashIn = PlatinaCashIn.createFromList(outParam, selectedEntity()?.personName)
            ?: throw Exception("BEFORE_PREPARE_PLATINA_OUT return null")

        if (platinaCashIn.label != null) {
            return platinaCashIn
        }

        selectedEntity()?.state = StatePlasticPacket.PREPARE_CARD_TO_OUT.dbValue
        sentRefreshAllListener(EditType.EDIT)

        return platinaCashIn
    }

    override fun endPreparePlatinaCardOut(platinaCashIn: PlatinaCashIn): Number {
        if(selectedEntity()?.statePlasticPacket != StatePlasticPacket.CARD_HOME_OFFICCES)
            throw Exception(DBStorePacket.STATE_NONE_GET_HOMES)

        val outParam = AfinaQuery.executeOut(DBStorePacketContent.AFTER_PREPARE_PLATINA_OUT,
            arrayOf(selectedEntity()?.id, platinaCashIn.label, platinaCashIn.descriptionDefault),
            intArrayOf(OracleTypes.NUMBER)
        )?.get(0) as? Number ?: throw Exception("endPreparePlatinaCardOut return null")

        TimerThreadData.setPlatinaCashInId(outParam, selectedEntity()?.id!!)

        return outParam
    }

    companion object {
        private const val EXEC_ALL_OUT_TO_PACKET = "{ call OD.PTKB_PLASTIC_AUTO.buildAllReliseCard(?) }"

        private const val SELECT_STATE_CONTENT = "select STATE, PLASTIC_PACK from od.ptkb_plast_pack_content where id = ?"

        private const val CREATE_REISSUE = "{ call OD.PTKB_PLASTIC_AUTO.createReissueContent(?) }"

        private const val CREATE_SMS =  "{ call OD.PTKB_PLASTIC_AUTO.createSmsContent(?, ?, ?) }"

        private const val ERROR_INCORRECT_CHANGE_PIN ="Некорректные данные при смене пин-кода"

        private const val SELECT_COUNT_CONTENT = "select OD.PTKB_PLASTIC_AUTO.getContentCount(?) from dual"
    }

    fun filterReset() {
        filterDataReset()

        initData()
    }

    fun setFilterLike(anyLine: String?) {

        filterDataReset()

        val sqlLike = anyLine?.bySqlLike() ?: return

        val checkDigitOnly = sqlLike.replace("[\\d\\s*%]+".toRegex(), "")

        if(checkDigitOnly.isEmpty()) {
            filter.filterEntity.newCardNumber = sqlLike
        } else {
            filter.filterEntity.personName = sqlLike.toUpperCase()
        }
    }

    private fun filterDataReset() {
        filter.filterEntity.personName = ""
        filter.filterEntity.newCardNumber = ""
    }

    override fun selectParams(): Array<Any?>? =
        filter?.getSqlParams()?.apply {
            this[0] = modeService?.dbValue ?: 0
        }
            ?: arrayOf<Any?>(0, "", "")
}

enum class ModeService(val dbValue: Int?) {
    Register(0),
    InPath(1),
    InHome(2),
    Unclaimed(3),
    OutClient(4),
    Error(5),
    Close(null)
}

object RegisterCardService :  StoreCardService(ModeService.Register)

object InPathStoreCardService :  StoreCardService(ModeService.InPath)

object InHomeStoreCardService :  StoreCardService(ModeService.InHome)

object UnclaimedStoreCardService :  StoreCardService(ModeService.Unclaimed)

object ErrorStoreCardService :  StoreCardService(ModeService.Error)

object CloseStoreCardService : StoreCardService(ModeService.Close), QuerySelect {
    override fun selectQuery(): String = "{ ? = call OD.PTKB_PLASTIC_AUTO.getCardsClosed(?, ?) }"

    override fun selectParams(): Array<Any?>? = filter?.getSqlParams()
        ?.let { arrayOf<Any?>(it[1], it[2]) }
        ?: arrayOf<Any?>("", "")
}

object OutClientStoreCardService : StoreCardService(ModeService.OutClient), QuerySelect {
    override fun selectQuery(): String = "{ ? = call OD.PTKB_PLASTIC_AUTO.getCardsOut(?, ?, ?) }"

    override fun selectParams(): Array<Any?>? = filter?.getSqlParams()
        ?.let { arrayOf<Any?>(filterMode?.dbValue, it[1], it[2]) }
        ?: arrayOf<Any?>(filterMode?.dbValue, "", "")
}