package ru.barabo.plastic.schema.gui.selector

import org.apache.log4j.Logger
import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.schema.entity.selector.SelectAccount
import ru.barabo.plastic.schema.gui.account.*
import ru.barabo.plastic.unnamed.general.bySqlLike2Plus
import ru.barabo.plastic.unnamed.general.bySqlLikePlus
import java.awt.Container
import java.awt.GridBagLayout
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import kotlin.reflect.KProperty0

class FilterSelectAccount(private val process: KProperty0<TabsBookProcessOk<SelectAccount>>,
                                  private val store: StoreFilterService<SelectAccount>) : JPanel() {

    private val logger = Logger.getLogger(FilterSelectAccount::class.java.name)

    private val codeAccount: JTextField

    private val labelAccount: JTextField

    private val descAccount: JTextField

    private val idClient: JTextField

    private val clientLabel: JTextField

    private val clientInn: JTextField

    private val select: JButton

    private val cancel: JButton

    init {
        layout = GridBagLayout()

        liteGroup("", 0, 1, 0, 1).apply {

            onlyButton("Выбрать", 0, 0, "outClient"){

                logger.error("entity=${store.selectedEntity()}")

                process.get().select(store.selectedEntity())
            }.apply { select = this }

            onlyButton("Отменить", 1, 0, "deleteDB"){
                process.get().cancel()
            }.apply { cancel = this }
        }

        groupPanel("Счет", 0, 3, 1, 5).apply {

            textFieldHorizontal("Код счета", 0).apply {
                codeAccount = this

                addKeyListener(FilterKeyLister {
                    SelectAccount.filter.filterEntity.code = it?.bySqlLikePlus()
                })
            }

            textFieldHorizontal("Наименование", 1).apply {
                labelAccount = this

                addKeyListener(FilterKeyLister {
                    SelectAccount.filter.filterEntity.name = it?.bySqlLike2Plus()
                })
            }

            textFieldHorizontal("Описание", 2).apply {
                descAccount = this
                addKeyListener(FilterKeyLister {
                    SelectAccount.filter.filterEntity.description = it?.bySqlLike2Plus()
                })
            }
        }

        groupPanel("Клиент", 0, 3, 2, 5).apply {

            textFieldHorizontal("id Клиента", 0).apply { idClient = this }

            textFieldHorizontal("Наименование", 1).apply {
                clientLabel = this

                addKeyListener(FilterKeyLister {
                    SelectAccount.filter.filterEntity.clientName = it?.bySqlLike2Plus()
                })
            }

            textFieldHorizontal("ИНН", 2).apply {
                clientInn = this
            }
        }

        SelectAccount.filter.initStoreChecker(store, ::isCheckedFilter)
    }

    private inner class FilterKeyLister(private val setter: (String?)->Unit) : KeyListener {
        override fun keyTyped(e: KeyEvent?) {}

        override fun keyPressed(e: KeyEvent?) {}

        override fun keyReleased(e: KeyEvent?) {

            val textField = (e?.source as? JTextField) ?: return

            setter(textField.text)

            SelectAccount.filter.applyFilter()
        }
    }

    private fun isCheckedFilter(selectAccount: SelectAccount): Boolean =
        with(selectAccount) {
            code?.trim()?.length?:0 >= 6 ||
            name?.trim()?.length?:0 >= 12 ||
            description?.trim()?.length?:0 >= 12 ||
            clientId != null ||
            clientName?.length?:0 >= 8 ||
            clientDescription?.length?:0 >= 10
        }
}

fun Container.textFieldHorizontal(label: String, gridY: Int): JTextField {

    add( JLabel(label), labelConstraint(gridY) )

    return JTextField().apply {

        this@textFieldHorizontal.add(this, textConstraint(gridY = gridY, gridX = 1) )
    }
}