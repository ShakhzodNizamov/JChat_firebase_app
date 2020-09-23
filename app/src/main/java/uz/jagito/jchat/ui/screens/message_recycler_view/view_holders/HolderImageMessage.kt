package uz.jagito.jchat.ui.screens.message_recycler_view.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.message_item_image.view.*
import uz.jagito.jchat.database.CURRENT_UID
import uz.jagito.jchat.ui.screens.message_recycler_view.views.MessageView
import uz.jagito.jchat.utilits.asTime
import uz.jagito.jchat.utilits.downloadAndSetImage

class HolderImageMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {
    private val blocReceivedImageMessage: ConstraintLayout = view.bloc_received_image_message
    private val chatReceivedImage: ImageView = view.chat_received_image
    private val chatReceivedImageMessageTime: TextView = view.chat_received_image_message_time

    private val blocUserImageMessage: ConstraintLayout = view.bloc_user_image_message
    private val chatUserImage: ImageView = view.chat_user_image
    private val chatUserImageMessageTime: TextView = view.chat_user_image_message_time

    override fun drawMessageHolder(messageView: MessageView) {
        if (messageView.from == CURRENT_UID) {

            blocUserImageMessage.visibility = View.VISIBLE
            blocReceivedImageMessage.visibility = View.GONE
            chatUserImage.downloadAndSetImage(messageView.fileUrl)
            chatUserImageMessageTime.text =
                messageView.timeStamp.asTime()


        } else {
            blocUserImageMessage.visibility = View.GONE
            blocReceivedImageMessage.visibility = View.VISIBLE
            chatReceivedImage.downloadAndSetImage(messageView.fileUrl)
            chatReceivedImageMessageTime.text =
                messageView.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {
    }

    override fun onDetach() {
    }
}