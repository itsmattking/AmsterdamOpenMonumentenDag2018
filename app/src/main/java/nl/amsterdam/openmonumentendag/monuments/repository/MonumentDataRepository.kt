package nl.amsterdam.openmonumentendag.monuments.repository

import nl.amsterdam.openmonumentendag.data.Monument
import nl.amsterdam.openmonumentendag.repository.ObservableDataRepository
import nl.amsterdam.openmonumentendag.source.DataSource
import io.reactivex.Observable
import io.reactivex.Single
import nl.amsterdam.openmonumentendag.monuments.data.MonumentJsonData
import nl.amsterdam.openmonumentendag.source.DataSourceSearchQuery

class MonumentDataRepository(dataSource: DataSource<MonumentJsonData, Monument>) : ObservableDataRepository<MonumentJsonData, Monument>(dataSource) {
    override fun getList(): Observable<List<Monument>> {
        return Observable.create { e ->
            e.onNext(dataSource.getAll())
            e.onComplete()
        }
    }

    override fun getSingle(id: Int): Single<Monument> {
        return Single.create { e ->
            e.onSuccess(dataSource.getOne(id))
        }
    }


    override fun insertOne(item: MonumentJsonData): Single<Boolean> {
        return Single.create { e ->
            e.onSuccess(dataSource.insertOne(item))
        }
    }

    override fun insertMany(items: List<MonumentJsonData>): Single<Boolean> {
        return Single.create { e ->
            e.onSuccess(dataSource.insertMany(items))
        }
    }

    override fun searchQuery(searchQuery: DataSourceSearchQuery): Observable<List<Monument>> {
        return Observable.create { e ->
            e.onNext(dataSource.searchQuery(searchQuery))
            e.onComplete()
        }
    }
}