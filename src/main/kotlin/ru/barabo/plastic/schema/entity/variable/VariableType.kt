package ru.barabo.plastic.schema.entity.variable

import ru.barabo.plastic.schema.service.TypeParams

enum class VariableType(val label: String, val resultAndParams: List<TypeParams>) {

    PRIOR_CONDITION("Эквайр. условие", AQUARING_PRIOR_CONDITION_PARAMS),
    CONDITION("Условие", SIMPLE_CONDITION_PARAMS),
    AMOUNT_VAR("Гр.Сумма-функция", GROUP_SUM_FUNC_PARAMS),
    DESCRIPTION_VAR("Назначение платежа-функция", GROUP_DESCRIPTION_FUNC_PARAMS),
    NONE("НЕТ", listOf(TypeParams.NONE));

    override fun toString(): String = label
}

/**
 * result-ConditionVariant; params: idCtl, idTerminal
 */
private val AQUARING_PRIOR_CONDITION_PARAMS = listOf(TypeParams.VARCHAR2, TypeParams.NUMBER, TypeParams.VARCHAR2)

/**
 * result-ConditionVariant; param: idTransactCtl
 */
private val SIMPLE_CONDITION_PARAMS = listOf(TypeParams.VARCHAR2, TypeParams.NUMBER)

/**
 * result-amount;
 * params: minIdTransactCtl, sumAmount
 */
private val GROUP_SUM_FUNC_PARAMS = listOf(TypeParams.NUMBER,
    TypeParams.NUMBER, TypeParams.VARCHAR2, TypeParams.NUMBER)

/**
 * result-description;
 * params: minIdTransactCtl, allSumAmount, calcSumAmount, defDescription
 */
private val GROUP_DESCRIPTION_FUNC_PARAMS = listOf(TypeParams.VARCHAR2,
    TypeParams.NUMBER, TypeParams.NUMBER, TypeParams.NUMBER, TypeParams.VARCHAR2)
