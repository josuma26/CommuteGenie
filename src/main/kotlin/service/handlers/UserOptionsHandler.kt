package org.example.service.handlers

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.impl.IntentRequestHandler
import com.amazon.ask.model.IntentRequest
import com.amazon.ask.model.Response
import com.amazon.ask.request.Predicates
import com.amazon.ask.request.RequestHelper
import com.google.inject.Guice
import org.example.common.TimeUtils
import org.example.model.Option
import org.example.model.Trip
import org.example.module.CommuteGenieModule
import org.example.service.CommuteGenie
import java.util.*
import kotlin.jvm.optionals.getOrNull


class UserOptionsHandler: IntentRequestHandler {

    private val commuteGenie: CommuteGenie;

    init {
        val injector = Guice.createInjector(CommuteGenieModule())
        commuteGenie = injector.getInstance(CommuteGenie::class.java)
    }

    override fun canHandle(input: HandlerInput, intentRequest: IntentRequest): Boolean {
        return input.matches(Predicates.intentName("GetUserOptionsIntent"))
    }

    override fun handle(input: HandlerInput, intentRequest: IntentRequest): Optional<Response> {
        val requestHelper = RequestHelper.forHandlerInput(input)

        val username = requestHelper.getSlotValue("username").getOrNull()

        return input.responseBuilder
            .withSpeech(responseForUsername(username))
            .build()
    }

    fun responseForUsername(username: String?): String {
        return username?.let {
            responseFromOptions(
                commuteGenie.userTripOptions(it)
            )
        } ?: "I don't know how to help you, tell me who's commuting to check."
    }

    private fun responseFromOptions(options: Map<Option, List<Trip>>): String {
        return options.filter { it.value.isNotEmpty() }
            .map { (option, trips) ->
                val take = option.name ?: "${option.startStation} to ${option.endStation}"
                val leaves = TimeUtils.timeString(trips[0].departureTime)
                val arrives = TimeUtils.timeString(trips[0].arrivalTime)
                "you can take ${take}, leaves $leaves from ${option.startStation} and gets to ${option.endStation} at $arrives"
            }
            .joinToString(" or ")
            .ifEmpty { "It's too late man, work from home" }
    }

}