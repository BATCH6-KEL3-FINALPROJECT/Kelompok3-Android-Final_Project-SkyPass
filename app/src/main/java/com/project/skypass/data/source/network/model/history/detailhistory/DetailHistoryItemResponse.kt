package com.project.skypass.data.source.network.model.history.detailhistory

import com.google.gson.annotations.SerializedName

data class DetailHistoryItemResponse(
    @SerializedName("adultPrice")
    var adultPrice: Int?,
    @SerializedName("booking_code")
    var bookingCode: String?,
    @SerializedName("booking_date")
    var bookingDate: String?,
    @SerializedName("booking_id")
    var bookingId: String?,
    @SerializedName("createdAt")
    var createdAt: String?,
    @SerializedName("Flight")
    var flight: Flight?,
    @SerializedName("flight_id")
    var flightId: String?,
    @SerializedName("is_round_trip")
    var isRoundTrip: Boolean?,
    @SerializedName("no_of_ticket")
    var noOfTicket: Int?,
    @SerializedName("payment_id")
    var paymentId: String?,
    @SerializedName("status")
    var status: String?,
    @SerializedName("Tickets")
    var tickets: List<Ticket>?,
    @SerializedName("totalAdult")
    var totalAdult: Int?,
    @SerializedName("totalBaby")
    var totalBaby: Int?,
    @SerializedName("totalChild")
    var totalChild: Int?,
    @SerializedName("total_price")
    var totalPrice: String?,
    @SerializedName("user_id")
    var userId: String?,
)
