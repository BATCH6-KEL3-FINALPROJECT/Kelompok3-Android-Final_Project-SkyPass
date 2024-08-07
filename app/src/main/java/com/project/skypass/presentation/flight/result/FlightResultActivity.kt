package com.project.skypass.presentation.flight.result

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.project.skypass.R
import com.project.skypass.data.model.OrderUser
import com.project.skypass.databinding.ActivityFlightResultBinding
import com.project.skypass.presentation.checkout.checkoutDataOrder.CheckoutDataOrdersActivity
import com.project.skypass.presentation.flight.detail.FlightDetailActivity
import com.project.skypass.presentation.main.LoginBottomSheetFragment
import com.project.skypass.utils.capitalizeWords
import com.project.skypass.utils.toIndonesianFormat
import org.koin.androidx.viewmodel.ext.android.viewModel

class FlightResultActivity : AppCompatActivity() {
    private val binding: ActivityFlightResultBinding by lazy {
        ActivityFlightResultBinding.inflate(layoutInflater)
    }

    private val viewModel: FlightResultViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getArgumentData()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.ivBackDetail.setOnClickListener {
            onBackPressed()
        }
    }

    private fun bindFlightData(flight: OrderUser) {
        flight.let { item ->

            val infoDetail = item.flightDescription?.replace(", ", "\n")
            val infoDetailRoundTrip = item.flightDescriptionRoundTrip?.replace(", ", "\n")

            binding.tvTotalPrice.text = item.priceTotal.toIndonesianFormat()
            binding.rvTicketDetail.tvAirportDeparture.text = item.departureAirportName
            binding.rvTicketDetail.tvAirportArrival.text = item.arrivalAirportName
            binding.rvTicketDetail.tvAirline.text = item.airlineName
            binding.rvTicketDetail.tvCityDeparture.text = item.departureCity
            binding.rvTicketDetail.tvCityArrival.text = item.arrivalCity
            binding.rvTicketDetail.tvDateDeparture.text = item.flightDepartureDate
            binding.rvTicketDetail.tvDateArrival.text = item.flightArrivalDate
            binding.rvTicketDetail.tvTimeDeparture.text = item.departureTime
            binding.rvTicketDetail.tvTimeArrival.text = item.arrivalTime
            binding.rvTicketDetail.tvInfoDetail.text = infoDetail
            binding.rvTicketDetail.tvFlightCode.text = item.flightCode
            binding.rvTicketDetail.tvSeatClass.text = item.seatClass?.capitalizeWords()

            if (item.isRoundTrip == false && item.supportRoundTrip == true) {
                binding.tvTotalPrice.text = (item.priceTotal?.plus(item.priceTotalRoundTrip!!)).toIndonesianFormat()
                binding.btnSelectFlight.text = getString(R.string.text_lanjut_checkout)
                binding.tvTotal.text = getString(R.string.text_total_harga)
                // change departure to arrival
                binding.rvTicketDetail.tvAirportDeparture.text = item.departureAirportNameRoundTrip
                binding.rvTicketDetail.tvAirportArrival.text = item.arrivalAirportNameRoundTrip
                binding.rvTicketDetail.tvAirline.text = item.airlineNameRoundTrip
                binding.rvTicketDetail.tvCityDeparture.text = item.arrivalCity
                binding.rvTicketDetail.tvCityArrival.text = item.departureCity
                binding.rvTicketDetail.tvDateDeparture.text = item.flightDepartureDateRoundTrip
                binding.rvTicketDetail.tvDateArrival.text = item.flightArrivalDateRoundTrip
                binding.rvTicketDetail.tvTimeDeparture.text = item.departureTimeRoundTrip
                binding.rvTicketDetail.tvTimeArrival.text = item.arrivalTimeRoundTrip
                binding.rvTicketDetail.tvInfoDetail.text = item.flightDescriptionRoundTrip
                binding.rvTicketDetail.tvFlightCode.text = item.flightCodeRoundTrip
                binding.rvTicketDetail.tvSeatClass.text = item.seatClass?.capitalizeWords()

                binding.tvTitleFlightResult.text = getString(R.string.text_detail_penerbangan)
                binding.rvTicketDetailRound.tvAirportDeparture.text = item.departureAirportName
                binding.rvTicketDetailRound.tvAirportArrival.text = item.arrivalAirportName
                binding.rvTicketDetailRound.tvAirline.text = item.airlineName
                binding.rvTicketDetailRound.tvCityDeparture.text = item.departureCity
                binding.rvTicketDetailRound.tvCityArrival.text = item.arrivalCity
                binding.rvTicketDetailRound.tvDateDeparture.text = item.flightDepartureDate
                binding.rvTicketDetailRound.tvDateArrival.text = item.flightArrivalDate
                binding.rvTicketDetailRound.tvTimeDeparture.text = item.departureTime
                binding.rvTicketDetailRound.tvTimeArrival.text = item.arrivalTime
                binding.rvTicketDetailRound.tvInfoDetail.text = infoDetailRoundTrip
                binding.rvTicketDetailRound.tvFlightCode.text = item.flightCode
                binding.rvTicketDetailRound.tvSeatClass.text = item.seatClass?.capitalizeWords()
                binding.rvTicketDetailRound.root.isVisible = true
            } else {
                binding.rvTicketDetailRound.root.isVisible = false
            }
        }
    }

    private fun getArgumentData() {
        intent.extras?.getParcelable<OrderUser>(EXTRA_FLIGHT)?.let {
            sendOrderData(it)
            bindFlightData(it)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun sendOrderData(item: OrderUser) {
        val infoDetail = item.flightDescription?.replace(", ", "\n")
        val infoDetailRoundTrip = item.flightDescriptionRoundTrip?.replace(", ", "\n")

        binding.btnSelectFlight.setOnClickListener {
            if (!viewModel.isLogin()) {
                val loginBottomSheetFragment = LoginBottomSheetFragment()
                loginBottomSheetFragment.show(supportFragmentManager, loginBottomSheetFragment.tag)
            } else {
                val updatedItem = item.copy(flightDescription = infoDetail, flightDescriptionRoundTrip = infoDetailRoundTrip)
                if (item.isRoundTrip == false) {
                    CheckoutDataOrdersActivity.sendDataOrder(
                        this,
                        updatedItem,
                    )
                } else {
                    FlightDetailActivity.startActivity(
                        this,
                        OrderUser(
                            // Change departure into arrival only in date & city
                            id = item.id,
                            arrivalCity = item.departureCity,
                            arrivalDate = item.departureDate,
                            seatClass = item.seatClass,
                            departureCity = item.arrivalCity,
                            departureDate = item.arrivalDate,
                            passengersTotal = item.passengersTotal,
                            passengersAdult = item.passengersAdult,
                            passengersBaby = item.passengersBaby,
                            passengersChild = item.passengersChild,
                            // change is round trip into false
                            isRoundTrip = false,
                            supportRoundTrip = item.supportRoundTrip,
                            orderDate = item.orderDate,
                            // set data round trip
                            airlineCode = item.airlineCodeRoundTrip,
                            airlineName = item.airlineNameRoundTrip,
                            arrivalAirportName = item.arrivalAirportNameRoundTrip,
                            arrivalIATACode = item.arrivalIATACodeRoundTrip,
                            arrivalTime = item.arrivalTimeRoundTrip,
                            departureAirportName = item.departureAirportNameRoundTrip,
                            departureIATACode = item.departureIATACodeRoundTrip,
                            departureTime = item.departureTimeRoundTrip,
                            flightCode = item.flightCodeRoundTrip,
                            flightDescription = infoDetailRoundTrip,
                            flightDuration = item.flightDurationRoundTrip,
                            flightDurationFormat = item.flightDurationFormatRoundTrip,
                            flightId = item.flightIdRoundTrip,
                            flightStatus = item.flightStatusRoundTrip,
                            flightSeat = item.flightSeatRoundTrip,
                            flightArrivalDate = item.flightArrivalDateRoundTrip,
                            flightDepartureDate = item.flightDepartureDateRoundTrip,
                            planeType = item.planeTypeRoundTrip,
                            priceAdult = item.priceAdultRoundTrip,
                            priceBaby = item.priceBabyRoundTrip,
                            priceChild = item.priceChildRoundTrip,
                            priceTotal = item.priceTotalRoundTrip,
                            paymentPrice = item.paymentPriceRoundTrip,
                            seatsAvailable = item.seatsAvailableRoundTrip,
                            terminal = item.terminalRoundTrip,
                            // Save data from one trip
                            airlineCodeRoundTrip = item.airlineCode,
                            airlineNameRoundTrip = item.airlineName,
                            arrivalAirportNameRoundTrip = item.arrivalAirportName,
                            arrivalIATACodeRoundTrip = item.arrivalIATACode,
                            arrivalTimeRoundTrip = item.arrivalTime,
                            departureAirportNameRoundTrip = item.departureAirportName,
                            departureIATACodeRoundTrip = item.departureIATACode,
                            departureTimeRoundTrip = item.departureTime,
                            flightCodeRoundTrip = item.flightCode,
                            flightDescriptionRoundTrip = infoDetail,
                            flightDurationRoundTrip = item.flightDuration,
                            flightDurationFormatRoundTrip = item.flightDurationFormat,
                            flightIdRoundTrip = item.flightId,
                            flightStatusRoundTrip = item.flightStatus,
                            flightSeatRoundTrip = item.flightSeat,
                            flightArrivalDateRoundTrip = item.flightArrivalDate,
                            flightDepartureDateRoundTrip = item.flightDepartureDate,
                            planeTypeRoundTrip = item.planeType,
                            priceAdultRoundTrip = item.priceAdult,
                            priceBabyRoundTrip = item.priceBaby,
                            priceChildRoundTrip = item.priceChild,
                            priceTotalRoundTrip = item.priceTotal,
                            paymentPriceRoundTrip = item.paymentPrice,
                            seatsAvailableRoundTrip = item.seatsAvailable,
                            terminalRoundTrip = item.terminal,
                        ),
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRAS = "EXTRAS"
        const val EXTRA_FLIGHT = "extra_flight"

        fun sendDataOrder(
            context: Context,
            orderUser: OrderUser,
        ) {
            val intent = Intent(context, FlightResultActivity::class.java)
            intent.putExtra(EXTRA_FLIGHT, orderUser)
            context.startActivity(intent)
        }
    }
}
