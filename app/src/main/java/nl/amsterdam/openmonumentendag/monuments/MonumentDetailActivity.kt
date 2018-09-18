package nl.amsterdam.openmonumentendag.monuments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_monument_detail.*
import kotlinx.android.synthetic.main.content_monument_detail.*
import nl.amsterdam.openmonumentendag.OpenMonumentenDagApplication
import nl.amsterdam.openmonumentendag.R
import nl.amsterdam.openmonumentendag.data.Monument
import nl.amsterdam.openmonumentendag.loadWithBaseUrl
import nl.amsterdam.openmonumentendag.monuments.presenter.MonumentContract
import nl.amsterdam.openmonumentendag.monuments.presenter.MonumentPresenter
import nl.amsterdam.openmonumentendag.monuments.repository.MonumentDataRepository
import nl.amsterdam.openmonumentendag.monuments.repository.SavedMonumentDataRepository
import nl.amsterdam.openmonumentendag.monuments.source.AggregateDataSource
import nl.amsterdam.openmonumentendag.monuments.source.api.MonumentApiDataSource
import nl.amsterdam.openmonumentendag.monuments.source.api.MonumentsApiService
import nl.amsterdam.openmonumentendag.monuments.source.db.MonumentDbDataSource
import nl.amsterdam.openmonumentendag.monuments.source.db.SavedMonumentDataSource
import java.text.SimpleDateFormat
import java.util.*

class MonumentDetailActivity : AppCompatActivity(), OnMapReadyCallback, MonumentContract.View {

    companion object {
        private const val MONUMENT_ID = "monument_id"
        private const val RETURN_LAST_MARKER_KEY = "returnLastMarker"

        fun getStartIntent(context: Context?, monumentId: Int): Intent {
            val intent = Intent(context, MonumentDetailActivity::class.java)
            intent.putExtra(MONUMENT_ID, monumentId)
            return intent
        }

        fun getStartIntentFromMap(context: Context?, monumentId: Int): Intent {
            val intent = Intent(context, MonumentDetailActivity::class.java)
            intent.putExtra(MONUMENT_ID, monumentId)
            intent.putExtra(RETURN_LAST_MARKER_KEY, true)
            return intent
        }
    }

    private lateinit var monument: Monument
    private lateinit var presenter: MonumentPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monument_detail)


        val aggregateDataSource = AggregateDataSource(
                MonumentApiDataSource(MonumentsApiService(okHttpClient = OpenMonumentenDagApplication.okHttpClient, languageCode = Locale.getDefault().language)),
                MonumentDbDataSource(OpenMonumentenDagApplication.monumentDbHelper)
        )

        val savedMonumentDataSource = SavedMonumentDataSource(OpenMonumentenDagApplication.monumentDbHelper)

        presenter = MonumentPresenter(this,
                MonumentDataRepository(aggregateDataSource),
                SavedMonumentDataRepository(savedMonumentDataSource))

        if (intent.getBooleanExtra(RETURN_LAST_MARKER_KEY, false)) {
            setResult(MapMonumentsFragment.DETAIL_ACTIVITY_RESULT_CODE, Intent().apply {
                putExtra(MapMonumentsFragment.LAST_OPEN_MARKER_KEY,
                        intent.extras.getInt(MONUMENT_ID, -1))
            })
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.getMonument(intent.extras.getInt(MONUMENT_ID))
    }

    override fun onStop() {
        presenter.detach()
        super.onStop()
    }

    override fun onMonumentLoaded(monument: Monument) {
        this.monument = monument
        setupViews()
    }

    override fun onStartLoading() {
    }

    override fun onEndLoading() {
    }

    override fun onMonumentSaved(id: Int) {
        detailSavedIcon.setImageResource(R.drawable.ic_saved_active)
    }

    override fun onMonumentUnsaved(id: Int) {
        detailSavedIcon.setImageResource(R.drawable.ic_saved)
    }

    private fun setupViews() {
        Picasso.get()
                .loadWithBaseUrl(monument.photos[0].main)
                .resize(800, 600)
                .centerCrop()
                .into(monumentDetailImage)

        detailTitleText.text = monument.title
        detailAddressText.text = monument.address

        val calInstance = Calendar.getInstance()
        detailTimingsText.text = monument.times.map {
            calInstance.time = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it.date)
            String.format("%s: %s - %s", calInstance.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()), it.open, it.close)
        }.joinToString(", ")

        detailEntryText.text = monument.entry

        if (monument.link.isNotEmpty()) {
            sep1.visibility = View.VISIBLE
            detailWebsiteText.visibility = View.VISIBLE
            detailWebsiteText.text = monument.link
        } else {
            sep1.visibility = View.INVISIBLE
            detailWebsiteText.visibility = View.INVISIBLE
        }

        if (monument.refreshments) {
            sep2.visibility = View.VISIBLE
            detailIconRefreshments.visibility = View.VISIBLE
            detailRefreshmentText.visibility = View.VISIBLE
        } else {
            sep2.visibility = View.INVISIBLE
            detailIconRefreshments.visibility = View.INVISIBLE
            detailRefreshmentText.visibility = View.INVISIBLE
        }

        if (monument.accessible) {
            sep3.visibility = View.VISIBLE
            detailIconWheelchair.visibility = View.VISIBLE
            detailWheelchairText.visibility = View.VISIBLE
        } else {
            sep3.visibility = View.INVISIBLE
            detailIconWheelchair.visibility = View.INVISIBLE
            detailWheelchairText.visibility = View.INVISIBLE
        }

        detailDescriptionText.text = monument.description

        if (!monument.year.isBlank()) {
            detailYearGroup.visibility = View.VISIBLE
            detailYearText.text = monument.year
        }

        if (!monument.architect.isBlank()) {
            detailArchitectGroup.visibility = View.VISIBLE
            detailArchitectText.text = monument.architect
        }

        if (!monument.sights.isBlank()) {
            detailSeeGroup.visibility = View.VISIBLE
            detailSeeText.text = monument.sights
        }

        if (!monument.activityInfos.isEmpty()) {
            detailDoGroup.visibility = View.VISIBLE
            detailDoText.text = monument.activityInfos.joinToString("|")
        }

        if (!monument.fact.isBlank()) {
            detailDidYouKnowGroup.visibility = View.VISIBLE
            detailDidYouKnowText.text = monument.fact
        }

        if (monument.saved) {
            detailSavedIcon.setImageResource(R.drawable.ic_saved_active)
        } else {
            detailSavedIcon.setImageResource(R.drawable.ic_saved)
        }

        setupListeners()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.detailMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupListeners() {
        saveLayout.setOnClickListener {
            presenter.saveMonument(monument.id)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.uiSettings?.setAllGesturesEnabled(false)

        val location = LatLng(monument.location.latitude, monument.location.longitude)
        googleMap?.addMarker(MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromResource(
                        if (monument.saved) R.drawable.ic_map_pointer_saved else R.drawable.ic_map_pointer
                ))
        )
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

}
