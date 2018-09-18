package nl.amsterdam.openmonumentendag.monuments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.map_info_window.view.*
import nl.amsterdam.openmonumentendag.OpenMonumentenDagApplication
import nl.amsterdam.openmonumentendag.R
import nl.amsterdam.openmonumentendag.data.Monument
import nl.amsterdam.openmonumentendag.loadWithBaseUrl
import nl.amsterdam.openmonumentendag.monuments.presenter.MonumentsContract
import nl.amsterdam.openmonumentendag.monuments.presenter.MonumentsPresenter
import nl.amsterdam.openmonumentendag.monuments.repository.MonumentDataRepository
import nl.amsterdam.openmonumentendag.monuments.repository.SavedMonumentDataRepository
import nl.amsterdam.openmonumentendag.monuments.source.AggregateDataSource
import nl.amsterdam.openmonumentendag.monuments.source.api.MonumentApiDataSource
import nl.amsterdam.openmonumentendag.monuments.source.api.MonumentsApiService
import nl.amsterdam.openmonumentendag.monuments.source.db.MonumentDbDataSource
import nl.amsterdam.openmonumentendag.monuments.source.db.SavedMonumentDataSource
import nl.amsterdam.openmonumentendag.utils.RoundedCornersTransformation
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class MapMonumentsFragment : Fragment(), MonumentsContract.View, OnMapReadyCallback {

    companion object {
        const val TAG = "MapMonumentsFragment"

        const val PERMISSION_REQUEST = 101
        const val DETAIL_ACTIVITY_RESULT_CODE = 111
        const val LAST_OPEN_MARKER_KEY = "lastOpenMarkerId"

        fun getInstance(args: Bundle?): MapMonumentsFragment {
            val fragment = MapMonumentsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var presenter: MonumentsPresenter
    private lateinit var monumentsList: List<Monument>
    private var mapWasLoaded = false
    private var lastOpenMarkerId = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_monument_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val aggregateDataSource = AggregateDataSource(
                MonumentApiDataSource(MonumentsApiService(okHttpClient = OpenMonumentenDagApplication.okHttpClient, languageCode = Locale.getDefault().language)),
                MonumentDbDataSource(OpenMonumentenDagApplication.monumentDbHelper))

        presenter = MonumentsPresenter(this,
                MonumentDataRepository(aggregateDataSource),
                SavedMonumentDataRepository(SavedMonumentDataSource(OpenMonumentenDagApplication.monumentDbHelper)))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lastOpenMarkerId = savedInstanceState?.getInt(LAST_OPEN_MARKER_KEY, -1) ?: -1
    }

    override fun onStart() {
        super.onStart()
        presenter.attach()
    }

    override fun onStop() {
        presenter.detach()
        super.onStop()
    }

    override fun onMonumentsLoaded(monumentsList: List<Monument>) {
        this.monumentsList = monumentsList
        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onMonumentSaved(id: Int) {
        //todo
    }

    override fun onMonumentUnsaved(id: Int) {
        //todo
    }

    override fun onStartLoading() {

    }

    override fun onEndLoading() {

    }

    private fun isPermissionAvailable(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context!!, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(permission: String) {
        if (context != null) {
            if (!isPermissionAvailable(permission)) {
                requestPermissions(arrayOf(permission), PERMISSION_REQUEST)
            } else {
                onPermissionGranted()
            }
        } else {
            onPermissionDenied()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }
    }

    private fun onPermissionGranted() {
        loadMap()
    }

    private fun onPermissionDenied() {
        loadMap()
    }

    private fun loadMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(LAST_OPEN_MARKER_KEY, lastOpenMarkerId)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DETAIL_ACTIVITY_RESULT_CODE) {
            lastOpenMarkerId = data?.getIntExtra(LAST_OPEN_MARKER_KEY, -1) ?: -1
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {

        // Enable my current location in UI and functionality
        if (isPermissionAvailable(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Get user location
            if (context != null) {
                FusedLocationProviderClient(context!!).lastLocation
            }
            googleMap?.isMyLocationEnabled = true
            googleMap?.uiSettings?.isMyLocationButtonEnabled = true
            googleMap?.setPadding(0, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    24f,
                    resources.displayMetrics).toInt(), 0, 0)
        }

        googleMap?.clear()

        // Add markers for all monuments
        val latLngBoundsBuilder: LatLngBounds.Builder = LatLngBounds.Builder()
        var markerToOpen: Marker? = null
        for (monument in monumentsList) {
            val location = LatLng(monument.location.latitude, monument.location.longitude)
            val marker = googleMap?.addMarker(MarkerOptions()
                    .position(location)
                    .title(monument.title)
                    .snippet(monument.address)
                    .icon(BitmapDescriptorFactory.fromResource(
                            if (monument.saved) R.drawable.ic_map_pointer_saved else R.drawable.ic_map_pointer
                    ))
            )
            marker?.tag = MapInfoWindowItem(monument)
            if (lastOpenMarkerId == monument.id) {
                markerToOpen = marker
            }
            latLngBoundsBuilder.include(location)
        }

        // Add info window for each marker
        googleMap?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoContents(marker: Marker?): View? {
                val mapInfoWindowItem = marker?.tag as MapInfoWindowItem?
                if (mapInfoWindowItem != null) {
                    val imageRefreshed = mapInfoWindowItem.imageRefreshed
                    val monument = mapInfoWindowItem.monument
                    val infoView = layoutInflater.inflate(R.layout.map_info_window, null)
                    infoView.infoTitleText.text = monument.title
                    infoView.infoSubtitleText.text = monument.address
                    infoView.infoDescriptionText.text = monument.times.joinToString(", ") {
                        val calInstance = Calendar.getInstance()
                        calInstance.time = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it.date)
                        String.format("%s: %s - %s", calInstance.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()), it.open, it.close)
                    }
                    val photosSize = monument.photos.size
                    if (photosSize > 0) {
                        Picasso.get()
                                .loadWithBaseUrl(monument.photos[0].thumb)
                                .transform(RoundedCornersTransformation())
                                .resize(400, 300)
                                .centerCrop()
                                .into(infoView.infoImageView, object : Callback {
                                    override fun onSuccess() {
                                        if (marker != null && !imageRefreshed && marker.isInfoWindowShown) {
                                            mapInfoWindowItem.imageRefreshed = true
                                            marker.tag = mapInfoWindowItem
                                            marker.showInfoWindow()
                                        }
                                    }

                                    override fun onError(e: Exception?) {
                                    }
                                })
                    }
                    lastOpenMarkerId = monument.id
                    return infoView
                }
                return null
            }

            override fun getInfoWindow(marker: Marker?): View? {
                return null
            }
        })

        // set listeners
        googleMap?.setOnInfoWindowClickListener {
            val mapInfoWindowItem = it.tag as MapInfoWindowItem
            val monument = mapInfoWindowItem.monument
            startActivityForResult(MonumentDetailActivity.getStartIntentFromMap(context, monument.id), DETAIL_ACTIVITY_RESULT_CODE)
        }

        if (!mapWasLoaded) {
            // By default set camera focus include all markers
            googleMap?.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(),
                            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                    40f,
                                    resources.displayMetrics).toInt())
            )
        }

        markerToOpen?.showInfoWindow()

        mapWasLoaded = true
    }

    data class MapInfoWindowItem(val monument: Monument, var imageRefreshed: Boolean = false)
}