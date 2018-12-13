package ru.barabo.plastic.unnamed.general

import ru.barabo.total.db.FilteredStore

interface StoreInTotal {
    fun selectApplicationStore()
}

interface StoreInPath : StoreInTotal {

    fun createUnnamedCards(countCards: Int, productCardTypeId: Long)
}

interface FilteredStoreInPath<E> : StoreInPath, FilteredStore<E>

interface StoreInHome : StoreInTotal {

    fun outCardToClient(resultOutClient: ResultOutClient)
}

interface FilteredStoreInHome<E> : StoreInHome, FilteredStore<E>

interface StoreOutClient : StoreInTotal {
    fun changePinCode()
}

interface FilteredStoreOutClient<E> : StoreOutClient, FilteredStore<E>