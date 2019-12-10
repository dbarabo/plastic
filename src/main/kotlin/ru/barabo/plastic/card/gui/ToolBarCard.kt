package ru.barabo.plastic.card.gui

import ru.barabo.plastic.card.service.FilterMode
import ru.barabo.plastic.card.service.StoreCardService
import ru.barabo.plastic.gui.PlasticGui
import ru.barabo.plastic.main.resources.ResourcesManager
import ru.barabo.plastic.packet.gui.getDialogProductTo
import ru.barabo.plastic.release.application.data.StaticData
import ru.barabo.plastic.release.sms.select.gui.TopToolBarSmsSelect
import ru.barabo.plastic.release.sms.select.gui.TopToolBarSmsSelect.ERROR_NO_PHONE
import ru.barabo.plastic.release.sms.select.gui.TopToolBarSmsSelect.isDigits10
import ru.barabo.plastic.schema.gui.account.processShowError
import ru.barabo.plastic.schema.gui.selector.FilterKeyLister
import ru.barabo.total.gui.any.ButtonKarkas
import ru.barabo.total.gui.any.ShowMenuListener
import java.awt.Container
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*

abstract class ToolBarCard(protected val store: StoreCardService, private val table: JTable) : JToolBar() {

    protected val filterMenu = arrayOf(
        ButtonKarkas(
            "nofilter", "Фильтр: Все",
            { store.filterMode = FilterMode.None }, 0),
        ButtonKarkas(
            "filter3", "С остатками/оборотами",
            { store.filterMode = FilterMode.WithTurnOrRest }, 0)
    )

    protected fun defaultEnd() {
        findAnyText(store)

        StateButton(store, this, table)
    }

    protected fun refreshKarkas() = ButtonKarkas("refresh", "Обновить") { refresh() }

    protected fun toApplicationKarkas() = ButtonKarkas("toApplication", "Заявление") { gotoApplication() }

    private fun refresh() {
        store.filterReset()
    }

    private fun gotoApplication() {
        PlasticGui.openApplicationForm(store.selectedEntity()?.applicationId)
    }
}

class ToolBarCardRegister(store: StoreCardService, table: JTable, private val leftButtonBar: LeftTabBar) : ToolBarCard(store, table) {
    init {
        toolButton( refreshKarkas() )

        toolButton("deleteAll", "Удалить") { delete() }

        toolButton( toApplicationKarkas() )

        toolButton("changeProduct", "Сменить продукт") { changeProduct() }

        popupButton("в Перевыпуск ➧", "select") {
            menuItem("в Действующие карты", "lost") { selectReissueActiveOut() }

            menuItem("в карты Закрытые", "time") { selectReissueClosed() }
        }
        //ButtonKarkas("death", "BTRT25 Send", { this.sendBtr25() }, null),

        defaultEnd()
    }

    private fun selectReissueClosed() {
        leftButtonBar.selectCloseButton()
    }

    private fun selectReissueActiveOut() {
        leftButtonBar.selectOutClientButton()
    }

    private fun delete() {
        val confirm = String.format(CONFIRM_DELETE, store.selectedEntity()?.personName)
        if(!isConfirmMessageYesNo(confirm, TITLE_DELETE)) return

        processShowError {
            store.deleteContent()
        }
    }

    private fun changeProduct() {
        val existProduct = store.selectedEntity()?.productName

        val products =StaticData.getInstance().cardProductLabel.filter { it != existProduct }

        val newProductName = getDialogProductTo(products) ?: return

        processShowError {
            store.changeProduct(newProductName)
        }
    }
}

private const val CONFIRM_REISSUE = "Перевыпустить карту %s ?"

private const val TITLE_REISSUE = "Оформление перевыпуска карты"

private const val CONFIRM_DELETE = "Удалить оформление '%s' ?"

private const val TITLE_DELETE = "Удаление заявления"

private const val MESSAGE_PHONE_ADD_SUCCESS = "Запрос на подключение sms-информирования успешно отправлен"

private const val MESSAGE_PHONE_REMOVE_SUCCESS = "Запрос на отключение sms-информирования успешно отправлен"

class ToolBarCardInPath(store: StoreCardService, table: JTable) : ToolBarCard(store, table)  {
    init {

        toolButton( refreshKarkas() )

        toolButton( toApplicationKarkas() )

        defaultEnd()
    }
}

class ToolBarCardOutClient(store: StoreCardService, table: JTable, leftButtonBar: LeftTabBar)
    : ToolBarCard(store, table), SelectToReissueCard {

    private val selector = SelectToReissueCardDelegate(store, this, leftButtonBar)

    init {
        toolButton( refreshKarkas() )

        toolButton( toApplicationKarkas() )

        toolButton("password", "сменить ПИН-код!") { changePin() }

        popupButton("SMS-сервис ➧", "sms") {
            menuItem("Подключить SMS", "smsAdd") { smsAdd() }

            menuItem("Отключить SMS", "smsRemove") { smsRemove() }
        }

        toolButton(ShowMenuListener(filterMenu).createButtonKarkas(0))

        findAnyText(store)
    }

    override fun addSelectedButtons() {
        selector.addSelectedButtons()
    }

    private fun changePin() {
        processShowError { store.changePin() }
    }

    private fun smsAdd() {
        smsInfoAddRemove(isAddSmsInfo = true)
    }

    private fun smsRemove() {
        smsInfoAddRemove(isAddSmsInfo = false)
    }

    private fun smsInfoAddRemove(isAddSmsInfo: Boolean) {
        val newPhone = inputDialog(TopToolBarSmsSelect.SHOW_PHONE, store.selectedEntity()?.phonePerson) ?: return

        processShowError {
            if(!isDigits10(newPhone)) {
                throw Exception(ERROR_NO_PHONE)
            }

            store.smsInfoAddRemove(newPhone, isAddSmsInfo)

            showMessage(if(isAddSmsInfo) MESSAGE_PHONE_ADD_SUCCESS else MESSAGE_PHONE_REMOVE_SUCCESS)
        }
    }
}

class ToolBarCardClosed(store: StoreCardService, table: JTable, leftButtonBar: LeftTabBar)
    : ToolBarCard(store, table), SelectToReissueCard {

    private val selector = SelectToReissueCardDelegate(store, this, leftButtonBar)

    init {
        toolButton( refreshKarkas() )

        toolButton( toApplicationKarkas() )

        findAnyText(store)
    }

    override fun addSelectedButtons() {
        selector.addSelectedButtons()
    }
}

interface SelectToReissueCard {
    fun addSelectedButtons()
}

class SelectToReissueCardDelegate(private val store: StoreCardService, private val container: Container,
                                  private val leftButtonBar: LeftTabBar) : SelectToReissueCard {
    private val toRegisterButton = button("sendPaket", "Перевыпустить") { toReissue() }
    private val returnBackButton =  button("return", "Вернуться") { returnBack() }

    override fun addSelectedButtons() {
        container.add(toRegisterButton)
        container.add(returnBackButton)
    }

    private fun toReissue() {
        val confirm = String.format(CONFIRM_REISSUE, store.selectedEntity()?.personName)
        if(!isConfirmMessageYesNo(confirm, TITLE_REISSUE)) return

        processShowError {
            store.addToRegisterCard()

            returnBack()
        }
    }

    private fun returnBack() {
        container.remove(returnBackButton)
        container.remove(toRegisterButton)

        leftButtonBar.selectRegister()
    }
}

fun Container.toolButton(icon: String, name: String?, groupIndex: Int? = null,
                         buttonGroupList: MutableList<ButtonGroup>? = null, action: ()->Unit): AbstractButton? {

    val button: AbstractButton = groupIndex?.let {
        JToggleButton( ResourcesManager.getIcon(icon) ).apply { buttonGroupList?.addGroup(this, it) }
    } ?: JButton(ResourcesManager.getIcon(icon) )

    button.text = name
    button.toolTipText = name
    button.addActionListener { action() }

    return button.apply { this@toolButton.add(this) }

}

fun button(icon: String, name: String?, action: ()->Unit): AbstractButton =
     JButton( ResourcesManager.getIcon(icon) ).apply {
        text = name
        toolTipText = name
        addActionListener { action() }
    }

fun Container.toolButton(icon: String, name: String?, action: ActionListener,
                         groupIndex: Int? = null, buttonGroupList: MutableList<ButtonGroup>? = null): AbstractButton? {

    name ?: return null
    val ico = ResourcesManager.getIcon(icon)

    val button: AbstractButton = groupIndex?.let {
        JToggleButton(ico).apply { buttonGroupList?.addGroup(this, it) }
    } ?: JButton(ico)

    button.text = name
    button.toolTipText = name
    button.addActionListener(action)

    return button.apply { this@toolButton.add(this) }
}

fun Container.toolButton(buttonKarkas: ButtonKarkas): AbstractButton? =
    this.toolButton(buttonKarkas.ico, buttonKarkas.name, buttonKarkas.listener, buttonKarkas.groupIndex)

private fun MutableList<ButtonGroup>.addGroup(button: AbstractButton?, index: Int) {
    while (size <= index) {
        add(ButtonGroup())
    }
    this[index].add(button)
}

fun JPopupMenu.menuItem(name: String? = null, icon: String = "",  action: ()->Unit = {} ): JMenuItem? {
    if(name == null) {
        addSeparator()
        return null
    }

    return JMenuItem(name, ResourcesManager.getIcon(icon)).apply {
        this.addActionListener{ action() }
        this@menuItem.add(this)
    }
}

fun Container.popupButton(name: String? = null, icon: String = "", op: JPopupMenu.()->Unit = {}): JButton /*JPopupMenu*/ {

    return popup(name, icon, op).apply { this@popupButton.add(this) }
}

fun popup(name: String? = null, icon: String = "", op: JPopupMenu.()->Unit = {}): JButton {
    val button = JButton( ResourcesManager.getIcon(icon) )
    button.text = name
    button.toolTipText = name

    val popupMenu = JPopupMenu()

    button.addActionListener { popupMenu.show(button, 1, button.height + 1) }

    op(popupMenu)

    return button
}

fun Container.findAnyText(store: StoreCardService): JTextField {
    this@findAnyText.add(JLabel(ResourcesManager.getIcon("find")) )
    return JTextField().apply {
        addKeyListener( FilterKeyLister(store.filter) { store.setFilterLike(it)} )

        this@findAnyText.add(this)

        minimumSize = Dimension(140, 32)
        preferredSize = Dimension(140, 32)
        maximumSize = Dimension(140, 32)
    }
}