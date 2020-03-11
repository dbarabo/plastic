package ru.barabo.report.gui

import org.jdesktop.swingx.JXDatePicker
import org.jdesktop.swingx.calendar.SingleDaySelectionModel
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.FlowLayout
import java.text.DateFormat
import java.util.*
import javax.swing.*
import javax.swing.text.DateFormatter
import javax.swing.text.DefaultFormatterFactory

class DateTimePicker() : JXDatePicker() {

    private val logger = LoggerFactory.getLogger(DateTimePicker::class.java)

    private var timeSpinner: JSpinner? = null
    private var timePanel: JPanel? = null
    private var timeFormat: DateFormat? = null

    constructor(date: Date?) : this() {
        this.date = date
    }

    override fun commitEdit() {
        commitTime()
        super.commitEdit()

        logger.error("commitEdit=$date")
    }

    override fun cancelEdit() {
        super.cancelEdit()
        //setTimeSpinners()
    }

    override fun getLinkPanel(): JPanel {
        super.getLinkPanel()

        if (timePanel == null) {
            timePanel = createTimePanel()
        }

        setTimeSpinners()

        return timePanel!!
    }

    private fun createTimePanel(): JPanel {
        val newPanel = JPanel()
        newPanel.layout = FlowLayout()
        val dateModel = SpinnerDateModel()
        timeSpinner = JSpinner(dateModel)
        if (timeFormat == null) timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT)
        updateTextFieldFormat()
        newPanel.add(JLabel("Time:"))
        newPanel.add(timeSpinner)
        newPanel.background = Color.WHITE
        return newPanel
    }

    private fun updateTextFieldFormat() {
        if (timeSpinner == null) return
        val tf: JFormattedTextField = (timeSpinner?.editor as JSpinner.DefaultEditor).textField
        val factory: DefaultFormatterFactory = tf.getFormatterFactory() as DefaultFormatterFactory
        val formatter: DateFormatter = factory.defaultFormatter as DateFormatter
        // Change the date format to only show the hours
        formatter.setFormat(timeFormat)
    }

    private fun commitTime() {
        val oldDate: Date = this.date
        val time = timeSpinner?.value as Date
        val timeCalendar = GregorianCalendar()
        timeCalendar.time = time
        val calendar = GregorianCalendar()
        calendar.time = oldDate
        calendar[Calendar.HOUR_OF_DAY] = timeCalendar[Calendar.HOUR_OF_DAY]
        calendar[Calendar.MINUTE] = timeCalendar[Calendar.MINUTE]
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        val newDate = calendar.time

        logger.error("before=${this.date}")
        logger.error("newDate=$newDate")

        this.date = newDate
        logger.error("after=${this.date}")
    }

    private fun setTimeSpinners() {
        timeSpinner?.value = date ?: Date()
    }

    fun getTimeFormat(): DateFormat? {
        return timeFormat
    }

    fun setTimeFormat(timeFormat: DateFormat?) {
        this.timeFormat = timeFormat
        updateTextFieldFormat()
    }

    init {
        // monthView.selectionModel = SingleDaySelectionModel()
    }
}