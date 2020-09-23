package uz.jagito.jchat.ui.screens.main_list

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_main_list.*
import uz.jagito.jchat.R
import uz.jagito.jchat.database.*
import uz.jagito.jchat.models.CommonModel
import uz.jagito.jchat.utilits.APP_ACTIVITY
import uz.jagito.jchat.utilits.AppValueEventListener
import uz.jagito.jchat.utilits.hideKeyboard


class MainListFragment : Fragment(R.layout.fragment_main_list) {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MainListAdapter
    private val mRefMainList = REF_DATABASE_ROOT
        .child(NODE_MAIN_LIST)
        .child(CURRENT_UID)
    private val mRefUser = REF_DATABASE_ROOT.child(NODE_USERS)
    private val mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    private var mListItems = listOf<CommonModel>()
    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "JChat"
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = main_list_recycler_view
        mAdapter = MainListAdapter()
        mRefMainList.addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot1 ->
            mListItems = dataSnapshot1.children.map { it.getCommonModel() }
            mListItems.forEach { model ->

                mRefUser.child(model.id)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot2 ->
                        val newModel = dataSnapshot2.getCommonModel()

                        mRefMessages.child(model.id).limitToLast(1)
                            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot3 ->
                                val tempList = dataSnapshot3.children.map { it.getCommonModel() }
                                if (tempList.isNotEmpty()) {
                                    newModel.timeStamp = tempList.first().timeStamp
                                    newModel.lastMessage = tempList.first().text
                                } else {
                                    newModel.timeStamp = ""
                                    newModel.lastMessage = "Чат очищен"
                                }

                                if (newModel.fullname.isEmpty()) {
                                    newModel.fullname = newModel.phone
                                }
                                mAdapter.updateListItems(newModel)
                            })
                    })
            }
        })
        mRecyclerView.adapter = mAdapter
    }
}