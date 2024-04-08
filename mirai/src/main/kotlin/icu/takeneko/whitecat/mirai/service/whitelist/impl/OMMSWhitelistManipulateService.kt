package icu.takeneko.whitecat.mirai.service.whitelist.impl

import icu.takeneko.whitecat.mirai.data.http.HttpResponseData
import icu.takeneko.whitecat.mirai.data.http.RequestStatus
import icu.takeneko.whitecat.mirai.data.http.WhitelistQueryData
import icu.takeneko.whitecat.mirai.data.whitelist.OMMSWhitelistConfiguration
import icu.takeneko.whitecat.mirai.data.whitelist.WhitelistGroupConfiguration
import icu.takeneko.whitecat.mirai.service.whitelist.WhitelistManipulateService
import icu.takeneko.whitecat.mirai.util.json
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.io.path.*

class OMMSWhitelistManipulateService(val groupUin: String): WhitelistManipulateService {

    private lateinit var configuration: OMMSWhitelistConfiguration
    private val filePath = Path("./data/$groupUin-omms-server.json")
    private val httpClient by lazy {
        HttpClient(CIO) {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials("whitecat", configuration.token)
                    }
                    realm = "http api"
                }
            }
            install(ContentNegotiation){
                json(Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                    prettyPrint = false
                    isLenient = true
                })
            }
        }
    }

    init {
        if (!filePath.exists()) {
            configuration = OMMSWhitelistConfiguration("http://localhost:50001", "XX_YOUR_API_TOKEN_HERE", mapOf("example" to WhitelistGroupConfiguration()))
            write()
        }
        try {
            configuration = json.decodeFromString(filePath.readText())
            write()
        } catch (t: Throwable) {
            t.printStackTrace()
            configuration = OMMSWhitelistConfiguration("http://localhost:50001", "XX_YOUR_API_TOKEN_HERE", mapOf("example" to WhitelistGroupConfiguration()))
            write()
        }
    }

    private fun write() {
        filePath.deleteIfExists()
        val s = json.encodeToString(configuration)
        filePath.createFile()
        filePath.writeText(s)
    }

    override fun initService() {

    }

    private fun requestForResults(target: String, player: String, action:String): Map<String, RequestResult>{
        val requests =
            (configuration.groups[target] ?: throw IllegalArgumentException("Whitelist group $target not exist"))
                .whitelists
                .map { it to WhitelistQueryData(it, listOf(player)) }
        val responses = mutableMapOf<String, RequestResult>()
        for ((id, request) in requests) {
            responses += id to runBlocking {
                val response = httpClient.post("${configuration.serverHttpApiUrl}/api/whitelist/$action"){
                    this.setBody(request)
                    contentType(ContentType.Application.Json)
                }
                if (response.status != HttpStatusCode.OK){
                    throw RuntimeException("Non 200 status: ${response.status.value}, $response")
                }
                val data = response.body<HttpResponseData>()
                if (data.status != RequestStatus.ACCEPTED)throw RuntimeException("Server returned Non ACCEPTED status: ${data.status}, $data")
                val success = json.decodeFromString<List<String>>(data.extra["success"]!!)
                val failure = json.decodeFromString<List<String>>(data.extra["failure"]!!)
                return@runBlocking RequestResult(success, failure)
            }
        }
        return responses
    }

    override fun add(target: String, player: String) {
        val result = requestForResults(target, player, "add")
        if (result.any { it.value.failure.isNotEmpty() }){
            throw RuntimeException("Add $player to group $target failed, got result: $result")
        }
    }

    override fun remove(target: String, player: String) {
        val result = requestForResults(target, player, "remove")
        if (result.any { it.value.failure.isNotEmpty() }){
            throw RuntimeException("Remove $player from group $target failed, got result: $result")
        }
    }

    override fun listAvailable(): Map<String, String> {
        return configuration.groups.mapValues { it.value.description }

    }

    private data class RequestResult(val success:List<String>, val failure:List<String>)
}