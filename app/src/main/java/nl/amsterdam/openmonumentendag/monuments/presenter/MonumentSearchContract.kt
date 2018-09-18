package nl.amsterdam.openmonumentendag.monuments.presenter

import nl.amsterdam.openmonumentendag.data.Monument
import nl.amsterdam.openmonumentendag.source.DataSourceSearchQuery

interface MonumentSearchContract {
    interface Presenter {
        fun searchMonuments(monumentSearchQuery: DataSourceSearchQuery)
        fun attach()
        fun detach()
    }

    interface View {
        fun onMonumentsSearchResults(monuments: List<Monument>)
        fun onMonumentsSearchStart()
        fun onMonumentsSearchEnd()
    }
}