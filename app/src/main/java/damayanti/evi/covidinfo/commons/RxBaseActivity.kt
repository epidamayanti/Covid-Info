package damayanti.evi.covidinfo.commons

import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable

open class RxBaseActivity : AppCompatActivity() {

    protected var subscriptions = CompositeDisposable()

    override fun onResume() {
        super.onResume()
        subscriptions = CompositeDisposable()

    }

    override fun onPause() {
        super.onPause()
        subscriptions.clear()
    }
}