package ru.barabo.plastic.terminal.gui

import ru.barabo.plastic.main.resources.ResourcesManager
import ru.barabo.plastic.schema.gui.selector.FilterKeyLister
import ru.barabo.plastic.terminal.service.PosTerminalService
import ru.barabo.total.gui.any.AbstractTopToolBar
import ru.barabo.total.gui.any.ButtonKarkas
import java.awt.Dimension
import javax.swing.JLabel
import javax.swing.JTextField

class ToolBarPosTerminal : AbstractTopToolBar() {

    private val buttons = arrayOf(
        ButtonKarkas("insertDB", "Новый терминал") {  DialogCreateTerminal(this).showDialogResultOk() },
        ButtonKarkas("percent", "Сменить %% ставку") { DialogChangePercentRate(this).showDialogResultOk() },
        ButtonKarkas("outClient", "Открыть договор") { DialogOpenPact(this).showDialogResultOk() },
        ButtonKarkas("deleteDB", "Закрыть договор") { DialogClosePact(this).showDialogResultOk() },
        ButtonKarkas(null, null, null, null) )

    override fun getButtonKarkases(): Array<ButtonKarkas> = buttons

    private val findAny: JTextField = JTextField().apply {
        minimumSize = Dimension(140, 32)
        preferredSize = Dimension(140, 32)

        addKeyListener( FilterKeyLister { PosTerminalService.setFilterByAny(it?:"") } )
    }

    init {
        initButton()

        add(findAny)
        add(JLabel(ResourcesManager.getIcon("find")) )
    }
}
