package damayanti.evi.covidinfo.database

import android.provider.BaseColumns

object DBContract {
    class UserEntry : BaseColumns {
        companion object {

            val TABLE_COUNTRIES = "countries"
            val COLUMN_COUNTRIES_COUNTRYCODE = "country_code"
            val COLUMN_COUNTRIES_COUNTRY = "country"
            val COLUMN_COUNTRIES_SLUG = "slug"
            val COLUMN_COUNTRIES_NEWCONFIRMED = "new_confirmed"
            val COLUMN_COUNTRIES_TOTALCONFIRMED = "total_confirmed"
            val COLUMN_COUNTRIES_NEWDEATHS = "new_deaths"
            val COLUMN_COUNTRIES_TOTALDEATHS = "total_deaths"
            val COLUMN_COUNTRIES_NEWRECOVERED = "new_recovered"
            val COLUMN_COUNTRIES_TOTALRECOVERED = "total_recovered"

            val TABLE_GLOBAL = "global"
            val COLUMN_GLOBAL_NEWCONFIRMED = "new_confirmed"
            val COLUMN_GLOBAL_TOTALCONFIRMED = "total_confirmed"
            val COLUMN_GLOBAL_NEWDEATHS = "new_deaths"
            val COLUMN_GLOBAL_TOTALDEATHS = "total_deaths"
            val COLUMN_GLOBAL_NEWRECOVERED = "new_recovered"
            val COLUMN_GLOBAL_TOTALRECOVERED = "total_recovered"
            val COLUMN_GLOBAL_DATE = "date"


        }
    }
}
