package icu.takeneko.whitecat.mirai.data.request


import kotlinx.serialization.Serializable
import java.security.SecureRandom

@Serializable
data class WhitelistRequest(
    val player: String,
    val operation: RequestOps,
    val source: String,
    val sourceDescriptor: String,
    val group: String,
    val groupDescriptor: String,
    val targetDescriptor: String?,
    val requestId:Int = newRequestId()
) {

}

private val random = SecureRandom()

private fun newRequestId(): Int {
    return random.nextInt(1000000,9999999)
}