package ru.barabo.plastic.unnamed.general

import ru.barabo.plastic.release.main.data.DBStorePlastic
import ru.barabo.plastic.unnamed.data.RowFieldClient
import ru.barabo.plastic.unnamed.data.RowFieldInPath
import ru.barabo.plastic.unnamed.gui.errorMessage

data class ClientOutData(
    var clientField: RowFieldClient = RowFieldClient(),
    var phone: String ="",
    var codeWord: String = "") {

    override fun toString(): String = clientField.id?.let { "${clientField.name} ${clientField.birthday()}" } ?: SELECT_CLIENT_LABEL

    fun checkClientId() = if(clientField.id == null) errorMessage(SELECT_CLIENT_ERROR) else true

    companion object {
        private const val SELECT_CLIENT_LABEL = "Нажмите для выбора клиента..."

        private const val SELECT_CLIENT_ERROR = "Не выбран клиент"

        fun createByCardField(cardField: RowFieldInPath): ClientOutData {
            val clientField = if(cardField.isUnnamedPerson()) RowFieldClient() else getClientById(cardField.getPersonId())

            return ClientOutData(clientField = clientField, phone = (clientField.phone()?:""))
        }

        private fun getClientById(personId: Long?): RowFieldClient {
            if(personId == null) return RowFieldClient()

            return DBStorePlastic.getInstance().clientSelect.let {
                it.setClientCriteria(ClientCriteria(id = personId))

                if (it.data.isEmpty()) RowFieldClient() else it.data[0]
            }
        }
    }
}