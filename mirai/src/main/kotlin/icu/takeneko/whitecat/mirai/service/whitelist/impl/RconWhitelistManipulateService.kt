package icu.takeneko.whitecat.mirai.service.whitelist.impl

import icu.takeneko.whitecat.mirai.data.whitelist.RconWhitelistConfiguration
import icu.takeneko.whitecat.mirai.service.whitelist.WhitelistManipulateService
import icu.takeneko.whitecat.mirai.util.json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import nl.vv32.rcon.Rcon
import java.net.InetSocketAddress
import java.nio.channels.SocketChannel
import java.rmi.RemoteException
import java.util.regex.Pattern
import kotlin.io.path.*

class RconWhitelistManipulateService(private val groupUin: Long) : WhitelistManipulateService {

    private val rcons = mutableMapOf<String, Rcon>()
    private val groupedRcons = mutableMapOf<String, List<Rcon>>()
    private val rconCommands = mutableMapOf<Rcon, RconCommand>()
    private val filePath = Path("./data/$groupUin-rcon-server.json")
    private var configuration: RconWhitelistConfiguration

    init {
        if (!filePath.exists()) {
            configuration = RconWhitelistConfiguration(mapOf(), mapOf())
            write()
        }
        try {
            configuration = json.decodeFromString(filePath.readText())
        } catch (t: Throwable) {
            t.printStackTrace()
            configuration = RconWhitelistConfiguration(mapOf(), mapOf())
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
        val ex = RuntimeException("Failed to enable rcon service.")
        configuration.servers.forEach { (k, v) ->
            try {
                val channel = SocketChannel.open(InetSocketAddress(v.address, v.port))
                rcons[k] = Rcon.newBuilder()
                    .withChannel(channel)
                    .withCharset(Charsets.UTF_8)
                    .build()
            } catch (t: Throwable) {
                ex.addSuppressed(t)
            }
        }
        for ((name, rcon) in rcons) {
            try {
                rcon.tryAuthenticate(configuration.servers[name]!!.password)
            } catch (e: Throwable) {
                ex.addSuppressed(e)
            }
        }

        for ((s, l) in configuration.targets) {
            groupedRcons[s] = l.servers.mapNotNull { rcons[it] }
        }
        for ((serverName, rcon) in rcons) {
            val c = configuration.servers[serverName]
            if (c == null){
                ex.addSuppressed(IllegalArgumentException("$serverName not found in configuration $configuration"))
                continue
            }
            rconCommands[rcon] = RconCommand(
                c.addWhitelistCommand,
                c.removeWhitelistCommand,
                Pattern.compile(c.addWhitelistResultPattern),
                Pattern.compile(c.removeWhitelistResultPattern)
            )
        }
        if (ex.suppressed.isNotEmpty()) {
            throw ex
        }
    }

    override fun add(target: String, player: String) {
        val ex = RuntimeException("Failed to add player.")
        for (rcon in (groupedRcons[target] ?: throw IllegalArgumentException("Target $target not found"))) {
            val command = rconCommands[rcon] ?: throw IllegalArgumentException("Command configuration for $rcon not found")
            val result = rcon.sendCommand(command.addWhitelistCommand.replace("%playerName%", player))
            if (!command.addWhitelistResultPattern.matcher(result).matches()){
                ex.addSuppressed(RemoteException(result))
            }
        }
        if (ex.suppressed.isNotEmpty()){
            throw ex
        }
    }

    override fun remove(target: String, player: String) {
        val ex = RuntimeException("Failed to remove player.")
        for (rcon in (groupedRcons[target] ?: throw IllegalArgumentException("Target $target not found"))) {
            val command = rconCommands[rcon] ?: throw IllegalArgumentException("Command configuration for $rcon not found")
            val result = rcon.sendCommand(command.removeWhitelistCommand.replace("%playerName%", player))
            if (!command.removeWhitelistResultPattern.matcher(result).matches()){
                ex.addSuppressed(RemoteException(result))
            }
        }
        if (ex.suppressed.isNotEmpty()){
            throw ex
        }
    }


    override fun listAvailable(): Map<String, String> {
        return configuration.targets.mapValues { it.value.description }
    }

    private data class RconCommand(
        val addWhitelistCommand: String,
        val removeWhitelistCommand: String,
        val addWhitelistResultPattern: Pattern,
        val removeWhitelistResultPattern: Pattern
    )
}