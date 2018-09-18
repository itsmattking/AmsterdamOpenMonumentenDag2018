package nl.amsterdam.openmonumentendag.monuments.presenter

import nl.amsterdam.openmonumentendag.data.Monument

interface MonumentContract {
    interface Presenter {
        fun getMonument(id: Int)
        fun saveMonument(id: Int)
        fun attach()
        fun detach()
    }
    interface View {
        fun onMonumentLoaded(monument: Monument)
        fun onMonumentSaved(id: Int)
        fun onMonumentUnsaved(id: Int)
        fun onStartLoading()
        fun onEndLoading()
    }
}