package ru.barabo.plastic.fio.service

import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.afina.AfinaOrm
import ru.barabo.plastic.fio.entity.FioChangeInfo

object FioChangeService : StoreFilterService<FioChangeInfo>(AfinaOrm, FioChangeInfo::class.java)