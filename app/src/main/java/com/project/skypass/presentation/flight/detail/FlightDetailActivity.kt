package com.project.skypass.presentation.flight.detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import coil.load
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import com.project.skypass.R
import com.project.skypass.data.model.FilterFlight
import com.project.skypass.data.model.Flight
import com.project.skypass.data.model.OrderUser
import com.project.skypass.databinding.ActivityFlightDetailBinding
import com.project.skypass.databinding.ItemDayBinding
import com.project.skypass.presentation.customview.FilterFlightSelected
import com.project.skypass.presentation.customview.OnItemAdapterClickedListener
import com.project.skypass.presentation.flight.detail.adapter.FlightDetailAdapter
import com.project.skypass.presentation.flight.filter.FilterFragment
import com.project.skypass.presentation.flight.result.FlightResultActivity
import com.project.skypass.presentation.main.MainActivity
import com.project.skypass.utils.convertMinutesToHours
import com.project.skypass.utils.displayText
import com.project.skypass.utils.getWeekPageTitle
import com.project.skypass.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class FlightDetailActivity : AppCompatActivity(), FilterFlightSelected {
    private val binding: ActivityFlightDetailBinding by lazy {
        ActivityFlightDetailBinding.inflate(layoutInflater)
    }
    private val flightDetailViewModel: FlightDetailViewModel by viewModel()
    private lateinit var flightDetailAdapter: FlightDetailAdapter

    private var selectedDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getArgumentData()
        setClickListeners()
        setupAdapter()
        observeFlightData()
    }

    private fun selectedDate() {
        val date = flightDetailViewModel.date
        selectedDate = date?.let { LocalDate.parse(it) }
        selectedDate?.let {
            flightDetailViewModel.setDepartureDate = it.toString()
            flightDetailViewModel.setSelectedDate(it)
            setupCalendarView(it)
            setupAdapter()
            observeFlightData()
        }
    }

    private fun setClickListeners() {
        binding.ivBackFlight.setOnClickListener {
            onBackPressed()
        }
        binding.cardFilter.setOnClickListener {
            val filterFragment = FilterFragment()
            filterFragment.filterFlight = this
            filterFragment.show(supportFragmentManager, "filter")
        }
        flightDetailAdapter.setOnTicketClickListener {
        }
    }

    private fun onItemClick(flight: Flight) {
        intent.extras?.getParcelable<OrderUser>(EXTRA_FLIGHT)?.let {
            sendOrderData(it, flight)
        }
    }

    private fun setupAdapter() {
        val itemClickListener = object : OnItemAdapterClickedListener<Flight> {
            override fun onClicked(item: Flight) {
                onItemClick(item)
            }
        }

        flightDetailAdapter = FlightDetailAdapter(itemClickListener)
        binding.rvTicket.adapter = flightDetailAdapter
    }

    private fun setupCalendarView(selectedDate: LocalDate) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            val bind = ItemDayBinding.bind(view)
            lateinit var day: WeekDay

            init {
                view.setOnClickListener {
                    if (this@FlightDetailActivity.selectedDate != day.date) {
                        this@FlightDetailActivity.selectedDate = day.date
                        val oldDate = this@FlightDetailActivity.selectedDate
                        flightDetailViewModel.setDepartureDate = day.date.toString()
                        flightDetailViewModel.setSelectedDate(day.date)
                        binding.cvCalender.notifyDateChanged(day.date)
                        oldDate?.let { binding.cvCalender.notifyDateChanged(it) }
                        selectedDate()
                    }
                }
            }

            fun bind(day: WeekDay) {
                this.day = day
                val dateFormatter = DateTimeFormatter.ofPattern("dd")
                bind.tvDay.text = dateFormatter.format(day.date)
                bind.tvWeek.text = day.date.dayOfWeek.displayText()
                bind.exSevenSelectedView.isVisible = day.date == selectedDate
            }
        }

        binding.cvCalender.dayBinder =
            object : WeekDayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)

                override fun bind(
                    container: DayViewContainer,
                    data: WeekDay,
                ) = container.bind(data)
            }

        binding.cvCalender.weekScrollListener = {
            binding.tvMonthFlight.text = getWeekPageTitle(it)
        }

        val currentMonth = YearMonth.now()
        binding.cvCalender.setup(
            currentMonth.minusMonths(5).atStartOfMonth(),
            currentMonth.plusMonths(5).atEndOfMonth(),
            firstDayOfWeekFromLocale(),
        )
        binding.cvCalender.scrollToDate(selectedDate)
    }

    private fun saveToOrderHistory(item: OrderUser) {
        flightDetailViewModel.saveToOrderHistory(item).observe(this) {
            it.proceedWhen()
        }
    }

    private fun observeFlightData() {
        flightDetailViewModel.getFlightDetail().observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.rvTicket.isVisible = true
                    // binding.pbLoading.isVisible = false
                    binding.shimmerViewContainer.isVisible = false
                    binding.shimmerViewContainer.stopShimmer()
                    binding.ivEmptyTicket.isVisible = false
                    binding.tvEmptyTicket.isVisible = false
                    binding.tvEmptyTicketSub.isVisible = false
                    binding.btnEditSearch.isVisible = false
                    it.payload?.let { data ->
                        flightDetailAdapter.submitData(data)
                    }
                },
                doOnLoading = {
                    binding.rvTicket.isVisible = false
                    // binding.pbLoading.isVisible = true
                    binding.shimmerViewContainer.isVisible = true
                    binding.shimmerViewContainer.startShimmer()
                    binding.ivEmptyTicket.isVisible = false
                    binding.tvEmptyTicket.isVisible = false
                    binding.tvEmptyTicketSub.isVisible = false
                    binding.btnEditSearch.isVisible = false
                },
                doOnError = {
                    // binding.pbLoading.isVisible = false
                    binding.shimmerViewContainer.isVisible = false
                    binding.shimmerViewContainer.stopShimmer()
                    binding.ivEmptyTicket.isVisible = true
                    binding.tvEmptyTicket.isVisible = true
                    binding.tvEmptyTicketSub.isVisible = true
                    binding.btnEditSearch.isVisible = true
                },
            )
        }
    }

    private fun getArgumentData() {
        intent.extras?.getParcelable<OrderUser>(EXTRA_FLIGHT)?.let {
            flightDetailViewModel.getHomeData(it)
            saveToOrderHistory(it)
            setProfileData(it)
            // selectedDate = LocalDate.parse(convertFlightDetail(it.departureDate!!))
            selectedDate()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setProfileData(item: OrderUser) {
        binding.tvTypeFlight.text = item.seatClass
        binding.tvPassenger.text = "${item.passengersTotal} Penumpang"
        binding.tvDestination.text = "${item.departureCity} > ${item.arrivalCity}"

        val linkLoad = "https://github.com/riansyah251641/food_app_asset/blob/main/banner/empty_flight_ticket.png?raw=true"
        binding.ivEmptyTicket.load(linkLoad) {
            crossfade(true)
            error(R.drawable.bg_no_internet)
        }
        binding.btnEditSearch.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun sendOrderData(
        item: OrderUser,
        itemFlight: Flight,
    ) {
        FlightResultActivity.sendDataOrder(
            this,
            OrderUser(
                // Home data
                id = item.id,
                arrivalCity = item.arrivalCity,
                arrivalDate = item.arrivalDate,
                seatClass = item.seatClass,
                departureCity = item.departureCity,
                departureDate = item.departureDate,
                passengersTotal = item.passengersTotal,
                passengersAdult = item.passengersAdult,
                passengersBaby = item.passengersBaby,
                passengersChild = item.passengersChild,
                isRoundTrip = item.isRoundTrip,
                supportRoundTrip = item.supportRoundTrip,
                orderDate = item.orderDate,
                // Flight data (One Way)
                airlineCode = itemFlight.airlineCode,
                airlineName = itemFlight.airlineName,
                arrivalAirportName = itemFlight.arrivalAirportName,
                arrivalIATACode = itemFlight.arrivalIATACode,
                arrivalTime = itemFlight.arrivalTime,
                departureAirportName = itemFlight.departureAirportName,
                departureIATACode = itemFlight.departureIATACode,
                departureTime = itemFlight.departureTime,
                flightCode = itemFlight.flightCode,
                flightDescription = itemFlight.flightDescription,
                flightDuration = itemFlight.flightDuration,
                flightDurationFormat =
                    itemFlight.flightDuration?.let { duration ->
                        val (_, _) =
                            convertMinutesToHours(
                                duration,
                            )
                    }.toString(),
                flightId = itemFlight.flightId,
                flightStatus = itemFlight.flightStatus,
                flightSeat = itemFlight.seatClass,
                flightArrivalDate = itemFlight.arrivalDate,
                flightDepartureDate = itemFlight.departureDate,
                planeType = itemFlight.planeType,
                priceAdult = item.priceAdult, // add edit
                priceBaby = item.priceBaby, // add edit
                priceChild = item.priceChild, // add edit
                priceTotal = itemFlight.price,
                paymentPrice = item.paymentPrice, // add edit
                seatsAvailable = itemFlight.seatsAvailable,
                terminal = itemFlight.terminal,
                // Flight data (Round Trip)
                airlineCodeRoundTrip = item.airlineCodeRoundTrip,
                airlineNameRoundTrip = item.airlineNameRoundTrip,
                arrivalAirportNameRoundTrip = item.arrivalAirportNameRoundTrip,
                arrivalIATACodeRoundTrip = item.arrivalIATACodeRoundTrip,
                arrivalTimeRoundTrip = item.arrivalTimeRoundTrip,
                departureAirportNameRoundTrip = item.departureAirportNameRoundTrip,
                departureIATACodeRoundTrip = item.departureIATACodeRoundTrip,
                departureTimeRoundTrip = item.departureTimeRoundTrip,
                flightCodeRoundTrip = item.flightCodeRoundTrip,
                flightDescriptionRoundTrip = item.flightDescriptionRoundTrip,
                flightDurationRoundTrip = item.flightDurationRoundTrip,
                flightDurationFormatRoundTrip = item.flightDurationFormatRoundTrip,
                flightIdRoundTrip = item.flightIdRoundTrip,
                flightStatusRoundTrip = item.flightStatusRoundTrip,
                flightSeatRoundTrip = item.flightSeatRoundTrip,
                flightArrivalDateRoundTrip = item.flightArrivalDateRoundTrip,
                flightDepartureDateRoundTrip = item.flightDepartureDateRoundTrip,
                planeTypeRoundTrip = item.planeTypeRoundTrip,
                priceAdultRoundTrip = item.priceAdultRoundTrip,
                priceBabyRoundTrip = item.priceBabyRoundTrip,
                priceChildRoundTrip = item.priceChildRoundTrip,
                priceTotalRoundTrip = item.priceTotalRoundTrip,
                paymentPriceRoundTrip = item.paymentPriceRoundTrip,
                seatsAvailableRoundTrip = item.seatsAvailableRoundTrip,
                terminalRoundTrip = item.terminalRoundTrip,
            ),
        )
    }

    companion object {
        const val EXTRA_FLIGHT = "extra_flight"

        fun startActivity(
            context: Context,
            orderData: OrderUser,
        ) {
            val intent = Intent(context, FlightDetailActivity::class.java)
            intent.putExtra(EXTRA_FLIGHT, orderData)
            context.startActivity((intent))
        }
    }

    override fun onFilterSelected(
        tag: String,
        filter: FilterFlight,
    ) {
        when (tag) {
            "filter" -> {
                binding.tvFilterCondition.text = filter.criteria
                flightDetailViewModel.applyFilter(filter)
                observeFlightData()
            }
        }
    }
}
