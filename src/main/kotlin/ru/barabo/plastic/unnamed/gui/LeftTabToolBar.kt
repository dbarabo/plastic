package ru.barabo.plastic.unnamed.gui

import ru.barabo.plastic.release.main.data.DBStorePlastic
import ru.barabo.total.db.DBStore
import ru.barabo.total.db.FilteredStore
import ru.barabo.total.db.ListenerStore
import ru.barabo.total.db.StateRefresh
import ru.barabo.total.gui.any.ButtonKarkas
import javax.swing.*

class LeftTabToolBar(private val book: JTabbedPane, dbStorePlastic: DBStorePlastic) : JToolBar(VERTICAL) {

    private val buttonGroupList = ArrayList<ButtonGroup>()

    private val buttons = arrayOf(
        Body("parovoz", "В Пути", dbStorePlastic.unnamedInPath) {
            dbStorePlastic.unnamedInPath.updateWithErrorMessage()
            book.selectedIndex = 0
        },
        Body("home", "В Офисе", dbStorePlastic.unnamedInHome) {
            dbStorePlastic.unnamedInHome.updateWithErrorMessage()
            book.selectedIndex = 1
        },
        Body("ok", "Выданные", dbStorePlastic.unnamedOutClient) {
            dbStorePlastic.unnamedOutClient.updateWithErrorMessage()
            book.selectedIndex = 2
        },
        Body("bug", "с Ошибками", dbStorePlastic.unnamedError) {
            dbStorePlastic.unnamedError.updateWithErrorMessage()
            book.selectedIndex = 3
        }
    )

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        isFloatable = true

        buttons.forEach { body ->

            val button = ButtonKarkas.createButton(
                ButtonKarkas(body.icon, body.name, { body.clickTab() }, 0),
                buttonGroupList).apply {

                DelegateButtonUpdateStore(this, body, body.dbStore)
            }

            add(button)
        }

        buttons.forEach { it.dbStore.updateAllData() }

        val max = components.maxByOrNull { it.maximumSize.width } ?: this

        components.forEach {
            it.maximumSize = max.maximumSize
            it.minimumSize = max.maximumSize
            it.preferredSize = max.maximumSize
        }

        (components[0] as? JToggleButton)?.isSelected = true
    }
}

class DelegateButtonUpdateStore<E>(private val button: AbstractButton,
                                   private val body: Body,
                                   private val dbStore: FilteredStore<E>
) : ListenerStore<E> {

    init {
        dbStore.addListenerStore(this)
    }

    override fun refreshData(allData: MutableList<E>?, stateRefresh: StateRefresh?) {
        button.text = "${body.name} (${dbStore.countUnfilteredData})"
    }

    override fun setCursor(row: E) {}
}

data class Body(val icon: String, val name: String,  val dbStore: FilteredStore<*>, val clickTab: ()->Unit)

fun DBStore<*>.updateWithErrorMessage() {
    try {
        updateAllData()
    } catch (e: Exception) {
        errorMessage(e.message)
    }
}