package org.example.mbta

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.mbta.model.MbtaApiResponse
import org.example.mbta.model.Route
import org.example.mbta.model.Schedule
import org.example.mbta.model.Stop
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

open class MbtaApi(
    private val objectMapper: ObjectMapper,
    private val url: String = MBTA_URL
) {

    private val httpClient = HttpClient.newHttpClient()

    private val cache = mutableMapOf<Request, Pair<String, MbtaApiResponse>>()


    /**
     * Predictions and schedules leaving from stopName towards destination
     */
    fun getDepartures(route: String, stopName: String, destination: String): List<Schedule> {
        return getPredictions(route, stopName, destination) + getSchedule(route, stopName, destination)
    }

    fun getPredictions(route: String, stopName: String, destination: String): List<Schedule> {
        return getScheduleOrPrediction(
            "predictions",  route, stopName, destination
        )
    }

    fun getSchedule(route: String, stopName: String, destination: String): List<Schedule> {
        return getScheduleOrPrediction(
            "schedules", route, stopName, destination
        )
    }

    fun getTripsArrival(tripIds: List<String>, route: String, end: String): List<Schedule> {
        return getScheduledOrPredictedArrival("predictions", tripIds, route, end) + getScheduledOrPredictedArrival("schedules", tripIds, route, end)
    }

    private fun getScheduledOrPredictedArrival(
        path: String, tripIds: List<String>, route: String, end: String
    ): List<Schedule> {
        val stopId = getStopId(route, end)
        val res = makeGetRequest(
            path,
            mapOf("trip" to tripIds.joinToString(","), "stop" to stopId)
        )
        return responseAsWithId(Schedule::class.java, res)
    }


    private fun getScheduleOrPrediction(
        path: String, route: String, stopName: String, destination: String
    ): List<Schedule> {
        val stopId = getStopId(route, stopName)
        val directionId = getDirectionId(route, destination)
        val res = makeGetRequest(
            path,
            mapOf(
                "stop" to stopId,
                "route" to route,
                "direction_id" to directionId.toString(),
                "include" to "trip"
            )
        )
        return responseAsWithId(Schedule::class.java, res)
    }

    private fun getDirectionId(route: String, destination: String): Int {
        val destinations = getRouteDirectionDestinations(route)
        return destinations.indexOf(destination)
    }

    private fun getStopId(route: String, stopName: String): String {
        return makeGetRequest("stops", mapOf("route" to route)).data
            .map { Pair(it.id, objectMapper.convertValue(it.attributes, Stop::class.java)) }
            .find { (_, stop) -> stop.name == stopName }
            ?.first
            ?: throw IllegalArgumentException("Could not find stop: $stopName")
    }

    private fun getRouteDirectionDestinations(route: String): List<String> {
        val res = makeGetRequest("routes", mapOf("id" to route))
        val route = responseAs(Route::class.java, res)[0]
        return route.directionDestinations
    }

    private fun <T> responseAsWithId(clazz: Class<T>, res: MbtaApiResponse): List<T> {
        return res.data.map {
            objectMapper.convertValue(it, clazz)
        }
    }

    private fun <T> responseAs(clazz: Class<T>, res: MbtaApiResponse): List<T> {
        return res.data.map { objectMapper.convertValue(it.attributes, clazz) }
    }

    private fun makeGetRequest(path: String, filters: Map<String, String>): MbtaApiResponse {
        val request = createGetRequest(path, filters)
        val cachedResponse = checkCache(path, filters)
        if (cachedResponse != null) {
            return cachedResponse
        }

        val response = httpClient.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        )
        if (response.statusCode() != 200) { throw RuntimeException("API call failed: ${response.body()}") }
        val mbtaApiResponse = objectMapper.readValue(response.body(), MbtaApiResponse::class.java)
        response.headers().map()["Last-Modified"]?.let {
             cache[Request(path, filters)] = Pair(it[0], mbtaApiResponse)
        }
        return mbtaApiResponse
    }

    private fun checkCache(path: String, filters: Map<String, String>): MbtaApiResponse? {
        return cache[Request(path, filters)]?.let { (lastModified, previousResponse) ->
            val req = HttpRequest.newBuilder().GET()
                .uri(makeRequestUri(path, filters))
                .header("x-api-key", API_KEY)
                .header("If-Modified-Since", lastModified.toString())
                .build()
            val response = httpClient.send(req, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() == 304) previousResponse else
                objectMapper.readValue(response.body(), MbtaApiResponse::class.java)
        }
    }


    private fun createGetRequest(path: String, filters: Map<String, String>): HttpRequest =
        HttpRequest.newBuilder().GET()
            .uri(makeRequestUri(path, filters))
            .header("x-api-key", API_KEY)
            .build()

    private fun makeRequestUri(path: String, filters: Map<String, String>): URI {
        val paramsString = filters
            .map { (k, v) -> "$k=$v" }
            .joinToString("&")
        return URI.create("$url/$path?$paramsString")
    }

    companion object {
        const val MBTA_URL = "https://api-v3.mbta.com"

        const val API_KEY = "5a812e62bdd24075b11434c5dc0004c9"

        data class Request(val path: String, val filters: Map<String, String>)
    }

}