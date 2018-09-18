package nl.amsterdam.openmonumentendag.monuments.source.db

import android.provider.BaseColumns

object DbDataSourceContract {
    object Monument {
        const val TABLE_NAME = "monuments"
        const val COLUMN_ID = BaseColumns._ID
        const val COLUMN_TITLE = "title"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_TIMES = "times"
        const val COLUMN_ENTRY = "entry"
        const val COLUMN_ACCESSIBLE = "accessible"
        const val COLUMN_REFRESHMENTS = "refreshments"
        const val COLUMN_YEAR = "year"
        const val COLUMN_ARCHITECT = "architect"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_FACT = "fact"
        const val COLUMN_SIGHTS = "sights"
        const val COLUMN_ACTIVITIES = "activities"
        const val COLUMN_EVENT = "event"
        const val COLUMN_LINK = "link"
        const val COLUMN_PHOTOS = "photos"
        const val COLUMN_LOCATION_LATITUDE = "location_latitude"
        const val COLUMN_LOCATION_LONGITUDE = "location_longitude"
        const val COLUMN_FETCHED_TIMESTAMP = "fetched_timestamp"

        val COLUMNS_ALL = arrayOf(
            COLUMN_ID,
            COLUMN_TITLE,
            COLUMN_ADDRESS,
            COLUMN_TIMES,
            COLUMN_ENTRY,
            COLUMN_ACCESSIBLE,
            COLUMN_REFRESHMENTS,
            COLUMN_YEAR,
            COLUMN_ARCHITECT,
            COLUMN_DESCRIPTION,
            COLUMN_FACT,
            COLUMN_SIGHTS,
            COLUMN_ACTIVITIES,
            COLUMN_EVENT,
            COLUMN_LINK,
            COLUMN_PHOTOS,
            COLUMN_LOCATION_LATITUDE,
            COLUMN_LOCATION_LONGITUDE,
            COLUMN_FETCHED_TIMESTAMP
        )
    }

    object SavedMonument {
        const val TABLE_NAME = "saved_monuments"
        const val COLUMN_ID = BaseColumns._ID
        const val COLUMN_MONUMENT_ID = "monument_id"

        val COLUMNS_ALL = arrayOf(
            COLUMN_ID,
            COLUMN_MONUMENT_ID
        )
    }
}