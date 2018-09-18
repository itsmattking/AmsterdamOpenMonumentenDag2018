package nl.amsterdam.openmonumentendag.source

interface DataSource<in S, out T> {
    fun getAll(): List<T>
    fun getOne(id: Int = -1): T
    fun insertOne(item: S): Boolean
    fun insertMany(items: List<S>): Boolean
    fun searchQuery(searchQuery: DataSourceSearchQuery): List<T>
}