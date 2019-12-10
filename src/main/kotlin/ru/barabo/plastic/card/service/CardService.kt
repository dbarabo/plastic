package ru.barabo.plastic.card.service

import ru.barabo.plastic.release.packet.data.PlatinaCashIn

interface CardService {

    fun contentCount(plasticPack: Long?): Int

    fun deleteContent()

    fun changeProduct(newProductName: String)

    fun changePin()

    fun smsInfoAddRemove(newPhone: String, isAddSmsInfo: Boolean)

    fun reIssueCards()

    fun addToRegisterCard()

    fun createFile()

    fun createFileForAll()

    fun toCreateFileState()

    fun toSmsState()

    fun goHomeState(): Long?

    fun toDopikIsSuccess(): Boolean

    fun toGetFromOfficeIsSuccess(): Boolean

    fun outClientOnlyIsSuccess(): Boolean

    fun prepareCreditCardOut(limit: Number)

    fun beforePreparePlatinaCardOut(): PlatinaCashIn

    fun endPreparePlatinaCardOut(platinaCashIn: PlatinaCashIn): Number

    var filterMode: FilterMode
}

enum class FilterMode(val dbValue: Int?) {
    None(null),
    WithTurnOrRest(1)
}