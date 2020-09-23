package uz.jagito.jchat.ui.screens.single_chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.*
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.choice_upload.*
import kotlinx.android.synthetic.main.fragment_single_chat.*
import kotlinx.android.synthetic.main.toolbar_info.view.*
import uz.jagito.jchat.R
import uz.jagito.jchat.database.*
import uz.jagito.jchat.models.CommonModel
import uz.jagito.jchat.models.UserModel
import uz.jagito.jchat.ui.screens.BaseFragment
import uz.jagito.jchat.ui.screens.main_list.MainListFragment
import uz.jagito.jchat.ui.screens.message_recycler_view.views.AppViewFactory
import uz.jagito.jchat.utilits.*


class SingleChatFragment(private val contact: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {
    private lateinit var mListenerInfoToolbar: AppValueEventListener
    private lateinit var mReceivingUser: UserModel
    private lateinit var mToolbarInfo: View
    private lateinit var mRefUser: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: SingleChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: AppChildEventListener
    private var mCountMessages = LOAD_MESSAGE_COUNT
    private var mIsScrolling = false
    private var mSmoothScrollToPosition = true
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager
    private val mAppVoiceRecorder = AppVoiceRecorder()
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>

    override fun onResume() {
        super.onResume()

        initFields()
        initToolbar()
        initRecyclerView()
        initSendMessageButton()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFields() {
        setHasOptionsMenu(true)
        mBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_choice)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mSwipeRefreshLayout = chat_swipe_refresh
        mLayoutManager = LinearLayoutManager(APP_ACTIVITY, LinearLayoutManager.VERTICAL, false)

        chat_input_message.addTextChangedListener(AppTextWatcher {
            val input = chat_input_message.text.toString().trim()
            if (input.isEmpty()) {
                chat_btn_send_message.visibility = View.INVISIBLE
                chat_btn_attach_file.visibility = View.VISIBLE
                chat_btn_voice.visibility = View.VISIBLE

            } else {
                chat_btn_send_message.visible()
                chat_btn_attach_file.visibility = View.GONE
                chat_btn_voice.visibility = View.GONE
            }
        })
        chat_btn_attach_file.setOnClickListener { attach() }

        chat_btn_voice.setOnTouchListener { _, motionEvent ->
            if (checkPermission(RECORD_AUDIO)) {
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    chat_input_message.hint = getString(R.string.chat_recording_voice)
                    chat_btn_voice.setColorFilter(
                        ContextCompat.getColor(
                            APP_ACTIVITY,
                            R.color.primary
                        )
                    )
                    val messageKey = getMessageKey(contact.id)
                    mAppVoiceRecorder.startRecord(messageKey)
                } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                    chat_input_message.hint = getString(R.string.chat_hint_message)
                    chat_btn_voice.colorFilter = null
                    mAppVoiceRecorder.stopRecord { file, messageKey ->
                        uploadFileToStorage(
                            Uri.fromFile(file),
                            messageKey,
                            contact.id,
                            TYPE_MESSAGE_VOICE
                        )
                        mSmoothScrollToPosition = true
                    }
                }
            }
            true
        }
    }

    private fun attach() {
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        btn_attach_file.setOnClickListener { attachFile() }
        btn_attach_image.setOnClickListener { attachImage() }
    }

    private fun attachImage() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .start(APP_ACTIVITY, this)
    }

    private fun attachFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE, Activity.RESULT_OK -> {
                    val uri = CropImage.getActivityResult(data).uri
                    val messageKey = getMessageKey(contact.id)
                    uploadFileToStorage(uri, messageKey, contact.id, TYPE_MESSAGE_IMAGE)
                    mSmoothScrollToPosition = true
                }
                PICK_FILE_REQUEST_CODE -> {
                    val uri = data.data
                    if (uri != null) {
                        val messageKey = getMessageKey(contact.id)
                        val fileName: String = getFileNameFromUri(uri)
                        uploadFileToStorage(uri, messageKey, contact.id, TYPE_MESSAGE_FILE, fileName)
                        mSmoothScrollToPosition = true
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        mRecyclerView = chat_recycle_view
        mLayoutManager.stackFromEnd = true
        mRecyclerView.layoutManager = mLayoutManager
        mAdapter = SingleChatAdapter()
        mRecyclerView.apply {
            adapter = mAdapter
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
        }

        mRefMessages = REF_DATABASE_ROOT
            .child(NODE_MESSAGES)
            .child(CURRENT_UID)
            .child(contact.id)
        mMessagesListener = AppChildEventListener {
            val message = it.getCommonModel()

            if (mSmoothScrollToPosition) {
                mAdapter.addMessageToBottom(AppViewFactory.getView(message)) {
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
            } else {
                mAdapter.addMessage(AppViewFactory.getView(message)) {
                    mSwipeRefreshLayout.isRefreshing = false
                }
            }
        }

        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mIsScrolling &&
                    dy < 0 &&
                    mLayoutManager.findFirstVisibleItemPosition() <= 3
                ) {
                    updateData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolling = true
                }
            }
        })

        mSwipeRefreshLayout.setOnRefreshListener {
            updateData()
        }
    }

    private fun updateData() {
        mSmoothScrollToPosition = false
        mIsScrolling = false
        mCountMessages += LOAD_MESSAGE_COUNT
        mRefMessages.removeEventListener(mMessagesListener)
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
    }

    private fun initToolbar() {
        mToolbarInfo = APP_ACTIVITY.mToolbar.toolbar_info
        mToolbarInfo.visibility = View.VISIBLE
        mListenerInfoToolbar = AppValueEventListener {
            mReceivingUser = it.getUserModel()
            initInfoToolbar()
        }
        mRefUser = REF_DATABASE_ROOT.child(
            NODE_USERS
        ).child(contact.id)
        mRefUser.addValueEventListener(mListenerInfoToolbar)

    }

    private fun initSendMessageButton() {
        chat_btn_send_message.setOnClickListener {
            mSmoothScrollToPosition = true
            val message = chat_input_message.text.toString().trim()
            if (message.isEmpty()) {
                showToast(getString(R.string.toast_input_is_empty))
            } else sendMessage(
                message,
                contact.id,
                TYPE_TEXT
            ) {
                saveToMainList(contact.id, TYPE_CHAT)
                chat_input_message.setText("")
            }
        }
    }

    private fun initInfoToolbar() {
        mToolbarInfo.apply {
            toolbar_chat_image.downloadAndSetImage(mReceivingUser.photoUrl)
            toolbar_chat_status.text = mReceivingUser.state
            toolbar_chat_fullname.text = if (mReceivingUser.fullname.isEmpty()) contact.fullname
            else mReceivingUser.fullname
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.single_chat_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear_chat -> clearChat(contact.id){
                showToast("Чат очищен")
                replaceFragment(MainListFragment())
            }
            R.id.menu_delete_chat -> deleteChat(contact.id){
                showToast("Чат удалён")
                replaceFragment(MainListFragment())
            }
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        mToolbarInfo.visibility = View.GONE
        mRefUser.removeEventListener(mListenerInfoToolbar)
        mRefMessages.removeEventListener(mMessagesListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAppVoiceRecorder.releaseRecorder()
        mAdapter.onDestroy()
    }
}