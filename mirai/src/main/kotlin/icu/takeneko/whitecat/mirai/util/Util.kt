package icu.takeneko.whitecat.mirai.util

import kotlinx.serialization.json.Json

internal val json = Json {
    prettyPrint = true
    encodeDefaults = true
    ignoreUnknownKeys = true
}