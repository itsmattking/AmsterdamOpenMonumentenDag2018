package nl.amsterdam.openmonumentendag.monuments.presenter

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import nl.amsterdam.openmonumentendag.monuments.repository.SavedMonumentDataRepository

class SavedMonumentsPresenter(val view: SavedMonumentsContract.View,
                              val monumentsMonumentDataRepository: SavedMonumentDataRepository) : SavedMonumentsContract.Presenter {

    val disposable = CompositeDisposable()

    override fun getSavedMonuments() {
        view.onStartLoading()
        disposable.add(monumentsMonumentDataRepository.getList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { l ->
                    view.onSavedMonumentsLoaded(l)
                    view.onEndLoading()
                })
    }

    override fun removeSavedMonument(id: Int) {
        // todo
    }

    override fun attach() {
        getSavedMonuments()
    }

    override fun detach() {
        disposable.clear()
    }

}