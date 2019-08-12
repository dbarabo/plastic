package ru.barabo.plastic.unnamed.gui.dialog

import ru.barabo.plastic.schema.gui.account.groupPanel
import ru.barabo.plastic.schema.gui.schema.AbstractDialog
import ru.barabo.plastic.schema.gui.schema.comboBox
import ru.barabo.plastic.schema.gui.selector.textFieldHorizontal
import ru.barabo.plastic.unnamed.data.RowFieldInPath
import ru.barabo.plastic.unnamed.entity.Department
import ru.barabo.plastic.unnamed.general.FilteredStoreInHome
import ru.barabo.plastic.unnamed.service.DepartmentService
import java.awt.Component
import javax.swing.JComboBox


class ChangeOfficeCard(component: Component, private val store: FilteredStoreInHome<RowFieldInPath>) : AbstractDialog(component, "Смена офиса карты") {

    private val departmentList: JComboBox<Department>

    init {
        groupPanel("Офис назначения карты", 0, 2, 0).apply {

            textFieldHorizontal("№ Перемещаемой карты", 0).apply {
                text = store.row.getCardNumber()
                this.isEditable = false
            }

            comboBox("Офис назначения", 1, DepartmentService.elemRoot()).apply { departmentList = this }
        }

        createOkCancelButton(2)

        packWithLocation()
    }

    override fun okProcess() {

        store.moveCardToDepartment(departmentList.selectedItem as? Department)
    }
}