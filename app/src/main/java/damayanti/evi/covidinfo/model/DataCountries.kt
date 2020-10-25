package damayanti.evi.covidinfo.model

data class DataCountries(
        val Country: String,
        val CountryCode: String,
        val Slug: String,
        val NewConfirmed: Int,
        val TotalConfirmed: Int,
        val NewDeaths: Int,
        val TotalDeaths: Int,
        val NewRecovered: Int,
        val TotalRecovered: Int,
        val Date: String
)



