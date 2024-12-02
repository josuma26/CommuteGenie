package org.example

import org.example.service.handlers.UserOptionsHandler

/**
 * Want program that tells me my commuting options:
 *
 *  - Commuter rail: 10min walk + train = is there a train leaving south station in >= 10 mins arriving westboro before 9:30am?
 *  - Shuttle: is there a green line out of park arriving northeastern before 7:55am
 *              or orange line out of dtc ruggles before 7:50am?
 *
 * In general, [List-of Constraint] Instant -> [List-of Option]
 *  - Option: station + departure time
 *  - Constraint: Option + min leave + max arrive
 *
 *  Scheduled vs predictions
 *  ----------------------
 *
 *  Predictions exist only for ongoing trips?
 *  Should prioritize predictions, but use schedules
 */
fun main() {
    val handler = UserOptionsHandler()
    println(handler.responseForUsername("Jose"))
}
