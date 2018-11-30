package ru.barabo.plastic.unnamed.general

import ru.barabo.total.db.FilteredStore

interface StoreInPath {

    fun createUnnamedCards(countCards: Int, productCardTypeId: Long)

    fun selectApplicationStore()
}

interface FilteredStoreInPath<E> : StoreInPath, FilteredStore<E>