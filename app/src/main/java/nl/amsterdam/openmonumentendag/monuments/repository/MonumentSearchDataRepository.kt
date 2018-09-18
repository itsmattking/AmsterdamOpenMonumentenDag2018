package nl.amsterdam.openmonumentendag.monuments.repository

import io.reactivex.Observable
import io.reactivex.Single
import nl.amsterdam.openmonumentendag.data.Monument
import nl.amsterdam.openmonumentendag.repository.ObservableDataRepository
import nl.amsterdam.openmonumentendag.source.DataSource
import nl.amsterdam.openmonumentendag.source.DataSourceSearchQuery

class MonumentSearchDataRepository(dataSource: DataSource<Monument, Monument>) : ObservableDataRepository<Monument, Monument>(dataSource) {
    override fun getList(): Observable<List<Monument>> {
        return Observable.empty()
    }

    override fun getSingle(id: Int): Single<Monument> {
        return Single.just(Monument.EMPTY)
    }

    override fun insertOne(item: Monument): Single<Boolean> {
        return Single.just(false)
    }

    override fun insertMany(items: List<Monument>): Single<Boolean> {
        return Single.just(false)
    }

    override fun searchQuery(searchQuery: DataSourceSearchQuery): Observable<List<Monument>> {
        return Observable.create { e ->
            e.onNext(dataSource.searchQuery(searchQuery))
            e.onComplete()
        }
    }
}