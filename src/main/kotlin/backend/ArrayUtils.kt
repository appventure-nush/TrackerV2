package backend

fun <T> ArrayList<T>.clearAndAddAll(c: Collection<T>) {
    clear()
    addAll(c)
}

fun <T> MutableList<T>.clearAndAddAll(c: Collection<T>) {
    clear()
    addAll(c)
}