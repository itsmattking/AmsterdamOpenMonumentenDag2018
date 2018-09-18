package nl.amsterdam.openmonumentendag.monuments.presenter

import io.reactivex.disposables.CompositeDisposable
import nl.amsterdam.openmonumentendag.monuments.repository.MonumentSearchDataRepository
import nl.amsterdam.openmonumentendag.source.DataSourceSearchQuery

class MonumentSearchPresenter(val view: MonumentSearchContract.View,
                              val monumentDataRepository: MonumentSearchDataRepository)
    : MonumentSearchContract.Presenter {

    val disposable: CompositeDisposable = CompositeDisposable()

    override fun searchMonuments(monumentSearchQuery: DataSourceSearchQuery) {
        view.onMonumentsSearchStart()
        disposable.add(monumentDataRepository.searchQuery(monumentSearchQuery).subscribe {
            monumentList -> view.onMonumentsSearchResults(monumentList)
                            view.onMonumentsSearchEnd()
        })
    }

    override fun attach() {

    }

    override fun detach() {
        disposable.clear()
    }
}