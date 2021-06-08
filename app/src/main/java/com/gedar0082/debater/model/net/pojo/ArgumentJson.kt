 package com.gedar0082.debater.model.net.pojo

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

data class ArgumentJson (
    @JsonProperty("id") val id: Long,
    @JsonProperty("title") val title: String,
    @JsonProperty("statement") val statement: String,
    @JsonProperty("answer") val answer_id: ArgumentJson?,
    @JsonProperty("debate_id") val debate_id: DebateJson?,
    @JsonProperty("thesis_id") val thesis_id: ThesisJson?,
    @JsonProperty("person_id") val person_id: PersonJson?,
    @JsonProperty("date_time") val date_time: Timestamp?,
    @JsonProperty("type") val type : Int?
){
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + statement.hashCode()
        result = 31 * result + (answer_id?.hashCode() ?: 0)
        result = 31 * result + (debate_id?.hashCode() ?: 0)
        result = 31 * result + (thesis_id?.hashCode() ?: 0)
        result = 31 * result + (person_id?.hashCode() ?: 0)
        result = 31 * result + (date_time.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "id = ${id}\n " +
                "title = ${title}\n" +
                "statement = ${statement}\n" +
                "answer = ${answer_id}\n" +
                "debate_id = ${debate_id}\n" +
                "thesis_id = ${thesis_id}\n" +
                "person_id = ${person_id}\n" +
                "date_time = ${date_time}\n" +
                "type = ${type}\n"
    }
}