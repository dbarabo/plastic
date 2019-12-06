package ru.barabo.plastic.card.entity

import ru.barabo.db.annotation.*
import ru.barabo.plastic.release.packet.data.ClassCard
import ru.barabo.plastic.release.packet.data.StatePlasticPacket
import ru.barabo.plastic.schema.entity.account.SEQ_CLASSIFIED
import ru.barabo.total.db.impl.formatter.CardFormat
import java.sql.Timestamp
import java.text.SimpleDateFormat

@Suppress("UNUSED_PARAMETER")
@SelectQuery("{ ? = call OD.PTKB_PLASTIC_AUTO.getCardsByMode(?, ?, ?) }")
@TableName("OD.PTKB_PLAST_PACK_CONTENT")
data class Card(
    @ColumnName("ID")
    @SequenceName(SEQ_CLASSIFIED)
    var id: Long? = null,

    @ColumnName("PLASTIC_PACK")
    var plasticPack: Long? = null,

    @ColumnName("TYPE_PACK")
    @Filtered(0, 0) // stub for mode
    var typePack: Int? = null,

    @ColumnName("APP_CARD")
    var applicationId: Long? = null,

    @ColumnName("OLD_CARD")
    var oldCardId: Long? = null,

    @ColumnName("STATE")
    var state: Int? = null,

    @ColumnName("CUSTOMER")
    var customer: Long? = null,

    @ColumnName("CREATED")
    var created: Timestamp? = null,

    @ColumnName("UPDATED")
    var updated: Timestamp? = null,

    @ColumnName("OLD_CARD_NUM")
    var oldCardNumber: String? = null,

    @ColumnName("OLD_END_CARD")
    var oldCardEnd: Timestamp? = null,

    @ColumnName("MSG_OIA")
    var messageResponse: String? = null,

    @ColumnName("PRODUCT_TO")
    var cardType: Long? = null,

    @ColumnName("PRODUCT_NAME")
    var productName: String? = null,

    @ColumnName("PERSON")
    var person: Long? = null,

    @ColumnName("PERSON_NAME")
    @Filtered(1, 1)
    var personName: String? = null,

    @ColumnName("NEW_CARD")
    var newCardId: Long? = null,

    @ColumnName("CARDNUM")
    @Filtered(2, 2)
    var newCardNumber: String? = null,

    @ColumnName("END_CARD")
    var newCardEnd: Timestamp? = null,

    @ColumnName("PHONE")
    var phone: String? = null,

    @ColumnName("DEPARTMENT")
    var departmentName: String? = null,

    @ColumnName("APP_DOC_NUMBER")
    var applicationNumber: String? = null,

    @ColumnName("CLASS_TYPE")
    var classType: Int? = null,

    @ColumnName("UPDATER")
    var updater: String? = null,

    @ColumnName("IS_CLOSE_ACCOUNT")
    var closeAccount: Int? = null) {

    private val cardFormat = CardFormat()

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy") //DateTimeFormatter.ofPattern("dd.MM.yyyy")

    private val dateCardToFormat = SimpleDateFormat("MM/yy")

    var isCloseAccount: Boolean?
    get() = closeAccount?.let { it != 0 }
    set(value) {}

    val classCard: ClassCard?
        get() = classType?.let { ClassCard.classCardByDbValue(it) }

    val statePlasticPacket: StatePlasticPacket?
        get() = state?.let { StatePlasticPacket.getStateByDbValue(it) }

    var stateValue: String?
        get() = state?.let { StatePlasticPacket.getStateByDbValue(it)?.label }
        set(value) {}

    var cardNumberByFormat: String
        get() = newCardNumber?.let { cardFormat.format(it) } ?: ""
        set(value) {}

    var oldCardNumberByFormat: String?
        get() =  oldCardNumber?.let { cardFormat.format(it) }
        set(value) {}

    var updatedFormat: String
        get()  = updated?.let { dateFormat.format(it) } ?: ""
        set(value) {}

    var oldCardEndFormat: String
        get() = oldCardEnd?.let { dateCardToFormat.format(it) } ?: ""
        set(value) {}

    var newCardEndFormat: String
        get() = newCardEnd?.let { dateCardToFormat.format(it) } ?: ""
        set(value) {}

}

