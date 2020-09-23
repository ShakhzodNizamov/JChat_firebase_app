package uz.jagito.jchat.ui.screens.settings

import kotlinx.android.synthetic.main.fragment_change_bio.*
import uz.jagito.jchat.R
import uz.jagito.jchat.database.*
import uz.jagito.jchat.ui.screens.BaseChangeFragment


class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {
    override fun onResume() {
        super.onResume()
        settings_input_bio.setText(USER.bio)

    }

    override fun change() {
        super.change()
        val newBio = settings_input_bio.text.toString()
        if (newBio.isEmpty()) {
            //TODO when bio is empty
        } else {
            setBioToDatabase(newBio)
        }
    }
}