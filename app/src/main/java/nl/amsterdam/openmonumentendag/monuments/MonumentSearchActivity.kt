package nl.amsterdam.openmonumentendag.monuments

import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import io.reactivex.disposables.Disposables
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_monument_search.*
import kotlinx.android.synthetic.main.item_monument_banner.view.*
import nl.amsterdam.openmonumentendag.OpenMonumentenDagApplication
import nl.amsterdam.openmonumentendag.R
import nl.amsterdam.openmonumentendag.data.Monument
import nl.amsterdam.openmonumentendag.loadWithBaseUrl
import nl.amsterdam.openmonumentendag.monuments.presenter.MonumentSearchContract
import nl.amsterdam.openmonumentendag.monuments.presenter.MonumentSearchPresenter
import nl.amsterdam.openmonumentendag.monuments.repository.MonumentSearchDataRepository
import nl.amsterdam.openmonumentendag.monuments.source.db.MonumentDbDataSource
import nl.amsterdam.openmonumentendag.source.DataSourceSearchQuery
import nl.amsterdam.openmonumentendag.utils.RoundedCornersTransformation


class MonumentSearchActivity : AppCompatActivity(),
        MonumentSearchContract.View {

    lateinit var monumentSearchPresenter: MonumentSearchPresenter
    var clickDisposable = Disposables.empty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monument_search)

        val monumentSearchDataRepository = MonumentSearchDataRepository(MonumentDbDataSource(OpenMonumentenDagApplication.monumentDbHelper))
        monumentSearchPresenter = MonumentSearchPresenter(this, monumentSearchDataRepository)

        monumentSearchBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.isNotBlank()) {
                    monumentSearchPresenter.searchMonuments(DataSourceSearchQuery(p0.toString()))
                }
            }
        })

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            monumentSearchBox.post {
                monumentSearchBox.compoundDrawables[0].let {
                    val wrappedDrawable = DrawableCompat.wrap(it)
                    DrawableCompat.setTintMode(wrappedDrawable, PorterDuff.Mode.SRC_ATOP)
                    DrawableCompat.setTint(wrappedDrawable, ResourcesCompat.getColor(resources, R.color.colorGrey, theme))
                    monumentSearchBox.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable, null, null, null)
                }
            }
        }

        val layoutManager = LinearLayoutManager(this)
        monumentSearchRecyclerView.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        monumentSearchRecyclerView.addItemDecoration(dividerItemDecoration)
    }

    override fun onMonumentsSearchResults(monuments: List<Monument>) {
        if (monumentSearchRecyclerView.adapter != null) {
            (monumentSearchRecyclerView.adapter as MonumentSearchRecyclerAdapter).updateList(monuments)
        } else {
            val adapter = MonumentSearchRecyclerAdapter(monuments.toMutableList())
            clickDisposable.dispose()
            clickDisposable = adapter.publishSubject.subscribe{ monument -> startActivity(MonumentDetailActivity.getStartIntent(this, monument.id))}
            monumentSearchRecyclerView.adapter = adapter
        }
    }

    override fun onMonumentsSearchStart() {
        searchProgressBar.visibility = View.VISIBLE
    }

    override fun onMonumentsSearchEnd() {
        searchProgressBar.visibility = View.GONE
    }

    class MonumentSearchRecyclerAdapter(val monumentList: MutableList<Monument>) : RecyclerView.Adapter<MonumentSearchRecyclerAdapter.MonumentSearchViewHolder>() {
        val publishSubject: PublishSubject<Monument> = PublishSubject.create()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonumentSearchViewHolder {
            return MonumentSearchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_monument_banner, parent, false))
        }

        override fun getItemCount(): Int {
            return monumentList.size
        }

        override fun onBindViewHolder(holder: MonumentSearchViewHolder, position: Int) {
            monumentList[position].let {
                holder.titleView.text = it.title
                holder.addressView.text = it.address
                if (it.photos.size > 0) {
                    Picasso.get()
                            .loadWithBaseUrl(it.photos[0].thumb)
                            .transform(RoundedCornersTransformation())
                            .fit()
                            .centerCrop()
                            .into(holder.imageView)
                }
            }
        }

        fun updateList(newMonumentList: List<Monument>) {
            monumentList.clear()
            monumentList.addAll(newMonumentList)
            notifyDataSetChanged()
        }

        inner class MonumentSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val rootView = itemView
            val titleView = itemView.bannerTitleText
            val imageView = itemView.bannerImageView
            val addressView = itemView.bannerSubtitleText
            init {
                rootView.setOnClickListener { v -> publishSubject.onNext(monumentList[adapterPosition])}
            }
        }
    }
}
