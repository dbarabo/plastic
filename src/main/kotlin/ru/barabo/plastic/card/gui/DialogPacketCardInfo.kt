package ru.barabo.plastic.card.gui

import ru.barabo.gui.swing.table.ColumnTableModel
import ru.barabo.gui.swing.table.EntityTable
import ru.barabo.plastic.card.entity.PacketCardInfo
import ru.barabo.plastic.card.service.PacketCardInfoService
import ru.barabo.plastic.schema.gui.schema.AbstractDialog
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JScrollPane

open class DialogPacketCardInfo(component: Component) : AbstractDialog(component, "Перемещаемые карты") {

    init {

        layout = BorderLayout()

        add(JLabel(TEXT_INFO), BorderLayout.NORTH)

        add(JScrollPane(TablePacketCardInfo), BorderLayout.CENTER)

        add(JButton("Ok").apply {addActionListener { dispose() } },  BorderLayout.SOUTH)

        packWithLocation()
    }

    override fun okProcess() {}
}

private const val TEXT_INFO = "Внимание, перемещаются все карты, указанные в таблице, т.к. они в одном пакете!!!"

object TablePacketCardInfo : EntityTable<PacketCardInfo>(packetCardInfoColumns, PacketCardInfoService)

private val packetCardInfoColumns = listOf(
    ColumnTableModel("Держатель", 50, PacketCardInfo::personName, false),
    ColumnTableModel("№ Карты", 20, PacketCardInfo::cardNumber, false),
    ColumnTableModel("Эмбоссируемое имя", 20, PacketCardInfo::embosName, false)
)