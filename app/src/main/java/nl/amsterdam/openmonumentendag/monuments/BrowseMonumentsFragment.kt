package nl.amsterdam.openmonumentendag.monuments

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_browse_monuments.*
import kotlinx.android.synthetic.main.item_monument_card.view.*
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
import java.util.*


class BrowseMonumentsFragment : Fragment(), MonumentsContract.View {

    companion object {
        const val TAG = "BrowseMonumentsFragment"

        fun getInstance(args: Bundle?): BrowseMonumentsFragment {
            val fragment = BrowseMonumentsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var presenter: MonumentsPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_browse_monuments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        browseMonumentInnerLayout.post {
            val params = browseMonumentInnerLayout.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior as AppBarLayout.ScrollingViewBehavior?
            if (behavior != null) {
                behavior.overlayTop = browseMonumentHeaderImage.measuredHeight / 2
            }
        }

        val aggregateDataSource = AggregateDataSource(
                MonumentApiDataSource(MonumentsApiService(okHttpClient = OpenMonumentenDagApplication.okHttpClient, languageCode = Locale.getDefault().language)),
                MonumentDbDataSource(OpenMonumentenDagApplication.monumentDbHelper))

        presenter = MonumentsPresenter(this,
                MonumentDataRepository(aggregateDataSource),
                SavedMonumentDataRepository(SavedMonumentDataSource(OpenMonumentenDagApplication.monumentDbHelper)))
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
        setupRecyclerView(monumentsList)
    }

    override fun onMonumentSaved(id: Int) {
        //todo
    }

    override fun onMonumentUnsaved(id: Int) {
        //todo
    }

    override fun onStartLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun onEndLoading() {
        progressBar.visibility = View.GONE
    }

    private fun setupRecyclerView(monumentsList: List<Monument>) {
        if (recyclerView.adapter != null) {
            (recyclerView.adapter as Adapter).updateItems(monumentsList)
        } else {
            recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = Adapter(
                    monumentsList.toMutableList(),
                    presenter.monumentSaveActionPublisher,
                    object : Adapter.Callback {
                        override fun onMonumentClick(monument: Monument) {
                            startActivity(MonumentDetailActivity.getStartIntent(context, monument.id))
                        }

                        override fun onSaveClick(monument: Monument) {
                            presenter.saveMonument(monument.id)
                        }
                    }
            )
        }
    }


    class Adapter(val dataList: MutableList<Monument>?,
                  val monumentSavedPublisher: PublishSubject<MonumentsPresenter.MonumentSave>,
                  val callback: Callback) : RecyclerView.Adapter<Adapter.ViewHolder>() {

        interface Callback {
            fun onMonumentClick(monument: Monument)
            fun onSaveClick(monument: Monument)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_monument_card, parent, false))
        }

        override fun getItemCount(): Int {
            return dataList?.size ?: 0
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val monument = dataList?.get(position)
            if (monument != null) {
                holder.titleView.text = monument.title
                holder.subtitleView.text = monument.address
                if (monument.saved) {
                    holder.saveIcon.setImageResource(R.drawable.ic_saved_active)
                } else {
                    holder.saveIcon.setImageResource(R.drawable.ic_saved)
                }
                val photosSize = monument.photos.size
                if (photosSize > 0) {
                    Picasso.get()
                            .loadWithBaseUrl(monument.photos[0].main)
                            .fit()
                            .centerCrop()
                            .into(holder.imageView)
                }
                holder.rootView.setOnClickListener { callback.onMonumentClick(monument) }
                holder.saveLayout.setOnClickListener {
                    callback.onSaveClick(monument)
                }

                holder.disposable.clear()
                holder.disposable.add(monumentSavedPublisher.subscribe { monumentSave ->
                    if (monumentSave.id == monument.id) {
                        holder.saveIcon.setImageResource(
                            if (monumentSave.saved) R.drawable.ic_saved_active
                            else R.drawable.ic_saved
                        )
                        monument.saved = monumentSave.saved
                    }
                })
            }
        }

        fun updateItems(newItems: List<Monument>) {
            Single.create(object: SingleOnSubscribe<DiffUtil.DiffResult> {
                override fun subscribe(emitter: SingleEmitter<DiffUtil.DiffResult>) {
                    emitter.onSuccess(DiffUtil.calculateDiff(MonumentDiffCallback(dataList ?: mutableListOf(), newItems.toList())))
                }
            }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { diffResult ->
                    dataList?.clear()
                    dataList?.addAll(newItems.toList())
                    diffResult.dispatchUpdatesTo(this)
                }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val disposable = CompositeDisposable()
            val rootView = itemView
            val imageView = itemView.imageView
            val titleView = itemView.titleText
            val subtitleView = itemView.subtitleText
            val saveLayout = itemView.saveLayout
            val saveIcon = itemView.itemCardSavedIcon
        }
    }

}
