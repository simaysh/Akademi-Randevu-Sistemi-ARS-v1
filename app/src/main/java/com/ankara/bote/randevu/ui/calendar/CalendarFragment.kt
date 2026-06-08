package com.ankara.bote.randevu.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ankara.bote.randevu.R
import com.ankara.bote.randevu.databinding.FragmentCalendarBinding
import com.ankara.bote.randevu.ui.appointments.AppointmentAdapter
import com.ankara.bote.randevu.ui.main.MainViewModel
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val vm: MainViewModel by activityViewModels()

    private var selectedDate = LocalDate.now()
    private val appointmentDates = mutableSetOf<String>()
    private val keyFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val displayFmt = DateTimeFormatter.ofPattern("d MMMM yyyy, EEEE", Locale("tr", "TR"))
    private val monthFmt = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("tr", "TR"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apptAdapter = AppointmentAdapter { vm.cancelAppointment(it) }
        binding.rvDayAppointments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDayAppointments.adapter = apptAdapter

        setupCalendar()

        vm.myAppointments.observe(viewLifecycleOwner) { appointments ->
            appointmentDates.clear()
            // Sadece aktif ve onaylanmış randevuları takvimde işaretleyelim
            appointments.filter { it.status == "APPROVED" || it.status == "PENDING" }
                .forEach { appointmentDates.add(it.date) }
            binding.calendarView.notifyCalendarChanged()
            filterForDate(selectedDate, apptAdapter)
        }
    }

    private fun setupCalendar() {
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(12)
        val endMonth = currentMonth.plusMonths(12)

        class DayContainer(view: View) : ViewContainer(view) {
            val tvDay: TextView = view.findViewById(R.id.tvDay)
            val dot: View = view.findViewById(R.id.dotView)
            val bg: View = view.findViewById(R.id.backgroundView)
            lateinit var day: CalendarDay
            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        val prev = selectedDate
                        selectedDate = day.date
                        vm.selectDate(day.date)
                        binding.tvSelectedDate.text = day.date.format(displayFmt)
                        binding.calendarView.notifyDateChanged(prev)
                        binding.calendarView.notifyDateChanged(day.date)
                        (binding.rvDayAppointments.adapter as? AppointmentAdapter)?.let {
                            filterForDate(day.date, it)
                        }
                    }
                }
            }
        }

        binding.calendarView.dayBinder = object : MonthDayBinder<DayContainer> {
            override fun create(view: View) = DayContainer(view)
            override fun bind(container: DayContainer, data: CalendarDay) {
                container.day = data
                container.tvDay.text = data.date.dayOfMonth.toString()
                val isSelected = data.date == selectedDate
                val isToday = data.date == LocalDate.now()
                val isCurrent = data.position == DayPosition.MonthDate
                val hasAppt = appointmentDates.contains(data.date.format(keyFmt))

                container.tvDay.alpha = if (isCurrent) 1f else 0.3f
                
                when {
                    isSelected -> {
                        container.bg.setBackgroundResource(R.drawable.bg_selected_day)
                        container.bg.visibility = View.VISIBLE
                        container.tvDay.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                    isToday -> {
                        container.bg.setBackgroundResource(R.drawable.bg_today)
                        container.bg.visibility = View.VISIBLE
                        container.tvDay.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary))
                    }
                    else -> {
                        container.bg.background = null
                        container.bg.visibility = View.INVISIBLE
                        container.tvDay.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                    }
                }
                
                container.dot.visibility = if (hasAppt && !isSelected) View.VISIBLE else View.GONE
            }
        }

        binding.calendarView.monthScrollListener = { month ->
            binding.tvMonthYear.text = month.yearMonth.format(monthFmt)
                .replaceFirstChar { it.uppercase() }
        }

        binding.calendarView.setup(startMonth, endMonth, firstDayOfWeekFromLocale())
        binding.calendarView.scrollToMonth(currentMonth)
        binding.tvSelectedDate.text = selectedDate.format(displayFmt)

        binding.btnPrevMonth.setOnClickListener {
            val current = binding.calendarView.findFirstVisibleMonth()?.yearMonth ?: return@setOnClickListener
            binding.calendarView.smoothScrollToMonth(current.minusMonths(1))
        }
        binding.btnNextMonth.setOnClickListener {
            val current = binding.calendarView.findFirstVisibleMonth()?.yearMonth ?: return@setOnClickListener
            binding.calendarView.smoothScrollToMonth(current.plusMonths(1))
        }
    }

    private fun filterForDate(date: LocalDate, adapter: AppointmentAdapter) {
        val key = date.format(keyFmt)
        val filtered = vm.myAppointments.value?.filter { it.date == key } ?: emptyList()
        adapter.submitList(filtered)
        binding.tvNoAppointments.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.rvDayAppointments.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
