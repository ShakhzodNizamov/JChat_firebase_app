package uz.jagito.jchat.ui.screens.register

import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_enter_phone_number.*
import uz.jagito.jchat.R
import uz.jagito.jchat.database.*
import uz.jagito.jchat.utilits.*
import java.util.concurrent.TimeUnit


class EnterPhoneNumberFragment : Fragment(R.layout.fragment_enter_phone_number) {
    private lateinit var mPhoneNumber: String
    private lateinit var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onStart() {
        super.onStart()
        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = AUTH.currentUser?.uid.toString()
                        val dateMap = mutableMapOf<String, Any>()
                        dateMap[CHILD_ID] = uid
                        dateMap[CHILD_PHONE] = mPhoneNumber

                        REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
                            .addListenerForSingleValueEvent(AppValueEventListener {
                                if (!it.hasChild(CHILD_USERNAME)) {
                                    dateMap[CHILD_USERNAME] = uid
                                }
                                REF_DATABASE_ROOT.child(
                                    NODE_PHONES
                                ).child(mPhoneNumber).setValue(uid)
                                    .addOnFailureListener { showToast(it.message.toString()) }
                                    .addOnSuccessListener {
                                        REF_DATABASE_ROOT.child(
                                            NODE_USERS
                                        ).child(uid).updateChildren(dateMap)
                                            .addOnSuccessListener {
                                                showToast("Добро пожаловать")
                                                restartActivity()
                                            }
                                            .addOnFailureListener{showToast(it.message.toString())}
                                    }
                            })
                    } else {
                        showToast(task.exception?.message.toString())
                    }
                }


                /*
                AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast("Добро пожаловать")
                        restartActivity()
                    } else {
                        showToast(task.exception?.message.toString())
                    }
                } */
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                showToast(p0.message.toString())
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                replaceFragment(
                    EnterCodeFragment(
                        mPhoneNumber,
                        id
                    )
                )
            }
        }
        register_btn_next.setOnClickListener { sendCode() }
    }

    private fun sendCode() {
        if (register_input_phone_number.text.toString().isEmpty()) {
            showToast(getString(R.string.register_toast_enter_phone))
        } else {
            authUser()
        }
    }

    private fun authUser() {
        mPhoneNumber = register_input_phone_number.text.toString()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            mPhoneNumber,
            60,
            TimeUnit.SECONDS,
            APP_ACTIVITY,
            mCallback
        )
    }
}