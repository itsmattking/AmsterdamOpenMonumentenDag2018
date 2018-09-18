package nl.amsterdam.openmonumentendag.source

data class DataSourceSearchQuery(val searchString: String,
                                 val limit: Int = 1000,
                                 val offset: Int = 0)
