package uz.jagito.jchat

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.jagito.jchat.database.AUTH
import uz.jagito.jchat.database.initFirebase
import uz.jagito.jchat.database.initUser
import uz.jagito.jchat.databinding.ActivityMainBinding
import uz.jagito.jchat.ui.objects.AppDrawer
import uz.jagito.jchat.ui.screens.main_list.MainListFragment
import uz.jagito.jchat.ui.screens.register.EnterPhoneNumberFragment
import uz.jagito.jchat.utilits.*

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    lateinit var mAppDrawer: AppDrawer
    lateinit var mToolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        APP_ACTIVITY = this
        initFirebase()
        initUser {
            CoroutineScope(Dispatchers.IO).launch {
                initContacts()
            }
            initFields()
            initFunc()
        }
    }

    private fun initFunc() {
        setSupportActionBar(mToolbar)
        if (AUTH.currentUser != null) {
            mAppDrawer.create()
            replaceFragment(MainListFragment(), false)
        } else {
            replaceFragment(EnterPhoneNumberFragment(),false)
        }
    }

    private fun initFields() {
        mToolbar = mBinding.mainToolbar
        mAppDrawer =
            AppDrawer()
    }

    override fun onResume() {
        super.onResume()
        AppStates.updateState(AppStates.ONLINE)
        println("onResume")
    }

    override fun onStart() {
        super.onStart()
        println("onStart")
        AppStates.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        println("onStop")
        AppStates.updateState(AppStates.OFFLINE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(APP_ACTIVITY, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            initContacts()
        }
    }
}