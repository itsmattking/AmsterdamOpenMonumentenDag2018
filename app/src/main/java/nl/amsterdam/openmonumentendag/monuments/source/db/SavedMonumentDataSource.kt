package nl.amsterdam.openmonumentendag.monuments.source.db

import android.content.ContentValues
import nl.amsterdam.openmonumentendag.data.Monument
import nl.amsterdam.openmonumentendag.data.MonumentLocation
import nl.amsterdam.openmonumentendag.data.MonumentOpeningTime
import nl.amsterdam.openmonumentendag.data.MonumentPhoto
import nl.amsterdam.openmonumentendag.source.DataSource
import nl.amsterdam.openmonumentendag.source.DataSourceSearchQuery

class SavedMonumentDataSource(val dbOpenHelper: MonumentDbOpenHelper) : DataSource<Int, Monument> {

    override fun getAll(): List<Monument> {
        val cursor = dbOpenHelper.readableDatabase.rawQuery(
        "SELECT " + DbDataSourceContract.Monument.COLUMNS_ALL.map{ "${DbDataSourceContract.Monument.TABLE_NAME}.${it}"}.joinToString(",") + ", " +
            "${DbDataSourceContract.SavedMonument.TABLE_NAME}.${DbDataSourceContract.SavedMonument.COLUMN_MONUMENT_ID} AS saved " +
            "FROM ${DbDataSourceContract.Monument.TABLE_NAME} " +
            "LEFT JOIN ${DbDataSourceContract.SavedMonument.TABLE_NAME} ON " +
            "${DbDataSourceContract.Monument.TABLE_NAME}.${DbDataSourceContract.Monument.COLUMN_ID} = ${DbDataSourceContract.SavedMonument.TABLE_NAME}.${DbDataSourceContract.SavedMonument.COLUMN_MONUMENT_ID} " +
            "WHERE saved IS NOT NULL",
null
        )
        val results = cursor.iterator { c ->
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
                    c.getInt(cursor.getColumnIndex("saved")) > 0,
                    c.getLong(cursor.getColumnIndex(DbDataSourceContract.Monument.COLUMN_FETCHED_TIMESTAMP))
            )

        }
        cursor.close()
        return results
    }

    override fun getOne(id: Int): Monument {
        // todo
        return Monument.EMPTY
    }

    override fun insertOne(item: Int): Boolean {
        val countCursor = dbOpenHelper.readableDatabase.query(
            DbDataSourceContract.SavedMonument.TABLE_NAME,
            arrayOf(DbDataSourceContract.SavedMonument.COLUMN_ID),
                "${DbDataSourceContract.SavedMonument.COLUMN_MONUMENT_ID} = ?",
                arrayOf(item.toString()),
                null,
                null,
                null,
                null
        )
        if (countCursor.count > 0) {
            dbOpenHelper.writableDatabase.delete(
                DbDataSourceContract.SavedMonument.TABLE_NAME,
                "${DbDataSourceContract.SavedMonument.COLUMN_MONUMENT_ID} = ?",
                arrayOf(item.toString())
            )
            countCursor.close()
            return false
        } else {
            dbOpenHelper.writableDatabase.insert(
                DbDataSourceContract.SavedMonument.TABLE_NAME,
                null,
                ContentValues().apply {
                    put(DbDataSourceContract.SavedMonument.COLUMN_MONUMENT_ID, item)
                }
            )
            countCursor.close()
            return true
        }
    }

    override fun insertMany(items: List<Int>): Boolean {
        return true
    }

    override fun searchQuery(searchQuery: DataSourceSearchQuery): List<Monument> {
        return emptyList()
    }
}