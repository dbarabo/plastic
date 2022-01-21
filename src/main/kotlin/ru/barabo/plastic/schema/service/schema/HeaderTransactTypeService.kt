package ru.barabo.plastic.schema.service.schema

import ru.barabo.afina.AfinaOrm
import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.schema.entity.schema.HeaderTransactType

object HeaderTransactTypeService : StoreFilterService<HeaderTransactType>(AfinaOrm, HeaderTransactType::class.java)