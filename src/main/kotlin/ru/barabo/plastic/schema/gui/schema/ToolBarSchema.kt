package ru.barabo.plastic.schema.gui.schema

import ru.barabo.plastic.schema.entity.schema.HeaderTransactType
import ru.barabo.plastic.schema.gui.account.processShowError
import ru.barabo.plastic.schema.gui.selector.FilterKeyLister
import ru.barabo.plastic.schema.service.schema.HeaderTransactTypeService
import ru.barabo.plastic.schema.service.schema.SchemaService
import ru.barabo.plastic.unnamed.general.bySqlLikePlus
import ru.barabo.total.gui.any.AbstractTopToolBar
import ru.barabo.total.gui.any.ButtonKarkas
import java.awt.Dimension
import javax.swing.JTable
import javax.swing.JTextField

class ToolBarSchema(table: JTable) : AbstractTopToolBar(table) {

    private val buttons = arrayOf(
        ButtonKarkas("insertDB", "Создать Схему проводки", { createSchema() },	null),
        ButtonKarkas("application", "Правка Схемы", { editSchema() },	null),
        ButtonKarkas("condition", "Задать условие", { createCondition() },	null),
        ButtonKarkas(null, null, null, null)
    )

    override fun getButtonKarkases(): Array<ButtonKarkas> = buttons

    private val findTransact: JTextField = JTextField().apply {
        minimumSize = Dimension(100, 32)
        preferredSize = Dimension(100, 32)

        addKeyListener( FilterKeyLister(HeaderTransactType.filter) {
            HeaderTransactType.filter.filterEntity.transactType = it?.bySqlLikePlus()?:""
        })
    }

    init {
        initButton()

        add(findTransact)

        HeaderTransactType.filter.initStoreChecker(HeaderTransactTypeService)
    }


    private fun createSchema() {
        DialogCreateSchema(SchemaService.createDefaultSchema(), this).showDialogResultOk()
    }

    private fun editSchema() {

        processShowError {
            val schema = SchemaService.selectedEntity() ?: throw Exception("Нет текущей схемы проводки")

            DialogCreateSchema(schema, this).showDialogResultOk()
        }
    }

    private fun createCondition() {
        DialogCreateCondition(this).showDialogResultOk()
    }
}