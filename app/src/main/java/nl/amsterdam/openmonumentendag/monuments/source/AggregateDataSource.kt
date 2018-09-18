package nl.amsterdam.openmonumentendag.monuments.source

import nl.amsterdam.openmonumentendag.BuildConfig
import nl.amsterdam.openmonumentendag.data.Monument
import nl.amsterdam.openmonumentendag.monuments.source.api.MonumentApiDataSource
import nl.amsterdam.openmonumentendag.monuments.source.db.MonumentDbDataSource
import nl.amsterdam.openmonumentendag.source.DataSource
import nl.amsterdam.openmonumentendag.source.DataSourceSearchQuery

class AggregateDataSource(val apiDataSource: MonumentApiDataSource,
                          val dbDataSource: MonumentDbDataSource) : DataSource<Any, Monument> {

    companion object {
        // 3 hour stale time to fetch and update data from the server
        const val STALE_DATA_LIMIT_IN_MILLIS = 3L * 3600L * 1000L
        // 30 second stale time for development
        const val STALE_DATA_LIMIT_IN_MILLIS_DEBUG = 30L * 1000L
    }

    override fun getAll(): List<Monument> {
        return dbDataSource.getAll().let { dbData ->
            if (dbData.isEmpty() || hasStaleLocalData(dbData[0].fetchedTimestamp)) {
            apiDataSource.getAll().let { apiData ->
                    if (dbDataSource.insertMany(apiData)) dbDataSource.getAll()
                    else dbData
                }
            } else {
            dbData
            }
        }
    }

    override fun getOne(id: Int): Monument {
        return dbDataSource.getOne(id)
    }

    override fun insertOne(item: Any): Boolean {
        // noop
        return true
    }

    override fun insertMany(items: List<Any>): Boolean {
        // noop
        return true
    }

    override fun searchQuery(searchQuery: DataSourceSearchQuery): List<Monument> {
        return emptyList()
    }

    private fun hasStaleLocalData(lastTimestamp: Long): Boolean {
        return System.currentTimeMillis() - lastTimestamp > getStaleDataLimit();
    }

    private fun getStaleDataLimit(): Long {
        return if (BuildConfig.DEBUG) {
            STALE_DATA_LIMIT_IN_MILLIS_DEBUG
        } else {
            STALE_DATA_LIMIT_IN_MILLIS
        }
    }
}