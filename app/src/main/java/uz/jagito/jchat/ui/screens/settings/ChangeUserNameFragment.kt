package uz.jagito.jchat.ui.screens.settings

import kotlinx.android.synthetic.main.fragment_change_user_name.*
import uz.jagito.jchat.R
import uz.jagito.jchat.database.*
import uz.jagito.jchat.ui.screens.BaseChangeFragment
import uz.jagito.jchat.utilits.AppValueEventListener
import uz.jagito.jchat.utilits.showToast
import java.util.*


class ChangeUserNameFragment : BaseChangeFragment(R.layout.fragment_change_user_name) {
    private lateinit var mNewUsername: String
    override fun onResume() {
        super.onResume()
        settings_input_username.setText(USER.username)
    }


    override fun change() {
        mNewUsername = settings_input_username.text
            .toString()
            .toLowerCase(Locale.getDefault())
            .trim()
        if (mNewUsername.isEmpty()) {
            showToast(getString(R.string.toast_input_is_empty))
        } else {
            REF_DATABASE_ROOT.child(NODE_USERNAMES)
                .addListenerForSingleValueEvent(AppValueEventListener {
                    if (it.hasChild(mNewUsername)) {
                        showToast(" уже существует")
                    } else {
                        changeUsername()
                    }
                })
            //changeUsername()
        }
    }

    private fun changeUsername() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(mNewUsername).setValue(
            CURRENT_UID
        )
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    updateCurrentUsername(mNewUsername)
                }
            }
    }


}