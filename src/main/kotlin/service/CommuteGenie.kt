package org.example.service

import org.example.mbta.MbtaApi
import org.example.model.Constraint
import org.example.model.Option
import org.example.model.Trip
import org.example.model.UserConfiguration

class CommuteGenie(
    private val mbtaApi: MbtaApi,
    private val userConfigurations: Map<String, UserConfiguration>
) {

    fun userTripOptions(username: String): Map<Option, List<Trip>> =
        userConfigurations[username]?.options?.associate { Pair(it.option, tripMeetsConstraint(it)) }
            ?: throw IllegalArgumentException("Could not find user $username")

    /**
     * Checks if a constraint can be met using MBTA predictions and schedules.
     *
     * e.g., is there a red line train from South Station to charles/mgh that
     *  - leaves later than 10 minutes from now
     *  - arrives less than 8am
     */
    fun tripMeetsConstraint(constraint: Constraint): List<Trip> {
        val option = constraint.option
        val departures = mbtaApi.getDepartures(option.route, option.startStation, option.destination)
        val leaveAfter = departures.filter {
            it.departureTime?.let { time -> time >= constraint.minDepartureTime.get() } ?: false
        }
        val tripIds = leaveAfter.map { it.tripId }
        val arrivals = leaveAfter.zip(
            mbtaApi.getTripsArrival(tripIds, option.route, option.endStation)
        ).map { (leaving, arriving) -> Pair(leaving.departureTime!!, arriving) }
        val arriveBefore = arrivals.filter { it.second.arrivalTime?.let {
            time -> time <= constraint.maxDepartureTime.get()
        } ?: false }
        return arriveBefore .map { Trip(option, it.first, it.second.arrivalTime!!) }
    }
}