package ru.barabo.plastic.unnamed.gui.dialog

import java.awt.*
import javax.swing.*
import kotlin.concurrent.thread

private const val TITLE = "Идет обработка заявлений"

@Suppress("CAST_NEVER_SUCCEEDS")
class PleaseWait() : JDialog(null as? JFrame, TITLE, Dialog.ModalityType.APPLICATION_MODAL) {
    init {

        layout = BorderLayout()

        setLocationRelativeTo(null)

        minimumSize = Dimension(
            Toolkit.getDefaultToolkit().screenSize.width / 8,
            Toolkit.getDefaultToolkit().screenSize.height / 18
        )

        add(JLabel("Пожалуйста, подождите идет создание и обработка заявлений......."), BorderLayout.PAGE_START)

        val progress = JProgressBar().apply {
            isIndeterminate = true

            minimumSize = Dimension(150, 50)
        }

        add(progress, BorderLayout.CENTER)

        pack()
    }

    fun showPleaseWait(): PleaseWait {

        invalidate()
        repaint()

        isVisible = true

        return this
    }
}

fun runPleaseWait(): PleaseWait{

    val pleaseWait = PleaseWait()

    thread(){ pleaseWait.showPleaseWait() }

    return pleaseWait
}
