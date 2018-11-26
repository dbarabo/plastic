package ru.barabo.plastic.unnamed.gui

import ru.barabo.total.gui.any.ButtonKarkas
import javax.swing.*

class LeftTabToolBar(private val book: JTabbedPane) : JToolBar(VERTICAL) {

    private val buttonGroupList = ArrayList<ButtonGroup>()

    private val buttons = arrayOf(
        Body("parovoz", "В Пути", 0),
        Body("home", "В Офисе", 1),
        Body("ok", "Выданные", 2),
        Body("bug", "с Ошибками", 3)
    )

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        isFloatable = true

        buttons.forEach { body ->

            val button = ButtonKarkas.createButton(
                ButtonKarkas(body.icon, body.name, { book.selectedIndex = body.selectedTab }, 0),
                buttonGroupList)

            add(button)
        }

        val max = components.maxBy { it.maximumSize.width } ?: this

        components.forEach {
            it.maximumSize = max.maximumSize
            it.minimumSize = max.maximumSize
            it.preferredSize = max.maximumSize
        }

        (components[0] as? JToggleButton)?.isSelected = true
    }
}

data class Body(val icon: String, val name: String, val selectedTab: Int)