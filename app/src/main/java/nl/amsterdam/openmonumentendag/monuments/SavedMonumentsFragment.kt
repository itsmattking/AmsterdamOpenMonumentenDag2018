package nl.amsterdam.openmonumentendag.monuments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.squareup.picasso.Picasso
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_saved_monuments.*
import kotlinx.android.synthetic.main.item_monument_banner.view.*
import nl.amsterdam.openmonumentendag.OpenMonumentenDagApplication
import nl.amsterdam.openmonumentendag.R
import nl.amsterdam.openmonumentendag.data.Monument
import nl.amsterdam.openmonumentendag.loadWithBaseUrl
import nl.amsterdam.openmonumentendag.monuments.presenter.SavedMonumentsContract
import nl.amsterdam.openmonumentendag.monuments.presenter.SavedMonumentsPresenter
import nl.amsterdam.openmonumentendag.monuments.repository.SavedMonumentDataRepository
import nl.amsterdam.openmonumentendag.monuments.source.db.SavedMonumentDataSource
import nl.amsterdam.openmonumentendag.utils.RoundedCornersTransformation


class SavedMonumentsFragment : Fragment(), SavedMonumentsContract.View {

    companion object {
        const val TAG = "SavedMonumentsFragment"
        var lastKnownLocation: LatLng? = null

        fun getInstance(args: Bundle?): SavedMonumentsFragment {
            val fragment = SavedMonumentsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var presenter: SavedMonumentsPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_saved_monuments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedMonumentInnerLayout.post {
            val params = savedMonumentInnerLayout.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior as AppBarLayout.ScrollingViewBehavior?
            if (behavior != null) {
                behavior.overlayTop = savedMonumentHeaderImage.measuredHeight / 2
            }
        }

        val savedMonumentDataSource = SavedMonumentDataSource(OpenMonumentenDagApplication.monumentDbHelper)
        presenter = SavedMonumentsPresenter(this, SavedMonumentDataRepository(savedMonumentDataSource))
    }

    override fun onStart() {
        super.onStart()
        presenter.attach()
    }

    override fun onStop() {
        presenter.detach()
        super.onStop()
    }

    override fun onSavedMonumentsLoaded(items: List<Monument>) {
        setupRecyclerView(items)
    }

    override fun onRemovedSavedMonument(id: Int) {
        // todo
    }

    override fun onStartLoading() {
        savedProgressBar.visibility = View.VISIBLE
    }

    override fun onEndLoading() {
        savedProgressBar.visibility = View.GONE
    }

    private fun setupRecyclerView(monumentsList: List<Monument>) {
        if (savedRecyclerView.adapter != null) {
            (savedRecyclerView.adapter as Adapter).updateItems(monumentsList)
        } else {
            savedRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            savedRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            savedRecyclerView.adapter = SavedMonumentsFragment.Adapter(monumentsList.toMutableList(), object : Adapter.Callback {
                override fun onMonumentClick(monument: Monument) {
                    startActivity(MonumentDetailActivity.getStartIntent(context, monument.id))
                }
            })
        }
        savedPlaceholderText.visibility = if (monumentsList.isEmpty()) View.VISIBLE else View.GONE
        updateLastLocation()
    }

    private fun updateLastLocation() {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient(context!!).lastLocation.addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result?.apply {
                        lastKnownLocation = LatLng(this.latitude, this.longitude)
                        savedRecyclerView.adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    class Adapter(val dataList: MutableList<Monument>?, val callback: Callback) : RecyclerView.Adapter<Adapter.ViewHolder>() {

        interface Callback {
            fun onMonumentClick(monument: Monument)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_monument_banner, parent, false))
        }

        override fun getItemCount(): Int {
            return dataList?.size ?: 0
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val monument = dataList?.get(position)
            if (monument != null) {
                holder.titleView.text = monument.title
                holder.subtitleView.text = monument.address
                val photosSize = monument.photos.size
                if (photosSize > 0) {
                    Picasso.get()
                            .loadWithBaseUrl(monument.photos[0].thumb)
                            .transform(RoundedCornersTransformation())
                            .fit()
                            .centerCrop()
                            .into(holder.imageView)
                }
                if (lastKnownLocation != null) {
                    val distance = SphericalUtil.computeDistanceBetween(
                            LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude),
                            LatLng(monument.location.latitude, monument.location.longitude)
                    )
                    holder.distanceView.visibility = View.VISIBLE
                    holder.distanceView.text = String.format("%.2f km", distance / 1000)
                } else {
                    holder.distanceView.visibility = View.GONE
                }
                holder.rootView.setOnClickListener { callback.onMonumentClick(monument) }
            }
        }

        fun updateItems(newItems: List<Monument>) {
            Single.create(object : SingleOnSubscribe<DiffUtil.DiffResult> {
                override fun subscribe(emitter: SingleEmitter<DiffUtil.DiffResult>) {
                    emitter.onSuccess(DiffUtil.calculateDiff(MonumentDiffCallback(dataList
                            ?: arrayListOf(), newItems)))
                }
            }).subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { diffResult ->
                        this.dataList?.clear()
                        this.dataList?.addAll(newItems)
                        diffResult.dispatchUpdatesTo(this)
                    }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView = itemView.bannerImageView
            val titleView = itemView.bannerTitleText
            val subtitleView = itemView.bannerSubtitleText
            val distanceView = itemView.bannerDistanceText
            val rootView = itemView
        }
    }
}