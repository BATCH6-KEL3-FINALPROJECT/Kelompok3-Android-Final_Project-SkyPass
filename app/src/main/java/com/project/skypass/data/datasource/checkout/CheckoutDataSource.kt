package com.project.skypass.data.datasource.checkout

import com.project.skypass.data.source.network.model.booking.GetBookingDataResponse
import com.project.skypass.data.source.network.model.checkout.request.CheckoutRequestResponse
import com.project.skypass.data.source.network.model.checkout.response.CheckoutResponse
import com.project.skypass.data.source.network.model.payment.PaymentResponse

interface CheckoutDataSource {
    suspend fun createBooking(token: String, bookingPayload: CheckoutRequestResponse): CheckoutResponse
    suspend fun getBookingData(token: String, bookingId: String): GetBookingDataResponse
    suspend fun createPayment(token: String, paymentId: String): PaymentResponse
}