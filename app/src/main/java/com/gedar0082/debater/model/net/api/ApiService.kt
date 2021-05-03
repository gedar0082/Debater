package com.gedar0082.debater.model.net.api

import com.gedar0082.debater.model.net.notification.PushNotification
import com.gedar0082.debater.model.net.pojo.*
import com.gedar0082.debater.util.Constants.Companion.CONTENT_TYPE
import com.gedar0082.debater.util.Constants.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    /*
    fcm authentication
     */

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>

    /*
    person block
     */

    @GET("getPersonById")
    suspend fun getPersonById(@Query("id") id: Long): PersonJson

    @GET("getPersonByEmail")
    suspend fun getPersonByEmail(@Query("email") email: String): PersonJson

    @GET("getAllPersons")
    suspend fun getAllPersons(): List<PersonJson>

    @DELETE("deletePersonById")
    suspend fun deletePersonById(@Query("id") id: Long)

    @PUT("updatePerson")
    suspend fun updatePerson(@Body personJson: PersonJson)

    @POST("insertPerson")
    suspend fun insertPerson(@Body personJson: PersonAuthenticationJson) : PersonJson

    @POST("checkAuthentication")
    suspend fun checkAuthentication(@Body personJson: PersonAuthenticationJson) : Response<BodyMessageJson>

    @POST("checkRegistration")
    suspend fun checkRegistration(@Body personJson: PersonAuthenticationJson) : Response<BodyMessageJson>

    /*
    debate block
     */

    @GET("getDebateById")
    suspend fun getDebateById(@Query("id") id: Long): DebateJson

    @GET("getAllDebates")
    suspend fun getAllDebates(): List<DebateJson>

    @DELETE("deleteDebateById")
    suspend fun deleteDebateById(@Query("id") id: Long)

    @PUT("updateDebate")
    suspend fun updateDebate(@Body debateJson: DebateJson)

    @POST("insertDebate")
    suspend fun insertDebate(@Body debateJson: DebateJson) : DebateJson

    /*
    person_debate block
     */

    @GET("getPersonDebateByDebateIdAndPersonId")
    suspend fun getPersonDebateByDebateIdAndPersonId(
        @Query("debate_id") debateId: Long,
        @Query("person_id") personId: Long
    )
            : PersonDebateJson

    @GET("getPersonDebateByDebateId")
    suspend fun getPersonDebateByDebateId(@Query("debate_id") debateId: Long)
            : List<PersonDebateJson>

    @GET("getPersonDebateByPersonId")
    suspend fun getPersonDebateByPersonId(@Query("person_id") personId: Long)
            : List<PersonDebateJson>

    @DELETE("deletePersonDebate")
    suspend fun deletePersonDebate(@Body personDebateRawJson: PersonDebateRawJson)

    @PUT("updatePersonDebate")
    suspend fun updatePersonDebate(@Body personDebateJson: PersonDebateJson)

    @POST("insertRawPersonDebate")
    suspend fun insertRawPersonDebate(@Body personDebateRawJson: PersonDebateRawJson)

}