package ru.barabo.plastic.schema.gui.schema

import ru.barabo.plastic.schema.gui.account.groupPanel
import ru.barabo.plastic.schema.gui.account.onlyButton
import ru.barabo.plastic.schema.gui.account.processShowError
import ru.barabo.plastic.unnamed.gui.dialog.parentWindow
import java.awt.Component
import java.awt.Container
import java.awt.GridBagLayout
import javax.swing.JDialog
import javax.swing.JPanel

abstract class AbstractDialog(component: Component, title: String) :
    JDialog(parentWindow(component), title, true) {

    @Volatile private var resultOk = true

    abstract fun okProcess()

    init {

        layout = GridBagLayout()
    }

    fun packWithLocation() {
        pack()

        setLocationRelativeTo(owner)
    }

    fun Container.createOkCancelButton(gridY: Int = 1, gridX: Int = 0): JPanel =
        groupPanel("", gridY, 2, gridX).apply {
            onlyButton("Сохранить", 0, 0, "saveDB"){ ok() }

            onlyButton("Отменить", 0, 1, "deleteDB"){ cancel() }
        }

    private fun ok() {
        resultOk = true

        processShowError { okProcess() }

        dispose()
    }

    protected fun cancel() {

        resultOk = false

        dispose()
    }

    fun showDialogResultOk(): Boolean {
        resultOk = false

        isVisible = true

        return resultOk
    }
}