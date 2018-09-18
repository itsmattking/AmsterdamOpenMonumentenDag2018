package nl.amsterdam.openmonumentendag.monuments.presenter

import nl.amsterdam.openmonumentendag.data.Monument

interface SavedMonumentsContract {
    interface Presenter {
        fun getSavedMonuments()
        fun removeSavedMonument(id: Int)
        fun attach()
        fun detach()
    }
    interface View {
        fun onSavedMonumentsLoaded(items: List<Monument>)
        fun onRemovedSavedMonument(id: Int)
        fun onStartLoading()
        fun onEndLoading()
    }
}