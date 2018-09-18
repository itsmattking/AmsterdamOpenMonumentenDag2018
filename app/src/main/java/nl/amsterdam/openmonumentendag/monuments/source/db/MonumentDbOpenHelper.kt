package nl.amsterdam.openmonumentendag.monuments.source.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class MonumentDbOpenHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        const val DB_NAME = "monuments.db"
        const val DB_VERSION = 10

        const val MONUMENTS_CREATE_SQL = "CREATE TABLE ${DbDataSourceContract.Monument.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${DbDataSourceContract.Monument.COLUMN_TITLE} TEXT," +
            "${DbDataSourceContract.Monument.COLUMN_ADDRESS} TEXT," +
            "${DbDataSourceContract.Monument.COLUMN_TIMES} TEXT," +
            "${DbDataSourceContract.Monument.COLUMN_ENTRY} TEXT," +
            "${DbDataSourceContract.Monument.COLUMN_ACCESSIBLE} INTEGER," +
            "${DbDataSourceContract.Monument.COLUMN_REFRESHMENTS} INTEGER," +
            "${DbDataSourceContract.Monument.COLUMN_YEAR} TEXT," +
            "${DbDataSourceContract.Monument.COLUMN_ARCHITECT} TEXT," +
            "${DbDataSourceContract.Monument.COLUMN_DESCRIPTION} TEXT," +
            "${DbDataSourceContract.Monument.COLUMN_FACT} TEXT," +
            "${DbDataSourceContract.Monument.COLUMN_SIGHTS} TEXT," +
            "${DbDataSourceContract.Monument.COLUMN_ACTIVITIES} TEXT," +
            "${DbDataSourceContract.Monument.COLUMN_EVENT} TEXT," +
            "${DbDataSourceContract.Monument.COLUMN_LINK} TEXT," +
            "${DbDataSourceContract.Monument.COLUMN_PHOTOS} TEXT," +
            "${DbDataSourceContract.Monument.COLUMN_LOCATION_LATITUDE} TEXT," +
            "${DbDataSourceContract.Monument.COLUMN_LOCATION_LONGITUDE} TEXT," +
            "${DbDataSourceContract.Monument.COLUMN_FETCHED_TIMESTAMP} INTEGER" +
            ")"

        const val MONUMENTS_DELETE_SQL = "DROP TABLE IF EXISTS ${DbDataSourceContract.Monument.TABLE_NAME}"

        const val SAVED_MONUMENTS_CREATE_SQL = "CREATE TABLE ${DbDataSourceContract.SavedMonument.TABLE_NAME} (" +
                "${DbDataSourceContract.SavedMonument.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${DbDataSourceContract.SavedMonument.COLUMN_MONUMENT_ID} INTEGER" +
                ")"

        const val SAVED_MONUMENTS_DELETE_SQL = "DROP TABLE IF EXISTS ${DbDataSourceContract.SavedMonument.TABLE_NAME}"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(MONUMENTS_CREATE_SQL)
        db?.execSQL(SAVED_MONUMENTS_CREATE_SQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(MONUMENTS_DELETE_SQL)
        db?.execSQL(MONUMENTS_CREATE_SQL)
        if (newVersion == 7) {
            db?.execSQL(SAVED_MONUMENTS_CREATE_SQL)
        }
        if (newVersion == 8) {
            db?.execSQL(SAVED_MONUMENTS_DELETE_SQL)
            db?.execSQL(SAVED_MONUMENTS_CREATE_SQL)
        }
    }
}