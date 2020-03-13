package ru.barabo.report.gui

import ru.barabo.plastic.schema.gui.account.onlyButton
import ru.barabo.plastic.schema.gui.account.textConstraint
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Container
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.border.BevelBorder

class MessageInformer : JPanel() {

    val info = JTextArea().apply {
        isEditable = false
        background = Color(245, 245, 245)
    }

    val editor: JTextArea = JTextArea().apply {
        rows = 3
        this.border = BorderFactory.createBevelBorder(BevelBorder.LOWERED) //  BorderFactory.createLineBorder(Color.black)
    }

    private val isPrivate: JCheckBox

    private val toolbar = JPanel().apply {
        layout = BorderLayout()

        add(editor, BorderLayout.CENTER)

        add(JPanel().apply {

            layout = GridBagLayout()

            onlyButton("Отправить", 0, 0, "send24") {

            }
            checkBox("Только для меня", 1, 0).apply {
                isPrivate = this
            }
        }, BorderLayout.EAST)
    }

    init {
        layout = BorderLayout()

        add(info, BorderLayout.CENTER)

        add(toolbar, BorderLayout.SOUTH)
    }
}

fun Container.checkBox(title: String, gridY: Int, gridX: Int = 0, clickListener: ()->Unit = {}): JCheckBox {
    return JCheckBox(title).apply {

        addActionListener { clickListener() }

        this@checkBox.add(this, textConstraint(gridY, 1, gridX) )
    }
}