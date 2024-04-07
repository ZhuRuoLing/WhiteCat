package icu.takeneko.whitecat.mirai.data

class RequestManager<T : Any, U : MutableList<T>>(
    private val dataProvider: () -> U,
    private val dataModifier: ((U) -> Unit) -> Unit,
    private val matcher: (String, T) -> Boolean
) {

    fun match(s:String): List<T>{
        return dataProvider().filter { matcher(s,it) }.also { if (it.isEmpty()) throw IllegalArgumentException("No matches for the given PlayerName $s") }
    }

    fun add(t:T) {
        dataModifier {
            it.add(t)
        }
    }

    fun remove(t:T){
        dataModifier{
            it.remove(t)
        }
    }

    fun removeMatching(s:String){
        match(s).forEach { remove(it) }
    }

    fun get() = dataProvider()
}