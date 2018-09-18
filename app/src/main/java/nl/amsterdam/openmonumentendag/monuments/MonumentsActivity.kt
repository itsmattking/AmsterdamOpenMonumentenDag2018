package nl.amsterdam.openmonumentendag.monuments

import android.animation.TimeInterpolator
import android.content.Intent
import android.graphics.Interpolator
import android.os.Bundle
import android.support.transition.Fade
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.animation.AccelerateInterpolator
import com.google.android.gms.maps.MapFragment
import kotlinx.android.synthetic.main.activity_monuments.*
import nl.amsterdam.openmonumentendag.R
import nl.amsterdam.openmonumentendag.R.id.navigationView
import nl.amsterdam.openmonumentendag.monuments.MonumentsActivity.Companion.LAST_NAVIGATION_SELECTED_ID_KEY


class MonumentsActivity : AppCompatActivity() {

    companion object {
        const val LAST_NAVIGATION_SELECTED_ID_KEY = "lastNavigationSelectedId"
    }

    private var lastNavigationSelectedId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monuments)
        lastNavigationSelectedId = savedInstanceState?.getInt(LAST_NAVIGATION_SELECTED_ID_KEY, -1) ?: -1
        searchFab.setOnClickListener { v -> startActivity(Intent(this, MonumentSearchActivity::class.java))}
        setupNavigationBar()
    }

    private fun setupNavigationBar() {
        navigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_browse -> changeFragment(BrowseMonumentsFragment.Companion::getInstance, BrowseMonumentsFragment.TAG)
                R.id.action_saved -> changeFragment(SavedMonumentsFragment.Companion::getInstance, SavedMonumentsFragment.TAG)
                R.id.action_map -> changeFragment(MapMonumentsFragment.Companion::getInstance, MapMonumentsFragment.TAG)
            }
            lastNavigationSelectedId = item.itemId
            true
        }
        navigationView.selectedItemId = if (lastNavigationSelectedId != -1) lastNavigationSelectedId else R.id.action_browse
    }

    private fun changeFragment(create: (args: Bundle?) -> Fragment, tag: String, args: Bundle? = null) {
        val fragment = supportFragmentManager.findFragmentByTag(tag) ?: create(args)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment, tag)
        fragmentTransaction.commit()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt(LAST_NAVIGATION_SELECTED_ID_KEY, lastNavigationSelectedId)
        if (lastNavigationSelectedId == R.id.action_map) {
            val mapsFragment: Fragment? = supportFragmentManager.findFragmentByTag(MapMonumentsFragment.TAG)
            mapsFragment?.onSaveInstanceState(outState ?: Bundle())
        }
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (navigationView.selectedItemId != R.id.action_browse) {
            navigationView.selectedItemId = R.id.action_browse
        } else {
            super.onBackPressed()
        }
    }
}
