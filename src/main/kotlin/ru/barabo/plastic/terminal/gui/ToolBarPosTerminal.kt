package ru.barabo.plastic.terminal.gui

import ru.barabo.plastic.card.gui.DialogPacketCardInfo
import ru.barabo.plastic.main.resources.ResourcesManager
import ru.barabo.plastic.schema.gui.selector.FilterKeyLister
import ru.barabo.plastic.terminal.gui.scheduler.DialogScheduler
import ru.barabo.plastic.terminal.service.PosTerminalService
import ru.barabo.plastic.terminal.service.StoreSchedulerService
import ru.barabo.plastic.unnamed.gui.errorMessage
import ru.barabo.total.gui.any.AbstractTopToolBar
import ru.barabo.total.gui.any.ButtonKarkas
import java.awt.Dimension
import javax.swing.JLabel
import javax.swing.JTextField

class ToolBarPosTerminal : AbstractTopToolBar() {

    private val buttons = arrayOf(
        ButtonKarkas("refresh", "Обновить") { refreshData() },

        ButtonKarkas("insertDB", "Новый терминал") {
            DialogCreateTerminal(this).showDialogResultOk()
        },
        ButtonKarkas("percent", "Сменить %% ставку") {
            DialogChangePercentRate(this).showDialogResultOk()
        },
        ButtonKarkas("outClient", "Открыть договор") {
            DialogOpenPact(this).showDialog(PosTerminalService.selectedEntity()?.pactStart)
        },
        ButtonKarkas("deleteDB", "Закрыть договор") {
            DialogClosePact(this).showDialog(PosTerminalService.selectedEntity()?.pactEnd)
        },
        ButtonKarkas("tudasuda", "Источник комиссии") {  },

        ButtonKarkas("scheduler", "Расписание") { schedulerView() },
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

        buttons[5].button?.isEnabled = false
    }

    private fun refreshData() {
        PosTerminalService.initData()

        if(findAny.text?.isNotEmpty() == true) {
            PosTerminalService.setFilterByAny(findAny.text)
        }
    }

    private fun schedulerView() {
        if(PosTerminalService.selectedEntity()?.bankName?.toUpperCase() != OUR_BANK) {
            errorMessage(ERROR_ONLY_OUR_BANK)
            return
        }

        StoreSchedulerService.initData()
        DialogScheduler(this).showDialogResultOk()
    }
}

private const val OUR_BANK = "НАШ БАНК"

private const val ERROR_ONLY_OUR_BANK = "Данный сервис доступен только для клиентов с р/с открытым в нашем банке :("
