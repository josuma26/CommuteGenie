package org.example.service.handlers

import com.amazon.ask.dispatcher.request.handler.HandlerInput
import com.amazon.ask.dispatcher.request.handler.RequestHandler
import com.amazon.ask.model.LaunchRequest
import com.amazon.ask.model.Response
import com.amazon.ask.request.Predicates.requestType
import java.util.*

class LaunchRequestHandler: RequestHandler {
    override fun canHandle(input: HandlerInput): Boolean {
        return input.matches(requestType(LaunchRequest::class.java))
    }

    override fun handle(input: HandlerInput): Optional<Response> {
        return input.responseBuilder
            .withSpeech("Good morning, please tell me who is commuting.")
            .build()
    }
}