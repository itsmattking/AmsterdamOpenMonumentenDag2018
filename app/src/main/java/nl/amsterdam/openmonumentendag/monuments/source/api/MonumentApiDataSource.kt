package nl.amsterdam.openmonumentendag.monuments.source.api

import nl.amsterdam.openmonumentendag.data.Monument
import nl.amsterdam.openmonumentendag.source.DataSource
import nl.amsterdam.openmonumentendag.source.DataSourceSearchQuery

class MonumentApiDataSource(val apiService: MonumentsApiService) : DataSource<Any, Monument> {
    override fun getAll(): List<Monument> {
        return apiService.getMonuments()
    }

    override fun getOne(id: Int): Monument {
        return Monument.EMPTY
    }

    override fun insertOne(item: Any): Boolean {
        // No inserting on this API - noop
        return true
    }

    override fun insertMany(items: List<Any>): Boolean {
        // No inserting on this API - noop
        return true
    }

    override fun searchQuery(searchQuery: DataSourceSearchQuery): List<Monument> {
        return emptyList()
    }
}