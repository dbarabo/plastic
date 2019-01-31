package ru.barabo.plastic.fio.gui

import ru.barabo.db.EditType
import ru.barabo.db.service.StoreListener
import ru.barabo.plastic.fio.entity.FioChangeInfo
import ru.barabo.plastic.fio.service.FioChangeService
import ru.barabo.plastic.schema.gui.account.groupPanel
import ru.barabo.plastic.schema.gui.selector.textFieldHorizontal
import java.awt.Container
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class DetailFioChange : JPanel(), StoreListener<List<FioChangeInfo>> {

    private var client: FioChangeInfo? = null

    private val surName: JTextField

    private val firstName: JTextField

    private val middleName: JTextField

    private val address: JTextField

    private val phone: JTextField

    private val documentType: JTextField

    private val documentLine: JTextField

    private val documentNumber: JTextField

    private val codeWord: JTextField

    private val idInProcCenter: JTextField

    init {
        layout = GridBagLayout()

        groupPanel("ФИО", 0, 3).apply {
            textFieldHorizontReadOnly("Фамилия", 0).apply { surName = this }

            textFieldHorizontReadOnly("Имя", 1).apply { firstName = this }

            textFieldHorizontReadOnly("Отчество", 2).apply { middleName = this }
        }

        groupPanel("Адрес", 4, 2).apply {

            textFieldHorizontReadOnly("Адрес", 0).apply { address = this }

            textFieldHorizontal("Телефон", 1).apply { phone = this }
        }

        groupPanel("Документ", 6, 3 ).apply {
            textFieldHorizontReadOnly("Тип", 0).apply { documentType = this }

            textFieldHorizontReadOnly("Серия", 1).apply { documentLine = this }

            textFieldHorizontReadOnly("Номер", 2).apply { documentNumber = this }
        }

        groupPanel("Дополнительно", 9, 2).apply {
            textFieldHorizontal("Кодовое слово", 0).apply { codeWord = this }

            textFieldHorizontReadOnly("ID в ПЦ", 1).apply { idInProcCenter = this }
        }

        maxSpaceYConstraint(11)

        FioChangeService.addListener(this)

        updateInfo()
    }

    override fun refreshAll(elemRoot: List<FioChangeInfo>, refreshType: EditType) {
        if(refreshType in listOf(EditType.INIT, EditType.CHANGE_CURSOR, EditType.ALL)) {
            client = FioChangeService.selectedEntity()
            updateInfo()
        }
    }

    private fun updateInfo() {
        surName.text = client?.surName

        firstName.text = client?.name

        middleName.text = client?.middleName

        address.text = client?.address

        phone.text = client?.phone

        documentType.text = client?.documentType

        documentLine.text = client?.documentLine

        documentNumber.text = client?.documentNumber

        codeWord.text = client?.codeWord

        idInProcCenter.text = client?.idInProcCenter
    }
}

private fun Container.textFieldHorizontReadOnly(label: String, gridY: Int) =
    textFieldHorizontal(label, gridY).apply { isEditable = false }

internal fun Container.maxSpaceYConstraint(gridY: Int) =
    JLabel("").apply {
        this@maxSpaceYConstraint.add(this,
            GridBagConstraints(0, gridY, 1, 1, 1.0, 10.0,
                GridBagConstraints.PAGE_END, GridBagConstraints.HORIZONTAL,
                Insets(0, 0, 0, 0), 0, 0)
            )
    }
