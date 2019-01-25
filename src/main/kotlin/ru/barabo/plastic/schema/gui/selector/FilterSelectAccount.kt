package ru.barabo.plastic.schema.gui.selector

import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.schema.entity.selector.SelectAccount
import ru.barabo.plastic.schema.entity.selector.SqlFilterEntity
import ru.barabo.plastic.schema.gui.account.*
import ru.barabo.plastic.unnamed.general.bySqlLike2Plus
import ru.barabo.plastic.unnamed.general.bySqlLikePlus
import ru.barabo.plastic.unnamed.general.parseLong
import java.awt.Container
import java.awt.GridBagLayout
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.*
import kotlin.reflect.KProperty0

class FilterSelectAccount(process: KProperty0<TabsBookProcessOk<SelectAccount>>,
                          store: StoreFilterService<SelectAccount>) : JPanel() {

    //private val logger = Logger.getLogger(FilterSelectAccount::class.java.name)
    init {
        layout = GridBagLayout()

        selectCancelButton(process, store)

        groupPanel("Счет", 0, 3, 1).apply {
            textFieldHorizontal("id Счета", 0).apply {
                setFilterByKeyListener { SelectAccount.filter.filterEntity.id = it?.parseLong() }
                text = SelectAccount.filter.filterEntity.id?.toString()
             }

            textFieldHorizontal("Код счета", 1).apply {
                setFilterByKeyListener { SelectAccount.filter.filterEntity.code = it?.bySqlLikePlus() }
                text = SelectAccount.filter.filterEntity.code
            }

            textFieldHorizontal("Наименование", 2).apply {
                setFilterByKeyListener { SelectAccount.filter.filterEntity.name = it?.bySqlLike2Plus() }
                text = SelectAccount.filter.filterEntity.name
            }
        }

        groupPanel("Клиент", 0, 3, 2).apply {

            textFieldHorizontal("Наименование", 0).apply {
                setFilterByKeyListener { SelectAccount.filter.filterEntity.clientName = it?.bySqlLike2Plus() }
                text = SelectAccount.filter.filterEntity.clientName
              }

            textFieldHorizontal("ИНН", 1).apply {
                setFilterByKeyListener { SelectAccount.filter.filterEntity.clientInn = it?.bySqlLikePlus() }
                text = SelectAccount.filter.filterEntity.clientInn
            }

            textFieldHorizontal("Описание Клиента", 2).apply {
                setFilterByKeyListener { SelectAccount.filter.filterEntity.clientDescription = it?.bySqlLike2Plus() }
                text = SelectAccount.filter.filterEntity.clientDescription
            }
        }

        SelectAccount.filter.initStoreChecker(store, ::isCheckedFilter)
    }

    private fun isCheckedFilter(selectAccount: SelectAccount): Boolean =
        with(selectAccount) {
            id != null ||
            code?.trim()?.length?:0 >= 5 ||
            name?.trim()?.length?:0 >= 7 ||
            clientName?.length?:0 >= 7 ||
            clientDescription?.length?:0 >= 10 ||
            clientInn?.length?:0 >= 6
        }
}

fun <T: Any> Container.selectCancelButton(process: KProperty0<TabsBookProcessOk<T>>, store: StoreFilterService<T>): JPanel =
    liteGroup("", 0, 0).apply {

        onlyButton("Выбрать", 0, 0, "outClient24"){

            process.get().select(store.selectedEntity())
        }

        onlyButton("Отменить", 1, 0, "deleteDB24"){
            process.get().cancel()
        }
    }

private fun JComponent.setFilterByKeyListener(setter: (String?)->Unit) {
    addKeyListener( FilterKeyLister(SelectAccount.filter, setter) )
}

class FilterKeyLister(private val filter: SqlFilterEntity<*>? = null, private val setter: (String?)->Unit) : KeyListener {
    override fun keyTyped(e: KeyEvent?) {}

    override fun keyPressed(e: KeyEvent?) {}

    override fun keyReleased(e: KeyEvent?) {

        val textField = (e?.source as? JTextField) ?: return

        setter(textField.text)

        filter?.applyFilter()
    }
}

fun Container.textFieldHorizontal(label: String, gridY: Int): JTextField {

    add( JLabel(label), labelConstraint(gridY) )

    return JTextField().apply {

        this@textFieldHorizontal.add(this, textConstraint(gridY = gridY, gridX = 1) )
    }
}