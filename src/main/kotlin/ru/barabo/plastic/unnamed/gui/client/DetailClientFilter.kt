package ru.barabo.plastic.unnamed.gui.client

import ru.barabo.plastic.main.resources.ResourcesManager
import ru.barabo.plastic.unnamed.data.RowFieldClient
import ru.barabo.plastic.unnamed.general.ClientCriteria
import ru.barabo.plastic.unnamed.general.ClientCriteriaDBStore
import ru.barabo.plastic.unnamed.gui.errorMessage
import ru.barabo.total.db.ListenerStore
import ru.barabo.total.db.StateRefresh
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.border.TitledBorder
import kotlin.concurrent.thread

class DetailClientFilter(private val store : ClientCriteriaDBStore<RowFieldClient>) : JPanel(), ListenerStore<RowFieldClient> {

    private val filterKeyLister = FilterKeyLister()

    private val fioText = JTextField("").apply {
        this.addKeyListener(filterKeyLister)
    }

    private val addressText = JTextField("").apply {
        this.addKeyListener(filterKeyLister)
    }

    private val lineDocument = JTextField("").apply {
        this.addKeyListener(filterKeyLister)
    }

    private val numberDocument = JTextField("").apply {
        this.addKeyListener(filterKeyLister)
    }

    private val birthDay = JTextField("").apply {
        this.addKeyListener(filterKeyLister)
    }

    private val phone = JTextField("").apply {
        this.addKeyListener(filterKeyLister)
    }

    private val id = JTextField("").apply {
        this.addKeyListener(filterKeyLister)
    }

    private val findButton = JButton("Найти", ResourcesManager.getIcon("wait16")).apply {
        addActionListener { clickFilter() }
    }

    private val okButton = JButton("Выбрать\n", ResourcesManager.getIcon("outClient")).apply {
        addActionListener {  }
    }

    private val cancelButton = JButton("Отменить\n", ResourcesManager.getIcon("deleteDB")).apply {
        addActionListener {  }
    }


    @Volatile private var isRuningFilter: Boolean = false

    private var priorValue:  Array<Any?> = emptyArray()

    private inner class FilterKeyLister : KeyListener {
        override fun keyTyped(e: KeyEvent?) {}

        override fun keyPressed(e: KeyEvent?) {}

        override fun keyReleased(e: KeyEvent?) {

            sendFilter()
        }
    }

    init {
        layout = GridBagLayout()

        add(JLabel("ФИО клиента:"), labelConstraint(0))
        add(fioText, textConstraint(0, 4))

        add(JPanel().apply {
            layout = GridBagLayout()

            add( findButton,  textConstraint(0, 1, 1, 0))

            add(JLabel(" "), textConstraint(0, 1, 1, 1))

        }, textConstraint(0, 1, 1, 5))

        add(JLabel("Адрес:"), labelConstraint(1))
        add(addressText, textConstraint(1, 4))
        add(JLabel(" "), textConstraint(1, 1, 1, 5))

        add(JPanel().apply {
            border = TitledBorder("Паспорт РФ")

            layout = GridBagLayout()

            add(JLabel("Серия:"), labelConstraint(0))
            add(lineDocument, textConstraint(0))

            add(JLabel("Номер:"), labelConstraint(0, 2))
            add(numberDocument, textConstraint(0, 1, 1, 3))

        }, textConstraint(2, 4, 3, 0) )

        add(JPanel().apply {
            border = TitledBorder("Спец. информация")
            layout = GridBagLayout()

            add(JLabel("id Клиента:"), labelConstraint(0))
            add(id, textConstraint(0))
        }, textConstraint(2, 1, 3, 4)  )

        add(JPanel().apply {
            layout = GridBagLayout()

            add( okButton,  textConstraint(0, 1, 1, 0))

            add(JLabel(" "), textConstraint(0, 1, 1, 1))

        }, textConstraint(2, 1, 3, 5))

        add(JPanel().apply {
            border = TitledBorder("Доп. информация")

            layout = GridBagLayout()

            add(JLabel("День рождения[ДД.MM]"), labelConstraint(0))
            add(birthDay, textConstraint(0))

            add(JLabel("Телефон:"), labelConstraint(0, 2))
            add(phone, textConstraint(0, 1, 1, 3))
        }, textConstraint(5, 5, 3, 0) )

        add(JPanel().apply {
            layout = GridBagLayout()

            add( cancelButton,  textConstraint(0, 1, 1, 0))

            add(JLabel(" "), textConstraint(0, 1, 1, 1))

        }, textConstraint(5, 1, 3, 5))

       // add(JLabel(" "), textConstraint(5, 1, 3, 5))

        store.addListenerStore(this)
    }

    private fun clickFilter(): Boolean {
        val newData = getNewClientCriteria()

        if(newData.isEmptyData()) {
            return errorMessage(DATA_IS_EMPTY)
        }

        if(isNotChangedData(newData) ) {
            return errorMessage(DATA_IS_EQUALS)
        }

        return sendFilter()
    }

    companion object {
        private const val DATA_IS_EMPTY = "Данные для поиска отсутствуют или их недостаточно"

        private const val DATA_IS_EQUALS = "Поиск уже выполнен"
    }

    private fun sendFilter(): Boolean  {
        if(isRuningFilter) return false


        val newData = getNewClientCriteria()

        if(isNotChangedData(newData) ) return false

        if(!isRuningFilter) {
            isRuningFilter = true

            findButton.isEnabled = false
            findButton.text = "Идет поиск..."

            sendRequestFilter(newData)
        }

        return true
    }

    private fun sendRequestFilter(newData: ClientCriteria) {
        priorValue = newData.toSqlArray()

        thread {
            run {
                store.setClientCriteria(newData)
            }
        }
    }

    private fun getNewClientCriteria(): ClientCriteria = ClientCriteria.fromStrings(id.text,
        fioText.text, addressText.text, birthDay.text, lineDocument.text, numberDocument.text, phone.text)


    private fun isNotChangedData(clientCriteria: ClientCriteria): Boolean {

        if(clientCriteria.isEmptyData()) {
            return true
        }

        return clientCriteria.toSqlArray().contentEquals(priorValue)
    }

    override fun setCursor(row: RowFieldClient?) {}

    override fun refreshData(allData: MutableList<RowFieldClient>?, stateRefresh: StateRefresh?) {
        if(stateRefresh != StateRefresh.ALL) return

        isRuningFilter = false

        findButton.isEnabled = true
        findButton.text = "Найти"
        sendFilter()
    }
}

fun labelConstraint(gridY: Int, gridX: Int = 0) =
    GridBagConstraints(gridX, gridY, 1, 1, 0.0, 0.0,
        GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL,
        Insets(5, 2, 5, 2), 0, 0)


fun textConstraint(gridY: Int, width: Int = 1, height: Int = 1, gridX: Int = 1) =
    GridBagConstraints(gridX, gridY, width, height, 1.0, 1.0,
        GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL,
        Insets(5, 2, 5, 2), 0, 0)

