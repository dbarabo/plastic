package ru.barabo.plastic.schema.entity.variable

enum class VariableType(val label: String) {

    PRIOR_CONDITION("Эквайр. условие"),
    CONDITION("Условие"),
    AMOUNT_VAR("Сумма-функция"),
    DESCRIPTION_VAR("Назначение платежа-функция"),
    NONE("НЕТ");

    override fun toString(): String = label
}