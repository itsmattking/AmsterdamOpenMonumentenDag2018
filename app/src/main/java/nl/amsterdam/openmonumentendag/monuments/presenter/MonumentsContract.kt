package nl.amsterdam.openmonumentendag.monuments.presenter

import nl.amsterdam.openmonumentendag.data.Monument

interface MonumentsContract {
    interface Presenter {
        fun getMonuments()
        fun saveMonument(id: Int)
        fun attach()
        fun detach()
    }
    interface View {
        fun onMonumentsLoaded(monumentsList: List<Monument>)
        fun onMonumentSaved(id: Int)
        fun onMonumentUnsaved(id: Int)
        fun onStartLoading()
        fun onEndLoading()
    }
}