package damayanti.evi.covidinfo.service

import damayanti.evi.covidinfo.commons.Utils
import damayanti.evi.covidinfo.model.DataResponse
import io.reactivex.Observable
import retrofit2.http.GET

interface ServiceGetData {
    @GET(Utils.SUMMARY)
    fun getData(): Observable<DataResponse>
}