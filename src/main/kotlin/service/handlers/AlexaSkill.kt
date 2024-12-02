package org.example.service.handlers

import com.amazon.ask.SkillStreamHandler
import com.amazon.ask.Skills

class AlexaSkill: SkillStreamHandler(skill) {
    companion object {
        private val skill = Skills.standard()
            .addRequestHandlers(
                UserOptionsHandler(),
                LaunchRequestHandler())
            .build()
    }
}