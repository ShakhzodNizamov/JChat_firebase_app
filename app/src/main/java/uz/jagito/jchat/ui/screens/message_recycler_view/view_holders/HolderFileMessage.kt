package uz.jagito.jchat.ui.screens.message_recycler_view.view_holders

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.message_item_file.view.*
import uz.jagito.jchat.database.CURRENT_UID
import uz.jagito.jchat.database.getFileFromStorage
import uz.jagito.jchat.ui.screens.message_recycler_view.views.MessageView
import uz.jagito.jchat.utilits.*
import java.io.File

class HolderFileMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {

    private val blocReceivedFileMessage: ConstraintLayout = view.bloc_received_file_message
    private val blocUserFileMessage: ConstraintLayout = view.bloc_user_file_message
    private val chatReceivedFileMessageTime: TextView = view.chat_received_file_message_time
    private val chatUserFileMessageTime: TextView = view.chat_user_file_message_time

    private val chatUserFileName: TextView = view.chat_user_filename
    private val chatUserBtnDownload: ImageView = view.chat_user_btn_download
    private val chatUserProgressBar: ProgressBar = view.chat_user_progress_bar

    private val chatReceivedBtnDownload: ImageView = view.chat_received_btn_download
    private val chatReceivedFileName: TextView = view.chat_received_filename
    private val chatReceivedProgressBar: ProgressBar = view.chat_received_progress_bar

    override fun drawMessageHolder(messageView: MessageView) {
        if (messageView.from == CURRENT_UID) {
            blocUserFileMessage.visible()
            blocReceivedFileMessage.gone()
            chatUserFileMessageTime.text = messageView.timeStamp.asTime()
            chatUserFileName.text = messageView.text
        } else {
            blocUserFileMessage.gone()
            blocReceivedFileMessage.visible()
            chatReceivedFileMessageTime.text = messageView.timeStamp.asTime()
            chatReceivedFileName.text = messageView.text
        }
    }

    override fun onAttach(view: MessageView) {
        if (view.from == CURRENT_UID) {
            chatUserBtnDownload.setOnClickListener { clickToBtnFile(view) }
        } else {
            chatReceivedBtnDownload.setOnClickListener { }
        }
    }

    private fun clickToBtnFile(view: MessageView) {
        if (view.from == CURRENT_UID) {
            chatUserBtnDownload.invisible()
            chatUserProgressBar.visible()
        } else {
            chatReceivedBtnDownload.invisible()
            chatReceivedProgressBar.visible()
        }
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            view.text
        )
        try {
            if (checkPermission(WRITE_FILES)){
                file.createNewFile()
                getFileFromStorage(file, view.fileUrl){
                    if (view.from == CURRENT_UID) {
                        chatUserBtnDownload.visible()
                        chatUserProgressBar.invisible()
                    } else {
                        chatReceivedBtnDownload.visible()
                        chatReceivedProgressBar.invisible()
                    }
                }
            }
        }catch (e: Exception){
            showToast(e.message.toString())
        }
    }

    override fun onDetach() {
        chatUserBtnDownload.setOnClickListener(null)
        chatReceivedBtnDownload.setOnClickListener(null)
    }
}