package com.project.skypass.presentation.calendar

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
import com.project.skypass.databinding.FragmentCalendarBinding
import com.project.skypass.presentation.customview.DataSelection
import com.project.skypass.utils.displayText
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCalendarBinding
    var dateSelection: DataSelection? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setValue()
        calendarView()
        sendData()
    }

    private fun setValue() {
        val currentDateDeparture = arguments?.getString("currentDateDeparture", "Belum dipilih")
        val currentDateReturn = arguments?.getString("currentDateReturn", "Belum dipilih")
        binding.tvDateDeparture.text = currentDateDeparture
        binding.tvDateReturn.text = currentDateReturn
    }

    private fun sendData() {
        binding.btnCalendar.setOnClickListener {
            val selectedDateDeparture = binding.tvDateDeparture.text.toString()
            val selectedDateReturn = binding.tvDateReturn.text.toString()
            if (tag == "departure") {
                dateSelection?.onDateSelected(tag ?: "", selectedDateDeparture)
            } else if (tag == "return") {
                dateSelection?.onDateSelected(tag ?: "", selectedDateReturn)
            }
            //dateSelection?.onDateSelected(tag ?: "", selectedDate)
            dismiss()
        }
    }

    private fun calendarView() {
        binding.rvDate.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                container.textView.text = data.date.dayOfMonth.toString()
                if (data.position == DayPosition.MonthDate) {
                    container.textView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorTextCalendar))
                } else {
                    container.textView.setTextColor(Color.GRAY)
                }
                container.textView.setOnClickListener {
                    if (tag == "departure") {
                        binding.tvDateDeparture.text = data.date.format(DateTimeFormatter.ofPattern(getString(R.string.format_date)))
                    } else if (tag == "return") {
                        binding.tvDateReturn.text = data.date.format(DateTimeFormatter.ofPattern(getString(R.string.format_date)))
                    }
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
        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed
        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
        binding.rvDate.setup(startMonth, endMonth, firstDayOfWeek)
        binding.rvDate.scrollToMonth(currentMonth)
        binding.rvDate.monthScrollListener = { month ->
            val formatter = DateTimeFormatter.ofPattern(getString(R.string.format_month_year))
            binding.tvMonthCalendar.text = month.yearMonth.format(formatter)
        }
    }

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        val textView = view.findViewById<TextView>(R.id.tv_date)
        // Will be set when this container is bound
        lateinit var day: CalendarDay

        init {
            view.setOnClickListener {
                // Use the CalendarDay associated with this container.
            }
        }
    }

}