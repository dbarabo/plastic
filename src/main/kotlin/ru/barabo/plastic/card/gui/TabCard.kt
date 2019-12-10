package ru.barabo.plastic.card.gui

import ru.barabo.gui.swing.table.ColumnTableModel
import ru.barabo.gui.swing.table.EntityTable
import ru.barabo.plastic.card.entity.Card
import ru.barabo.plastic.card.service.*
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.JToolBar

class TabCard(table: JTable, toolBar: JToolBar): JPanel() {
    init {
        layout = BorderLayout()

        add(JScrollPane(table), BorderLayout.CENTER)

        add(toolBar, BorderLayout.NORTH)
    }
}

object TableRegisterCard : EntityTable<Card>(cardRegisterColumns, RegisterCardService)

private val cardRegisterColumns = listOf(
    ColumnTableModel("Держатель", 50, Card::personName, false),
    ColumnTableModel("Состояние", 20, Card::stateValue, false),
    ColumnTableModel("Изменен", 20, Card::updatedFormat, false),
    ColumnTableModel("Номер карты", 40, Card::cardNumberByFormat, false),
    ColumnTableModel("Продукт", 50, Card::productName, false),
    ColumnTableModel("Офис", 30, Card::departmentName, false),
    ColumnTableModel("Старая карта", 30, Card::oldCardNumberByFormat, false),
    ColumnTableModel("Старая до", 20, Card::oldCardEndFormat, false)
)

object TableInPathCard : EntityTable<Card>(cardInPathColumns, InPathStoreCardService)

private val cardInPathColumns = listOf(
    // ColumnTableModel("ID", 50, Card::id, false),
    ColumnTableModel("Держатель", 50, Card::personName, false),
    ColumnTableModel("Состояние", 20, Card::stateValue, false),
    ColumnTableModel("Изменен", 20, Card::updatedFormat, false),
    ColumnTableModel("Номер карты", 40, Card::cardNumberByFormat, false),
    ColumnTableModel("Карта до", 20, Card::newCardEndFormat, false),
    ColumnTableModel("Продукт", 50, Card::productName, false),
    ColumnTableModel("Офис", 30, Card::departmentName, false),
    ColumnTableModel("Старая до", 20, Card::oldCardEndFormat, false),
    ColumnTableModel("Сообщение ПЦ", 50, Card::messageResponse, false)
)

object TableInHomeCard : EntityTable<Card>(cardInHomeColumns, InHomeStoreCardService)

private val cardInHomeColumns = listOf(
    ColumnTableModel("Держатель", 50, Card::personName, false),
    ColumnTableModel("Состояние", 20, Card::stateValue, false),
    ColumnTableModel("Изменен", 20, Card::updatedFormat, false),
    ColumnTableModel("Номер карты", 40, Card::cardNumberByFormat, false),
    ColumnTableModel("Карта до", 20, Card::newCardEndFormat, false),
    ColumnTableModel("Продукт", 50, Card::productName, false),
    ColumnTableModel("Офис", 30, Card::departmentName, false),
    ColumnTableModel("Старая до", 20, Card::oldCardEndFormat, false)
)

object TableUnclaimedCard : EntityTable<Card>(cardInHomeColumns, UnclaimedStoreCardService)

object TableErrorCard : EntityTable<Card>(cardErrorColumns, ErrorStoreCardService)

private val cardErrorColumns = listOf(
    ColumnTableModel("Держатель", 50, Card::personName, false),
    ColumnTableModel("Состояние", 20, Card::stateValue, false),
    ColumnTableModel("Изменен", 20, Card::updatedFormat, false),
    ColumnTableModel("Номер карты", 40, Card::cardNumberByFormat, false),
    ColumnTableModel("Карта до", 20, Card::newCardEndFormat, false),
    ColumnTableModel("Продукт", 50, Card::productName, false),
    ColumnTableModel("Офис", 30, Card::departmentName, false),
    ColumnTableModel("Старая до", 20, Card::oldCardEndFormat, false),
    ColumnTableModel("Сообщение об ошибке", 100, Card::messageResponse, false)
)

object TableOutClientCard : EntityTable<Card>(outClientCardColumns, OutClientStoreCardService)

private val outClientCardColumns = listOf(
    ColumnTableModel("Держатель", 50, Card::personName, false),
    ColumnTableModel("Выдана", 10, Card::updatedFormat, false),
    ColumnTableModel("Номер карты", 40, Card::cardNumberByFormat, false),
    ColumnTableModel("Карта до", 10, Card::newCardEndFormat, false),
    ColumnTableModel("Продукт", 50, Card::productName, false),
    ColumnTableModel("Офис", 30, Card::departmentName, false),
    ColumnTableModel("Выдавший", 15, Card::updater, false),
    ColumnTableModel("SMS-сервис", 20, Card::phone, false)
)

object TableCloseCard : EntityTable<Card>(closeCardColumns, CloseStoreCardService)

private val closeCardColumns = listOf(
    ColumnTableModel("Держатель", 50, Card::personName, false),
    ColumnTableModel("Выдана", 20, Card::updatedFormat, false),
    ColumnTableModel("Номер карты", 40, Card::cardNumberByFormat, false),
    ColumnTableModel("Карта до", 20, Card::newCardEndFormat, false),
    ColumnTableModel("Продукт", 50, Card::productName, false),
    ColumnTableModel("Офис", 30, Card::departmentName, false),
    ColumnTableModel("Выдавший", 20, Card::updater, false),
    ColumnTableModel("Счет всё", 20, Card::isCloseAccount, false)
)


