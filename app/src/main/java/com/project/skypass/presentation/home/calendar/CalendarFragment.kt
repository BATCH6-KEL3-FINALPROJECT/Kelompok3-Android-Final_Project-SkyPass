package com.project.skypass.presentation.home.calendar

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.project.skypass.R
import com.project.skypass.data.model.DateCalendar
import com.project.skypass.databinding.FragmentCalendarBinding
import com.project.skypass.presentation.customview.DataSelection
import com.project.skypass.utils.convertDateCalendar
import com.project.skypass.utils.displayText
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCalendarBinding
    private val viewModel: CalendarHomeViewModel by viewModel()
    var dateSelection: DataSelection? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setInitialDate()
        setSelectedDate()
        setValue()
        calendarView()
    }

    private fun setInitialDate() {
        val initialDepartureDateStr = arguments?.getString("currentDateDeparture")
        val initialReturnDateStr = arguments?.getString("currentDateReturn")

        val initialDepartureDate =
            if (initialDepartureDateStr != "Belum dipilih") {
                val convertDateDeparture = initialDepartureDateStr?.let { convertDateCalendar(it) }
                LocalDate.parse(convertDateDeparture)
            } else {
                null
            }

        val initialReturnDate =
            if (initialReturnDateStr != "Belum dipilih") {
                val convertDateReturn = initialReturnDateStr?.let { convertDateCalendar(it) }
                LocalDate.parse(convertDateReturn)
            } else {
                null
            }

        viewModel.setInitialDates(initialDepartureDate, initialReturnDate)
    }

    private fun setSelectedDate() {
        viewModel.selectedDateDeparture.observe(viewLifecycleOwner) { date ->
            if (date != null) {
                binding.tvDateDeparture.text = date.format(DateTimeFormatter.ofPattern(getString(R.string.format_date)))
                binding.rvDate.notifyCalendarChanged()
            }
        }

        viewModel.selectedDateReturn.observe(viewLifecycleOwner) { date ->
            if (date != null) {
                binding.tvDateReturn.text = date.format(DateTimeFormatter.ofPattern(getString(R.string.format_date)))
                binding.rvDate.notifyCalendarChanged()
            }
        }
    }

    private fun setValue() {
        val currentDateDeparture = arguments?.getString("currentDateDeparture", "Belum dipilih")
        val currentDateReturn = arguments?.getString("currentDateReturn", "Belum dipilih")
        /*binding.tvDateDeparture.text = currentDateDeparture
        binding.tvDateReturn.text = currentDateReturn*/
        binding.tvDateDeparture.text = currentDateDeparture ?: "Belum dipilih"
        binding.tvDateReturn.text = currentDateReturn ?: "Belum dipilih"
    }

    private fun sendData(date: String) {
        binding.btnCalendar.setOnClickListener {
            val selectedDateDeparture = binding.tvDateDeparture.text.toString()
            val selectedDateReturn = binding.tvDateReturn.text.toString()

            val selectedDateDepartureAll = DateCalendar(selectedDateDeparture, date)
            val selectedDateReturnAll = DateCalendar(selectedDateReturn, date)

            // val tag = arguments?.getString("tag")

            if (tag == "departure") {
                dateSelection?.onDateSelected(tag ?: "", selectedDateDepartureAll)
            } else if (tag == "return") {
                dateSelection?.onDateSelected(tag ?: "", selectedDateReturnAll)
            }
            // dateSelection?.onDateSelected(tag ?: "", selectedDate)
            dismiss()
        }
    }

    private fun calendarView() {
        binding.rvDate.dayBinder =
            object : MonthDayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)

                override fun bind(
                    container: DayViewContainer,
                    data: CalendarDay,
                ) {
                    container.day = data
                    container.textView.text = data.date.dayOfMonth.toString()

                    if (data.position == DayPosition.MonthDate) {
                        container.textView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorTextCalendar))
                        when (data.date) {
                            viewModel.selectedDateDeparture.value -> {
                                container.textView.setBackgroundResource(R.drawable.bg_button)
                                container.textView.setTextColor(Color.WHITE)
                            }
                            viewModel.selectedDateReturn.value -> {
                                container.textView.setBackgroundResource(R.drawable.bg_button)
                                container.textView.setTextColor(Color.WHITE)
                            }
                            else -> {
                                // container.textView.background = null
                                val departureDate = viewModel.selectedDateDeparture.value
                                val returnDate = viewModel.selectedDateReturn.value
                                if (departureDate != null && returnDate != null && data.date.isAfter(departureDate) && data.date.isBefore(returnDate)) {
                                    container.textView.setBackgroundResource(R.drawable.bg_date_calendar_between)
                                    container.textView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorTextCalendar))
                                } else {
                                    container.textView.background = null
                                }
                            }
                        }
                    } else {
                        container.textView.setTextColor(Color.GRAY)
                        container.textView.background = null
                    }
                    container.textView.setOnClickListener {
                        if (tag == "departure") {
                            sendData(data.date.toString())
                            viewModel.selectDepartureDate(data.date)
                        } else if (tag == "return") {
                            sendData(data.date.toString())
                            viewModel.selectReturnDate(data.date)
                        }
                        binding.rvDate.notifyCalendarChanged()
                    }
                }
            }
        val daysOfWeek = daysOfWeek()
        binding.layoutWeek.root.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                textView.text = daysOfWeek[index].displayText()
            }
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)
        val firstDayOfWeek = firstDayOfWeekFromLocale()
        binding.rvDate.setup(startMonth, endMonth, firstDayOfWeek)
        binding.rvDate.scrollToMonth(currentMonth)
        binding.rvDate.monthScrollListener = { month ->
            val formatter = DateTimeFormatter.ofPattern(getString(R.string.format_month_year))
            binding.tvMonthCalendar.text = month.yearMonth.format(formatter)
        }
    }

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        val textView: TextView = view.findViewById(R.id.tv_date)
        lateinit var day: CalendarDay

        init {
            view.setOnClickListener {
            }
        }
    }
}
