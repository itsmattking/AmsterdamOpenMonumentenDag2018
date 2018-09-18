package nl.amsterdam.openmonumentendag.monuments.presenter

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import nl.amsterdam.openmonumentendag.monuments.repository.MonumentDataRepository
import nl.amsterdam.openmonumentendag.monuments.repository.SavedMonumentDataRepository

class MonumentsPresenter(val view: MonumentsContract.View,
                         val monumentDataRepository: MonumentDataRepository,
                         val savedMonumentDataRepository: SavedMonumentDataRepository) : MonumentsContract.Presenter {

    val monumentSaveActionPublisher: PublishSubject<MonumentSave> = PublishSubject.create()

    init {
        monumentSaveActionPublisher.doOnNext({ monumentSave ->
            if (monumentSave.saved) view.onMonumentSaved(monumentSave.id)
            else view.onMonumentUnsaved(monumentSave.id)
        })
    }

    val disposable = CompositeDisposable()

    override fun getMonuments() {
        view.onStartLoading()
        disposable.add(monumentDataRepository.getList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { l ->
                    view.onMonumentsLoaded(l)
                    view.onEndLoading()
                })
    }

    override fun saveMonument(id: Int) {
        disposable.add(savedMonumentDataRepository.insertOne(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { saved ->
                    monumentSaveActionPublisher.onNext(MonumentSave(id, saved))
                }
        )
    }

    override fun attach() {
        getMonuments()
    }

    override fun detach() {
        disposable.clear()
    }

    data class MonumentSave(val id: Int, val saved: Boolean)
}