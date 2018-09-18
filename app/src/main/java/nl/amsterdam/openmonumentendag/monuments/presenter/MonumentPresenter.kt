package nl.amsterdam.openmonumentendag.monuments.presenter

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import nl.amsterdam.openmonumentendag.monuments.repository.MonumentDataRepository
import nl.amsterdam.openmonumentendag.monuments.repository.SavedMonumentDataRepository

class MonumentPresenter(val view: MonumentContract.View,
                        val monumentDataRepository: MonumentDataRepository,
                        val savedMonumentDataRepository: SavedMonumentDataRepository) : MonumentContract.Presenter {

    val disposable = CompositeDisposable()

    override fun getMonument(id: Int) {
        view.onStartLoading()
        disposable.add(monumentDataRepository.getSingle(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { m ->
                    view.onMonumentLoaded(m)
                    view.onEndLoading()
                })
    }

    override fun saveMonument(id: Int) {
        disposable.add(savedMonumentDataRepository.insertOne(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { saved ->
                    if (saved) {
                        view.onMonumentSaved(id)
                    } else {
                        view.onMonumentUnsaved(id)
                    }
                })
    }

    override fun attach() {

    }

    override fun detach() {
        disposable.clear()
    }
}