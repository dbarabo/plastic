package ru.barabo.plastic.unnamed.general

import ru.barabo.total.db.DBStore

interface ClientCriteriaSetter {

    fun setClientCriteria(clientCriteria: ClientCriteria)
}

interface ClientCriteriaDBStore<E> : DBStore<E>, ClientCriteriaSetter

