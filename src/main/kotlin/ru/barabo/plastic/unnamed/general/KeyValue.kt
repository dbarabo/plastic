package ru.barabo.plastic.unnamed.general

data class KeyValue(val key: Long, val value: String) {
    override fun toString(): String = value
}