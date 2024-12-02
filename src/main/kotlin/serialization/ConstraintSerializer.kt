package org.example.serialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import org.example.model.Constraint
import org.example.model.TimeConstraint
import java.time.Duration
import java.time.LocalTime
import java.time.OffsetTime
import java.time.ZoneOffset

class ConstraintSerializer: StdDeserializer<TimeConstraint>(TimeConstraint::class.java) {
    override fun deserialize(parser: JsonParser?, context: DeserializationContext?): TimeConstraint {
        parser?.codec?.readTree<JsonNode>(parser)?.let { tree ->
            val type = tree.get("type").textValue()
            return when (type) {
                "FROM_NOW" ->TimeConstraint.fromNow(
                    Duration.parse(tree.get("offset").textValue())
                )
                "DAILY" -> TimeConstraint.dailyRun(
                    LocalTime.parse(tree.get("time").textValue())
                )

                else -> throw IllegalArgumentException("Could not parse $type")
            }
        } ?: throw IllegalStateException("Parser is null!")
    }
}