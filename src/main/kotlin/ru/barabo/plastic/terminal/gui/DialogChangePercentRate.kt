package ru.barabo.plastic.terminal.gui

import ru.barabo.plastic.schema.gui.account.groupPanel
import ru.barabo.plastic.schema.gui.account.textAreaHorizontal
import ru.barabo.plastic.schema.gui.schema.AbstractDialog
import ru.barabo.plastic.schema.gui.schema.comboBox
import ru.barabo.plastic.terminal.entity.PercentRateTerminal
import ru.barabo.plastic.terminal.service.PercentRateTerminalService
import ru.barabo.plastic.terminal.service.PosTerminalService
import java.awt.Component
import javax.swing.JComboBox

class DialogChangePercentRate(component: Component) : AbstractDialog(component, "Сменить %% ставку") {

    private val rateList: JComboBox<PercentRateTerminal>

    init {
        groupPanel("", 0, 3, 0).apply {

            textAreaHorizontal("Терминал/Клиент)", 0).apply {
                isEditable = false
                text = with(PosTerminalService) { "(${selectedEntity()?.terminal}) ${selectedEntity()?.clientName}" }
            }

            comboBox("%% Ставка", 2, PercentRateTerminalService.elemRoot()).apply {
                rateList = this

                selectedItem = PercentRateTerminalService.getItemById(PosTerminalService.selectedEntity()?.percentId)
            }
        }

        createOkCancelButton(3)

        pack()
    }

    override fun okProcess() {

        PosTerminalService.changePercentRate(rateList.selectedItem as? PercentRateTerminal)
    }
}