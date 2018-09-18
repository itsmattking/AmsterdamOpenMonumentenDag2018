package nl.amsterdam.openmonumentendag.monuments.repository

import io.reactivex.Observable
import io.reactivex.Single
import nl.amsterdam.openmonumentendag.data.Monument
import nl.amsterdam.openmonumentendag.monuments.source.db.SavedMonumentDataSource
import nl.amsterdam.openmonumentendag.repository.ObservableDataRepository
import nl.amsterdam.openmonumentendag.source.DataSourceSearchQuery

class SavedMonumentDataRepository(dataSource: SavedMonumentDataSource) : ObservableDataRepository<Int, Monument>(dataSource) {
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

    override fun insertOne(item: Int): Single<Boolean> {
        return Single.create { e ->
            e.onSuccess(dataSource.insertOne(item))
        }
    }

    override fun insertMany(items: List<Int>): Single<Boolean> {
        return Single.create { e ->
            e.onSuccess(dataSource.insertMany(items))
        }
    }

    override fun searchQuery(searchQuery: DataSourceSearchQuery): Observable<List<Monument>> {
        return Observable.empty()
    }
}