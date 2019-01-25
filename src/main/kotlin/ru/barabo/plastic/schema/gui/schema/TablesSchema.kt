package ru.barabo.plastic.schema.gui.schema

import ru.barabo.gui.swing.table.ColumnTableModel
import ru.barabo.gui.swing.table.EntityTable
import ru.barabo.plastic.schema.entity.schema.HeaderTransactType
import ru.barabo.plastic.schema.entity.schema.Schema
import ru.barabo.plastic.schema.entity.schema.TransType
import ru.barabo.plastic.schema.service.schema.HeaderTransactTypeService
import ru.barabo.plastic.schema.service.schema.SchemaService
import ru.barabo.plastic.schema.service.schema.TransTypeService

object TableHeaderTransactType : EntityTable<HeaderTransactType>(headerColumns, HeaderTransactTypeService)

private val headerColumns = listOf(
    ColumnTableModel("Заголовок", 50, HeaderTransactType::headerName, false)
)

object TableTransType : EntityTable<TransType>(transTypeColumns, TransTypeService)

private val transTypeColumns = listOf(
    ColumnTableModel("Наименование", 100, TransType::name, false),
    ColumnTableModel("Тип транзакции", 100, TransType::transactType, false)
)

object TableSchema : EntityTable<Schema>(schemaColumns, SchemaService)

private val schemaColumns = listOf(
    ColumnTableModel("Дебет", 100, Schema::debetName, true),
    ColumnTableModel("Кредит", 100, Schema::creditName, true),

    ColumnTableModel("Это дебет", 30, Schema::isDebetIndicator, true),
    ColumnTableModel("реверс индикатора", 30, Schema::reverseIndicator, true),
    ColumnTableModel("Это внутр.перевод", 100, Schema::isInternalTransfer, true),

    ColumnTableModel("Рассчет.Сумма", 80, Schema::amountName, true),
    ColumnTableModel("Рассчет.Назначение", 80, Schema::descriptionName, true),

    ColumnTableModel("Сортировка", 30, Schema::rowOrder, false)
)
