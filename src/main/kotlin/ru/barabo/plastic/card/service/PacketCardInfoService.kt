package ru.barabo.plastic.card.service

import ru.barabo.db.annotation.ParamsSelect
import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.afina.AfinaQuery
import ru.barabo.plastic.card.entity.PacketCardInfo

object PacketCardInfoService :  StoreFilterService<PacketCardInfo>(AfinaOrm, PacketCardInfo::class.java),ParamsSelect {

    private var packet: Number? = null

    private var departmentLabel: String? = null

    fun reSelect(packet: Number?, departmentLabel: String?) {
        this.packet = packet
        this.departmentLabel = departmentLabel
        initData()
    }

    fun reSelectByDepartmentUser(packet: Number?) {
        this.packet = packet
        this.departmentLabel = AfinaQuery.getUserDepartment().departmentName
        initData()
    }

    override fun selectParams(): Array<Any?>? = arrayOf(packet, departmentLabel)
}

