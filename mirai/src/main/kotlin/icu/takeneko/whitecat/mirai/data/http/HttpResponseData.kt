package icu.takeneko.whitecat.mirai.data.http

import kotlinx.serialization.Serializable

@Serializable
data class HttpResponseData(
    val status: RequestStatus = RequestStatus.ACCEPTED,
    val content: String = "",
    val refuseReason: String = "",
    val extra: Map<String, String> = mapOf()
)
