package ru.barabo.plastic.unnamed.gui.service

import java.awt.BorderLayout
import java.awt.Component
import javax.swing.*
import javax.swing.border.TitledBorder


class ServicePanel : JPanel() {

    init {
        layout = BorderLayout()

        val panelButton = JPanel().apply {
            border = TitledBorder("Стили")

            layout = BoxLayout(this, BoxLayout.Y_AXIS)//BorderLayout()
        }

        val group = ButtonGroup()

        panelButton.add(
            JRadioButton("Metall").apply {
                addActionListener { setThem("javax.swing.plaf.metal.MetalLookAndFeel") }

                group.add(this)
            })

        panelButton.add(
            JRadioButton("Nimbus").apply {
                addActionListener { setThem("javax.swing.plaf.nimbus.NimbusLookAndFeel") }

                group.add(this)
            })

        panelButton.add(
            JRadioButton("Windows").apply {
                addActionListener { setThem("com.sun.java.swing.plaf.windows.WindowsLookAndFeel") }

                group.add(this)
            })

        panelButton.add(
            JRadioButton("Windows Classic").apply {
                addActionListener { setThem("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel") }

                group.add(this)
            })

        panelButton.add(
            JRadioButton("Motif").apply {
                addActionListener { setThem("com.sun.java.swing.plaf.motif.MotifLookAndFeel") }

                group.add(this)
            })

        add(panelButton, BorderLayout.CENTER)
    }
}

fun Component.setThem(them: String) {

    UIManager.setLookAndFeel(them)

    SwingUtilities.updateComponentTreeUI( SwingUtilities.getWindowAncestor(this) )
    JFrame.setDefaultLookAndFeelDecorated(true)
}