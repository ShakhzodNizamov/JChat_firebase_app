package uz.jagito.jchat.ui.screens

import androidx.fragment.app.Fragment
import uz.jagito.jchat.utilits.APP_ACTIVITY


open class BaseFragment(layout: Int) : Fragment(layout) {


    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.mAppDrawer.disableDrawer()
    }

}