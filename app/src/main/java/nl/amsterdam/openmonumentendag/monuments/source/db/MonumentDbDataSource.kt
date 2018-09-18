package nl.amsterdam.openmonumentendag.monuments.source.db

import android.content.ContentValues
import android.database.Cursor
import android.provider.BaseColumns
import nl.amsterdam.openmonumentendag.data.Monument
import nl.amsterdam.openmonumentendag.data.MonumentLocation
import nl.amsterdam.openmonumentendag.data.MonumentOpeningTime
import nl.amsterdam.openmonumentendag.data.MonumentPhoto
import nl.amsterdam.openmonumentendag.source.DataSource
import nl.amsterdam.openmonumentendag.source.DataSourceSearchQuery

class MonumentDbDataSource(val dbHelper: MonumentDbOpenHelper) : DataSource<Monument, Monument> {

    override fun getAll(): List<Monument> {

        val cursor = dbHelper.readableDatabase.rawQuery(
                "SELECT " + DbDataSourceContract.Monument.COLUMNS_ALL.map{ "${DbDataSourceContract.Monument.TABLE_NAME}.${it}"}.joinToString(",") + ", " +
                    "${DbDataSourceContract.SavedMonument.TABLE_NAME}.${DbDataSourceContract.SavedMonument.COLUMN_MONUMENT_ID} AS saved " +
                    "FROM ${DbDataSourceContract.Monument.TABLE_NAME} " +
                    "LEFT JOIN ${DbDataSourceContract.SavedMonument.TABLE_NAME} ON " +
                    "${DbDataSourceContract.Monument.TABLE_NAME}.${DbDataSourceContract.Monument.COLUMN_ID} = ${DbDataSourceContract.SavedMonument.TABLE_NAME}.${DbDataSourceContract.SavedMonument.COLUMN_MONUMENT_ID} ",
                null
        )

        return cursor.iterator { c ->
            Monument(
                c.getInt(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_ID)),
                c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_TITLE)),
                c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_ADDRESS)),
                c.getString(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_TIMES)).split("|").map { MonumentOpeningTime(it.split(",")[0], it.split(",")[1], it.split(",")[2]) },
                c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_ENTRY)),
                c.getInt(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_ACCESSIBLE)) == 1,
                c.getInt(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_REFRESHMENTS)) == 1,
                c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_YEAR)),
                c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_ARCHITECT)),
                c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_DESCRIPTION)),
                c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_FACT)),
                c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_SIGHTS)),
                c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_ACTIVITIES)).split("|").filter { it.isNotBlank() },
                c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_EVENT)),
                c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_LINK)),
                c.getString(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_PHOTOS)).split("|").map { MonumentPhoto(it.split(",")[0], it.split(",")[1]) },
                MonumentLocation(
                    cursor.getDouble(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_LOCATION_LATITUDE)),
                    cursor.getDouble(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_LOCATION_LONGITUDE))
                ),
                c.getInt(c.getColumnIndex("saved")) != 0,
                c.getLong(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_FETCHED_TIMESTAMP))
            )
        }.also { cursor.close() }
    }

    override fun getOne(id: Int): Monument {
        val cursor = dbHelper.readableDatabase.rawQuery(
                "SELECT " + DbDataSourceContract.Monument.COLUMNS_ALL.map{ "${DbDataSourceContract.Monument.TABLE_NAME}.${it}"}.joinToString(",") + ", " +
                        "${DbDataSourceContract.SavedMonument.TABLE_NAME}.${DbDataSourceContract.SavedMonument.COLUMN_MONUMENT_ID} AS saved " +
                        "FROM ${DbDataSourceContract.Monument.TABLE_NAME} " +
                        "LEFT JOIN ${DbDataSourceContract.SavedMonument.TABLE_NAME} ON " +
                        "${DbDataSourceContract.Monument.TABLE_NAME}.${DbDataSourceContract.Monument.COLUMN_ID} = ${DbDataSourceContract.SavedMonument.TABLE_NAME}.${DbDataSourceContract.SavedMonument.COLUMN_MONUMENT_ID} " +
                        "WHERE ${DbDataSourceContract.Monument.TABLE_NAME}.${DbDataSourceContract.Monument.COLUMN_ID} = ?",
                arrayOf(id.toString())
        )

        return if (cursor.count == 0) {
            Monument.EMPTY
        } else {
            cursor.moveToFirst()
            Monument(
                cursor.getInt(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_ADDRESS)),
                cursor.getString(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_TIMES)).split("|").map { MonumentOpeningTime(it.split(",")[0], it.split(",")[1], it.split(",")[2]) },
                cursor.getString(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_ENTRY)),
                cursor.getInt(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_ACCESSIBLE)) == 1,
                cursor.getInt(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_REFRESHMENTS)) == 1,
                cursor.getString(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_YEAR)),
                cursor.getString(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_ARCHITECT)),
                cursor.getString(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_FACT)),
                cursor.getString(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_SIGHTS)),
                cursor.getString(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_ACTIVITIES)).split("|").filter{ it.isNotBlank() },
                cursor.getString(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_EVENT)),
                cursor.getString(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_LINK)),
                cursor.getString(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_PHOTOS)).split("|").map { MonumentPhoto(it.split(",")[0], it.split(",")[1]) },
                MonumentLocation(
                    cursor.getDouble(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_LOCATION_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_LOCATION_LONGITUDE))
                ),
                cursor.getInt(cursor.getColumnIndex("saved")) != 0,
                cursor.getLong(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_FETCHED_TIMESTAMP))
            )
        }.also { cursor.close() }
    }

    override fun insertOne(item: Monument): Boolean {
        val result = dbHelper.writableDatabase.replace(
                DbDataSourceContract.Monument.TABLE_NAME,
            null,
            ContentValues().apply {
                put(BaseColumns._ID, item.id)
                put(DbDataSourceContract.Monument.COLUMN_TITLE, item.title)
                put(DbDataSourceContract.Monument.COLUMN_ADDRESS, item.address)
                put(DbDataSourceContract.Monument.COLUMN_TIMES, item.times.map {arrayOf(it.date, it.open, it.close).joinToString(",")}.joinToString("|"))
                put(DbDataSourceContract.Monument.COLUMN_ENTRY, item.entry)
                put(DbDataSourceContract.Monument.COLUMN_ACCESSIBLE, item.accessible)
                put(DbDataSourceContract.Monument.COLUMN_REFRESHMENTS, item.refreshments)
                put(DbDataSourceContract.Monument.COLUMN_YEAR, item.year)
                put(DbDataSourceContract.Monument.COLUMN_ARCHITECT, item.architect)
                put(DbDataSourceContract.Monument.COLUMN_DESCRIPTION, item.description)
                put(DbDataSourceContract.Monument.COLUMN_FACT, item.fact)
                put(DbDataSourceContract.Monument.COLUMN_SIGHTS, item.sights)
                put(DbDataSourceContract.Monument.COLUMN_ACTIVITIES, item.activityInfos.joinToString("|"))
                put(DbDataSourceContract.Monument.COLUMN_EVENT, item.event)
                put(DbDataSourceContract.Monument.COLUMN_LINK, item.link)
                put(DbDataSourceContract.Monument.COLUMN_PHOTOS, item.photos.map{ arrayOf(it.main, it.thumb).joinToString(",")}.joinToString("|"))
                put(DbDataSourceContract.Monument.COLUMN_LOCATION_LATITUDE, item.location.latitude)
                put(DbDataSourceContract.Monument.COLUMN_LOCATION_LONGITUDE, item.location.longitude)
                put(DbDataSourceContract.Monument.COLUMN_FETCHED_TIMESTAMP, item.fetchedTimestamp)
            }
        )

        return result != -1L
    }

    // We use the replace method here to allow for updates
    // from the server without removing the data first.
    // This relies on IDs provided from the server to be stable.
    override fun insertMany(items: List<Monument>): Boolean {
        val db = dbHelper.writableDatabase

        db.beginTransaction()

        items.forEach { item ->
            db.replace(
                DbDataSourceContract.Monument.TABLE_NAME,
                null,
                ContentValues().apply {
                    put(BaseColumns._ID, item.id)
                    put(DbDataSourceContract.Monument.COLUMN_TITLE, item.title)
                    put(DbDataSourceContract.Monument.COLUMN_ADDRESS, item.address)
                    put(DbDataSourceContract.Monument.COLUMN_TIMES, item.times.map {
                        arrayOf(it.date, it.open, it.close).joinToString(",")
                    }.joinToString("|"))
                    put(DbDataSourceContract.Monument.COLUMN_ENTRY, item.entry)
                    put(DbDataSourceContract.Monument.COLUMN_ACCESSIBLE, item.accessible)
                    put(DbDataSourceContract.Monument.COLUMN_REFRESHMENTS, item.refreshments)
                    put(DbDataSourceContract.Monument.COLUMN_YEAR, item.year)
                    put(DbDataSourceContract.Monument.COLUMN_ARCHITECT, item.architect)
                    put(DbDataSourceContract.Monument.COLUMN_DESCRIPTION, item.description)
                    put(DbDataSourceContract.Monument.COLUMN_FACT, item.fact)
                    put(DbDataSourceContract.Monument.COLUMN_SIGHTS, item.sights)
                    put(DbDataSourceContract.Monument.COLUMN_ACTIVITIES, item.activityInfos.joinToString("|"))
                    put(DbDataSourceContract.Monument.COLUMN_EVENT, item.event)
                    put(DbDataSourceContract.Monument.COLUMN_LINK, item.link)
                    put(DbDataSourceContract.Monument.COLUMN_PHOTOS, item.photos.map {
                        arrayOf(it.main, it.thumb).joinToString(",")
                    }.joinToString("|"))
                    put(DbDataSourceContract.Monument.COLUMN_LOCATION_LATITUDE, item.location.latitude)
                    put(DbDataSourceContract.Monument.COLUMN_LOCATION_LONGITUDE, item.location.longitude)
                    put(DbDataSourceContract.Monument.COLUMN_FETCHED_TIMESTAMP, item.fetchedTimestamp)
                }
            )
        }
        db.setTransactionSuccessful()
        db.endTransaction()
        return true
    }

    override fun searchQuery(searchQuery: DataSourceSearchQuery): List<Monument> {
        val fuzzyString = "%" + searchQuery.searchString.replace(" ", "%") + "%"
        val cursor = dbHelper.readableDatabase.query(
            DbDataSourceContract.Monument.TABLE_NAME,
            DbDataSourceContract.Monument.COLUMNS_ALL,
            DbDataSourceContract.Monument.COLUMN_TITLE + " like ? OR " + DbDataSourceContract.Monument.COLUMN_ADDRESS + " like ?",
            arrayOf(fuzzyString, fuzzyString),
            null,
            null,
            DbDataSourceContract.Monument.COLUMN_ID + " asc"
        )
        return if (cursor.count == 0) {
            emptyList()
        } else {
            cursor.iterator {c ->
                Monument(
                    c.getInt(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_ID)),
                    c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_TITLE)),
                    c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_ADDRESS)),
                    c.getString(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_TIMES)).split("|").map { MonumentOpeningTime(it.split(",")[0], it.split(",")[1], it.split(",")[2]) },
                    c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_ENTRY)),
                    c.getInt(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_ACCESSIBLE)) == 1,
                    c.getInt(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_REFRESHMENTS)) == 1,
                    c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_YEAR)),
                    c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_ARCHITECT)),
                    c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_DESCRIPTION)),
                    c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_FACT)),
                    c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_SIGHTS)),
                    c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_ACTIVITIES)).split("|").filter { it.isNotBlank() },
                    c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_EVENT)),
                    c.getString(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_LINK)),
                    c.getString(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_PHOTOS)).split("|").map { MonumentPhoto(it.split(",")[0], it.split(",")[1]) },
                    MonumentLocation(
                            cursor.getDouble(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_LOCATION_LATITUDE)),
                            cursor.getDouble(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_LOCATION_LONGITUDE))
                    ),
                    false,
                    c.getLong(c.getColumnIndex(DbDataSourceContract.Monument.COLUMN_FETCHED_TIMESTAMP))
                )
            }.also { cursor.close() }
        }
    }
}

inline fun <reified T> Cursor.iterator(caller: (cursor: Cursor) -> T): List<T> {
    if (count == 0) {
        return emptyList()
    }
    return ArrayList<T>(count).apply {
        moveToFirst()
        do {
            add(caller.invoke(this@iterator))
            moveToNext()
        } while (!isAfterLast)
    }
}