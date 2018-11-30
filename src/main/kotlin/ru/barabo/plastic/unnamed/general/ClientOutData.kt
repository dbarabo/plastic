package ru.barabo.plastic.unnamed.general

import ru.barabo.plastic.unnamed.data.RowFieldClient

data class ClientOutData(
    var clientField: RowFieldClient = RowFieldClient(),
    var phone: String ="",
    var codeWord: String = "") {

    override fun toString(): String = clientField.id?.let { "${clientField.name} ${clientField.birthday()}" } ?: SELECT_CLIENT_LABEL

    companion object {
        private const val SELECT_CLIENT_LABEL = "Нажмите для выбора клиента..."
    }
}