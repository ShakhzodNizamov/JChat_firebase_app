package uz.jagito.jchat.ui.screens

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import uz.jagito.jchat.R
import uz.jagito.jchat.utilits.APP_ACTIVITY
import uz.jagito.jchat.utilits.hideKeyboard

open class BaseChangeFragment(@LayoutRes layout: Int) : Fragment(layout) {
    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        APP_ACTIVITY.mAppDrawer.disableDrawer()
        hideKeyboard()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        APP_ACTIVITY.menuInflater.inflate(R.menu.settings_menu_confirm, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings_confirm_change) change()
        return true
    }

    open fun change() {}
}