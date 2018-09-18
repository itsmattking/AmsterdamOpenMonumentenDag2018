package nl.amsterdam.openmonumentendag.repository

import nl.amsterdam.openmonumentendag.source.DataSource
import io.reactivex.Observable
import io.reactivex.Single
import nl.amsterdam.openmonumentendag.source.DataSourceSearchQuery

abstract class ObservableDataRepository<in S, T>(val dataSource: DataSource<S, T>) {
    abstract fun getList(): Observable<List<T>>
    abstract fun getSingle(id: Int): Single<T>
    abstract fun insertOne(item: S): Single<Boolean>
    abstract fun insertMany(items: List<S>): Single<Boolean>
    abstract fun searchQuery(searchQuery: DataSourceSearchQuery): Observable<List<T>>
}