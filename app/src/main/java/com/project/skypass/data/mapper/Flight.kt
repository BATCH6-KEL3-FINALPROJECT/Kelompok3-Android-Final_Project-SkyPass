package com.project.skypass.data.mapper

import com.project.skypass.data.model.Flight
import com.project.skypass.data.source.network.model.flight.flightdata.FlightItemResponse

fun FlightItemResponse?.toFlight() =
    Flight(
        flightId = this?.flight_id,
        airlineCode = this?.airline_code,
        airlineName = this?.airline_name,
        arrivalIATACode = this?.arrival_iata_code,
        arrivalAirportName = this?.arrival_airport_name,
        arrivalTime = this?.arrival_time,
        departureIATACode = this?.departure_iata_code,
        departureAirportName = this?.departure_airport_name,
        departureTime = this?.departure_time,
        arrivalCity = this?.arrival_city,
        departureCity = this?.departure_city,
        arrivalDate = this?.arrival_date,
        departureDate = this?.departure_date,
        terminal = this?.terminal,
        flightCode = this?.flight_code,
        flightStatus = this?.flight_status,
        flightDuration = this?.flight_duration,
        flightDescription = this?.flight_description,
        planeType = this?.plane_type,
        price = this?.price,
        priceForInfant = this?.price_for_infant,
        priceForChild = this?.price_for_child,
        seatClass = this?.seat_class,
        seatsAvailable = this?.seats_available,
    )

fun Collection<FlightItemResponse>?.toFlightData() = this?.map { it.toFlight() } ?: listOf()