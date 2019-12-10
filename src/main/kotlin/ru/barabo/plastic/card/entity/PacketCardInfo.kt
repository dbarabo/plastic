package ru.barabo.plastic.card.entity

import ru.barabo.db.annotation.ColumnName
import ru.barabo.db.annotation.SelectQuery

@SelectQuery("{ ? = call OD.PTKB_PLASTIC_AUTO.getCardPacketInfo(?, ?) }")
data class PacketCardInfo(
    @ColumnName("ID")
    var id: Long? = null,

    @ColumnName("PERSON")
    var personId: Long? = null,

    @ColumnName("LABEL")
    var personName: String? = null,

    @ColumnName("EMBOSNAME")
    var embosName: String? = null,

    @ColumnName("CARDNUM")
    var cardNumber: String? = null)