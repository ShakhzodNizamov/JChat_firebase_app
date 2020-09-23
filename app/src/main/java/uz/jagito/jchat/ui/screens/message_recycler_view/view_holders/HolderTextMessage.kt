package uz.jagito.jchat.ui.screens.message_recycler_view.view_holders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.message_item_text.view.*
import uz.jagito.jchat.database.CURRENT_UID
import uz.jagito.jchat.ui.screens.message_recycler_view.views.MessageView
import uz.jagito.jchat.utilits.asTime

class HolderTextMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {
    val blockUserMessage: ConstraintLayout = view.bloc_user_message
    val chatUserMessage: TextView = view.chat_user_message
    val chatUserMessageTime: TextView = view.chat_user_message_time

    val blockReceivedMessage: ConstraintLayout = view.bloc_received_message
    val chatReceivedMessage: TextView = view.chat_received_message
    val chatReceivedMessageTime: TextView = view.chat_received_message_time



    override fun drawMessageHolder(messageView: MessageView) {
        if (messageView.from == CURRENT_UID) {
            blockUserMessage.visibility = View.VISIBLE
            blockReceivedMessage.visibility = View.GONE
            chatUserMessage.text = messageView.text
            chatUserMessageTime.text =
                messageView.timeStamp.asTime()


        } else {
            blockUserMessage.visibility = View.GONE
            blockReceivedMessage.visibility = View.VISIBLE
            chatReceivedMessage.text = messageView.text
            chatReceivedMessageTime.text =
                messageView.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {
    }

    override fun onDetach() {
    }
}