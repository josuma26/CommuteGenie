package org.example.module

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.AbstractModule
import com.google.inject.Provides
import org.example.mbta.MbtaApi
import org.example.model.TimeConstraint
import org.example.model.UserConfiguration
import org.example.serialization.ConstraintSerializer
import org.example.service.CommuteGenie

class CommuteGenieModule: AbstractModule() {

    @Provides
    fun provideCommuteGenie(mbtaApi: MbtaApi, configs: List<UserConfiguration>): CommuteGenie {
        return CommuteGenie(mbtaApi, configs.associateBy { it.username })
    }

    @Provides
    fun provideConfigs(objectMapper: ObjectMapper): List<UserConfiguration> {
        return objectMapper.readValue(
            javaClass.classLoader.getResourceAsStream("users.json")!!
        )
    }

    @Provides
    fun provideMbtaApi(objectMapper: ObjectMapper): MbtaApi = MbtaApi(objectMapper)

    @Provides
    fun provideObjectMapper(): ObjectMapper {
        return ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(
                SimpleModule().addDeserializer(
                TimeConstraint::class.java, ConstraintSerializer()
            ))
    }
}