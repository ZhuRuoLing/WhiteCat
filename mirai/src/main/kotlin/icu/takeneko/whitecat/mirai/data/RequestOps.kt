package icu.takeneko.whitecat.mirai.data

enum class RequestOps {
    ADD{
        override fun describe() = "add"

    }, REMOVE{
        override fun describe() = "remove"
    };

    abstract fun describe():String
}