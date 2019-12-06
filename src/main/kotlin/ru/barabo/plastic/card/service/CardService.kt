package ru.barabo.plastic.card.service

import ru.barabo.plastic.release.packet.data.PlatinaCashIn

interface CardService {

    fun deleteContent()

    fun changeProduct(newProductName: String)

    fun changePin()

    fun reIssueCards()

    fun addToRegisterCard()

    fun createFile()

    fun createFileForAll()

    fun toCreateFileState()

    fun toSmsState()

    fun goHomeState()

    fun toDopikIsSuccess(): Boolean

    fun toGetFromOfficeIsSuccess(): Boolean

    fun outClientOnlyIsSuccess(): Boolean

    fun prepareCreditCardOut(limit: Number)

    fun beforePreparePlatinaCardOut(): PlatinaCashIn

    fun endPreparePlatinaCardOut(platinaCashIn: PlatinaCashIn): Number
}