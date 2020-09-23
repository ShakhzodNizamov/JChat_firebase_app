package uz.jagito.jchat.ui.screens.single_chat

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import uz.jagito.jchat.ui.screens.message_recycler_view.view_holders.*
import uz.jagito.jchat.ui.screens.message_recycler_view.views.MessageView

class SingleChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListMessagesCache = mutableListOf<MessageView>()
    private var mListHolders = mutableListOf<MessageHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun getItemCount(): Int = mListMessagesCache.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MessageHolder).drawMessageHolder(mListMessagesCache[position])
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onAttach(mListMessagesCache[holder.adapterPosition])
        mListHolders.add((holder as MessageHolder))
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onDetach()
        mListHolders.remove((holder as MessageHolder))
        super.onViewDetachedFromWindow(holder)
    }

    override fun getItemViewType(position: Int): Int {
        return mListMessagesCache[position].getTypeView()
    }

    fun addMessageToBottom(
        message: MessageView,
        onSuccess: () -> Unit
    ) {
        if (!mListMessagesCache.contains(message)) {
            mListMessagesCache.add(message)
            notifyItemInserted(itemCount)
        }
        onSuccess()
    }


    fun addMessage(
        message: MessageView,
        onSuccess: () -> Unit
    ) {
        if (!mListMessagesCache.contains(message)) {
            mListMessagesCache.add(message)
            mListMessagesCache.sortBy { it.timeStamp }
            notifyItemInserted(0)
        }
        onSuccess()
    }

    fun onDestroy() {
        mListHolders.forEach {
            it.onDetach()
        }
    }
}


