package ru.barabo.plastic.schema.gui.schema

import ru.barabo.gui.swing.table.ColumnTableModel
import ru.barabo.gui.swing.table.EntityTable
import ru.barabo.plastic.schema.entity.schema.ConditionVariant
import ru.barabo.plastic.schema.entity.schema.HeaderTransactType
import ru.barabo.plastic.schema.entity.schema.Schema
import ru.barabo.plastic.schema.entity.schema.TransType
import ru.barabo.plastic.schema.service.schema.ConditionVariantService
import ru.barabo.plastic.schema.service.schema.HeaderTransactTypeService
import ru.barabo.plastic.schema.service.schema.SchemaService
import ru.barabo.plastic.schema.service.schema.TransTypeService

object TableHeaderTransactType : EntityTable<HeaderTransactType>(headerColumns, HeaderTransactTypeService)

private val headerColumns = listOf(
    ColumnTableModel("Заголовок", 50, HeaderTransactType::headerName, false)
)

object TableTransType : EntityTable<TransType>(transTypeColumns, TransTypeService)

private val transTypeColumns = listOf(
    ColumnTableModel("Наименование", 200, TransType::name, false),
    ColumnTableModel("Тип транзакции", 60, TransType::transactType, false),
    ColumnTableModel("Эквайринг?", 30, TransType::isEquaringType, false),
    ColumnTableModel("Условие", 40, TransType::conditionName, false),
    ColumnTableModel("Категория", 30, TransType::categoryTransactName, false)
)

object TableConditionVariant : EntityTable<ConditionVariant>(conditionVariantColumns, ConditionVariantService)

private val conditionVariantColumns = listOf(
    ColumnTableModel("Вариант условия", 50, ConditionVariant::conditionVariant, true)
)

object TableSchema : EntityTable<Schema>(schemaColumns, SchemaService) {
    init {
        isReadOnly = true
    }
}

private val schemaColumns = listOf(
    ColumnTableModel("Дебет", 100, Schema::debetName, true),
    ColumnTableModel("Кредит", 100, Schema::creditName, true),

    ColumnTableModel("Это дебет", 30, Schema::isDebetIndicator, true),
    ColumnTableModel("реверс индикатора", 30, Schema::reverseIndicator, true),
    ColumnTableModel("Это внутр.перевод", 50, Schema::isInternalTransfer, true),

    ColumnTableModel("Рассчет.Сумма", 80, Schema::amountName, true),
    ColumnTableModel("Рассчет.Назначение", 80, Schema::descriptionName, true),

    ColumnTableModel("Сортировка", 30, Schema::rowOrder, true)
)

