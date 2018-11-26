package ru.barabo.plastic.afina

import java.io.File
import java.net.InetAddress

object VersionChecker {

    private const val PROGRAM_NAME = "PLASTIC.JAR"

    private const val VERSION_JAR = 1

    @JvmStatic
    fun checkVersion(): String {
        val versionNow = AfinaQuery.selectValueType<Number>(SELECT_MIN_VERSION) ?: return ""

        if(versionNow.toInt() <= VERSION_JAR) return ""

        addInfoForUpdateJar()

        return NEED_TO_UPDATE
    }

    private const val NEED_TO_UPDATE =
        "Ваша версия программы устарела\n После того как Вы закончите работать с ней - она обновится"

    private fun addInfoForUpdateJar() {
        val ip = InetAddress.getLocalHost().hostAddress

        val fullPath = File(VersionChecker::class.java.protectionDomain.codeSource.location.path).absolutePath

        if(isNotExistsToUpdate(ip, fullPath)) {
            addToUpdateInfo(ip, fullPath);
        }
    }

    private fun addToUpdateInfo(ip: String, fullPath: String) =
        AfinaQuery.execute(INSERT_UPDATE_PROGRAM, arrayOf(ip, fullPath))

    private fun isNotExistsToUpdate(ip: String, fullPath: String): Boolean {

        val isExists = AfinaQuery.selectValueType<Number>(SELECT_EXISTS_TOUPDATE, arrayOf(ip, fullPath)) ?: return true

        return isExists.toInt() == 0
    }

    private const val SELECT_MIN_VERSION = """
select coalesce(min(VERSION_NOW), 0)
from od.ptkb_version_jar j
where j.state = 0
  and j.program = '$PROGRAM_NAME'
  and j.due < sysdate"""

    private const val SELECT_EXISTS_TOUPDATE = """
select count(*) from dual
where exists(select 1 from od.ptkb_version_jar_toupdate u
where u.state = 0
  and u.ip_address = ?
  and u.local_path = ?
)"""

    private const val INSERT_UPDATE_PROGRAM =
        "insert into od.ptkb_version_jar_toupdate (ID, IP_ADDRESS, LOCAL_PATH) values (classified.nextval, ?, ?)"
}
