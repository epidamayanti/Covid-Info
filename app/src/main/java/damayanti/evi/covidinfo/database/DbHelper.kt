package damayanti.evi.covidinfo.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import damayanti.evi.covidinfo.model.DataCountries
import damayanti.evi.covidinfo.model.DataGlobal

class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES_GLOBAL)
        db.execSQL(SQL_CREATE_ENTRIES_COUNTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES_GLOBAL)
        db.execSQL(SQL_DELETE_ENTRIES_COUNTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    @Throws(SQLiteConstraintException::class)
    fun insertGlobal(global: DataGlobal, date :String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.UserEntry.COLUMN_GLOBAL_NEWCONFIRMED, global.NewConfirmed)
        values.put(DBContract.UserEntry.COLUMN_GLOBAL_TOTALCONFIRMED, global.TotalConfirmed)
        values.put(DBContract.UserEntry.COLUMN_GLOBAL_NEWDEATHS, global.NewDeaths)
        values.put(DBContract.UserEntry.COLUMN_GLOBAL_TOTALDEATHS, global.TotalDeaths)
        values.put(DBContract.UserEntry.COLUMN_GLOBAL_NEWRECOVERED, global.NewRecovered)
        values.put(DBContract.UserEntry.COLUMN_GLOBAL_TOTALRECOVERED, global.TotalRecovered)
        values.put(DBContract.UserEntry.COLUMN_GLOBAL_DATE, date.substring(0,10))

        // Insert the new row, returning the primary key value of the new row
        db.insert(DBContract.UserEntry.TABLE_GLOBAL, null, values)
        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun insertCountries(country: MutableList<DataCountries>): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        for(countries in country) {
            val values = ContentValues()
            values.put(DBContract.UserEntry.COLUMN_COUNTRIES_COUNTRY, countries.Country)
            values.put(DBContract.UserEntry.COLUMN_COUNTRIES_COUNTRYCODE, countries.CountryCode)
            values.put(DBContract.UserEntry.COLUMN_COUNTRIES_SLUG, countries.Slug)
            values.put(DBContract.UserEntry.COLUMN_COUNTRIES_NEWCONFIRMED, countries.NewConfirmed)
            values.put(DBContract.UserEntry.COLUMN_COUNTRIES_TOTALCONFIRMED, countries.TotalConfirmed)
            values.put(DBContract.UserEntry.COLUMN_COUNTRIES_NEWDEATHS, countries.NewDeaths)
            values.put(DBContract.UserEntry.COLUMN_COUNTRIES_TOTALDEATHS, countries.TotalDeaths)
            values.put(DBContract.UserEntry.COLUMN_COUNTRIES_NEWRECOVERED, countries.NewRecovered)
            values.put(DBContract.UserEntry.COLUMN_COUNTRIES_TOTALRECOVERED, countries.TotalRecovered)

            db.insert(DBContract.UserEntry.TABLE_COUNTRIES, null, values)
        }

        return true
    }

    fun getCasesCountries(country: String): String? {
        val db = writableDatabase
        var cursor: Cursor? = null
        var cases: String? = ""
        return try {
            cursor = db.rawQuery("SELECT * FROM " + DBContract.UserEntry.TABLE_COUNTRIES + " WHERE " + DBContract.UserEntry.COLUMN_COUNTRIES_COUNTRY + " LIKE '" + country + "'", null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                cases = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_COUNTRIES_TOTALCONFIRMED))
            }
            cases
        } finally {
            cursor!!.close()
        }
    }

    fun getTotalCases(): String? {
        val db = writableDatabase
        var cursor: Cursor? = null
        var cases: String? = ""
        return try {
            cursor = db.rawQuery("SELECT * FROM " + DBContract.UserEntry.TABLE_GLOBAL, null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                cases = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_GLOBAL_TOTALCONFIRMED))
            }
            cases
        } finally {
            cursor!!.close()
        }
    }

    fun getDeathCountries(country: String): String? {
        val db = writableDatabase
        var cursor: Cursor? = null
        var cases: String? = ""
        return try {
            cursor = db.rawQuery("SELECT * FROM " + DBContract.UserEntry.TABLE_COUNTRIES + " WHERE " + DBContract.UserEntry.COLUMN_COUNTRIES_COUNTRY + " LIKE '" + country + "'", null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                cases = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_COUNTRIES_TOTALDEATHS))
            }
            cases
        } finally {
            cursor!!.close()
        }
    }

    fun getTotalDeath(): String? {
        val db = writableDatabase
        var cursor: Cursor? = null
        var cases: String? = ""
        return try {
            cursor = db.rawQuery("SELECT * FROM " + DBContract.UserEntry.TABLE_GLOBAL, null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                cases = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_GLOBAL_TOTALDEATHS))
            }
            cases
        } finally {
            cursor!!.close()
        }
    }

    fun getListCountry(country: String): MutableList<String> {
        val listCountry = mutableListOf<String>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.UserEntry.TABLE_COUNTRIES+ " WHERE " + DBContract.UserEntry.COLUMN_COUNTRIES_COUNTRY + " LIKE '" + country + "'" +" order by "+DBContract.UserEntry.COLUMN_COUNTRIES_COUNTRY +" asc", null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES_COUNTRIES)
            return ArrayList()
        }

        var data: String
        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                data = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_COUNTRIES_COUNTRY))

                listCountry.add(data)
                cursor.moveToNext()
            }
        }
        return listCountry
    }

    fun readDate(): ArrayList<String> {
        val dates = ArrayList<String>()
        val db = writableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery("SELECT DATE FROM " +DBContract.UserEntry.TABLE_GLOBAL, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES_GLOBAL)
            return ArrayList()
        }

        var date: String
        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                date = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_GLOBAL_DATE))

                dates.add(date)
                cursor.moveToNext()
            }
        }
        return dates
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteAllData(): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Issue SQL statement.
        db.execSQL("DELETE FROM "+DBContract.UserEntry.TABLE_COUNTRIES)
        db.execSQL("DELETE FROM "+DBContract.UserEntry.TABLE_GLOBAL)

        return true
    }




    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "CovidInfo.db"

        private val SQL_CREATE_ENTRIES_GLOBAL =
            "CREATE TABLE " + DBContract.UserEntry.TABLE_GLOBAL + " (" +
                    DBContract.UserEntry.COLUMN_GLOBAL_NEWCONFIRMED + " INTEGER," +
                    DBContract.UserEntry.COLUMN_GLOBAL_TOTALCONFIRMED + " INTEGER," +
                    DBContract.UserEntry.COLUMN_GLOBAL_NEWDEATHS + " INTEGER," +
                    DBContract.UserEntry.COLUMN_GLOBAL_TOTALDEATHS + " INTEGER," +
                    DBContract.UserEntry.COLUMN_GLOBAL_NEWRECOVERED + " INTEGER," +
                    DBContract.UserEntry.COLUMN_GLOBAL_TOTALRECOVERED + " INTEGER," +
                    DBContract.UserEntry.COLUMN_GLOBAL_DATE + " TEXT )"

        private val SQL_CREATE_ENTRIES_COUNTRIES =
            "CREATE TABLE " + DBContract.UserEntry.TABLE_COUNTRIES + " (" +
                    DBContract.UserEntry.COLUMN_COUNTRIES_COUNTRY + " TEXT," +
                    DBContract.UserEntry.COLUMN_COUNTRIES_COUNTRYCODE + " TEXT," +
                    DBContract.UserEntry.COLUMN_COUNTRIES_SLUG + " TEXT," +
                    DBContract.UserEntry.COLUMN_COUNTRIES_NEWCONFIRMED + " INTEGER," +
                    DBContract.UserEntry.COLUMN_COUNTRIES_TOTALCONFIRMED + " INTEGER," +
                    DBContract.UserEntry.COLUMN_COUNTRIES_NEWDEATHS + " INTEGER," +
                    DBContract.UserEntry.COLUMN_COUNTRIES_TOTALDEATHS + " INTEGER," +
                    DBContract.UserEntry.COLUMN_COUNTRIES_NEWRECOVERED + " INTEGER," +
                    DBContract.UserEntry.COLUMN_COUNTRIES_TOTALRECOVERED + " TEXT )"

        private val SQL_DELETE_ENTRIES_GLOBAL = "DROP TABLE IF EXISTS " + DBContract.UserEntry.TABLE_GLOBAL
        private val SQL_DELETE_ENTRIES_COUNTRIES = "DROP TABLE IF EXISTS " + DBContract.UserEntry.TABLE_COUNTRIES
    }


}