package ru.barabo.plastic.schema.gui.selector

import ru.barabo.db.service.StoreFilterService
import ru.barabo.plastic.schema.entity.selector.SelectClient
import ru.barabo.plastic.schema.gui.account.groupPanel
import ru.barabo.plastic.unnamed.general.bySqlLike2Plus
import ru.barabo.plastic.unnamed.general.bySqlLikePlus
import ru.barabo.plastic.unnamed.general.parseLong
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JPanel
import kotlin.reflect.KProperty0

class FilterSelectClient(process: KProperty0<TabsBookProcessOk<SelectClient>>,
                         store: StoreFilterService<SelectClient>
) : JPanel() {
    init {
        layout = GridBagLayout()

        selectCancelButton(process, store)

        groupPanel("Клиент/Банк", 0, 2, 1).apply {

            textFieldHorizontal("Наименование", 0).apply {
                setFilterClientByKey { SelectClient.filter.filterEntity.label = it?.bySqlLike2Plus() }
                text = SelectClient.filter.filterEntity.label
            }

            textFieldHorizontal("ИНН(БИК)", 1).apply {
                setFilterClientByKey { SelectClient.filter.filterEntity.innOrBik = it?.bySqlLikePlus() }
                text = SelectClient.filter.filterEntity.innOrBik
            }
        }

        groupPanel("Клиент/Банк", 0, 2, 2).apply {

            textFieldHorizontal("Описание Клиента/Банка", 2).apply {
                setFilterClientByKey { SelectClient.filter.filterEntity.description = it?.bySqlLike2Plus() }
                text = SelectClient.filter.filterEntity.description
            }

            textFieldHorizontal("ID Клиента/Банка", 1).apply {
                setFilterClientByKey { SelectClient.filter.filterEntity.id = it?.parseLong() }
                text = SelectClient.filter.filterEntity.id?.toString()
            }
        }

        SelectClient.filter.initStoreChecker(store, ::isCheckedFilter)
    }

    private fun isCheckedFilter(selectClient: SelectClient): Boolean =
        with(selectClient) {
            id != null ||
            label?.trim()?.length?:0 >= 7 ||
            description?.trim()?.length?:0 >= 11 ||
            innOrBik?.length?:0 >= 6
        }
}

fun JComponent.setFilterClientByKey(setter: (String?)->Unit) {
    addKeyListener( FilterKeyLister(SelectClient.filter, setter) )
}