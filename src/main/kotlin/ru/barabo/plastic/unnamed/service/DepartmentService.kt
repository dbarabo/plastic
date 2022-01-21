package ru.barabo.plastic.unnamed.service

import ru.barabo.afina.AfinaOrm
import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.unnamed.entity.Department

object DepartmentService : StoreFilterService<Department>(AfinaOrm, Department::class.java)