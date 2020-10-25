package damayanti.evi.covidinfo.model


data class DataResponse (
    val Global:DataGlobal,
    val Countries: MutableList<DataCountries>,
    val Date: String
)