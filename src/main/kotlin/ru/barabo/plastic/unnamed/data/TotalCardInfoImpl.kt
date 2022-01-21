package ru.barabo.plastic.unnamed.data

import ru.barabo.afina.AfinaQuery
import ru.barabo.plastic.unnamed.general.KeyValue
import ru.barabo.plastic.unnamed.general.TotalCardInfo

class TotalCardInfoImpl() : TotalCardInfo {

    override fun getInstantIssueProducts(): List<KeyValue> = AfinaQuery.selectCursor(SELECT_INSTANCE_PRODUCTS)
            .map { KeyValue((it[0] as? Number)?.toLong()?:0L, (it[1] as? String)?:"")}

    companion object {
        private const val SELECT_INSTANCE_PRODUCTS= "{ ? = call od.PTKB_PLASTIC_AUTO.getInstantIssueProductCards }"
    }
}