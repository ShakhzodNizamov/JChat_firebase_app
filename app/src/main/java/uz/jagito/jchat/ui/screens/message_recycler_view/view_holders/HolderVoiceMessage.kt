package uz.jagito.jchat.ui.screens.message_recycler_view.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.message_item_voice.view.*
import uz.jagito.jchat.database.CURRENT_UID
import uz.jagito.jchat.ui.screens.message_recycler_view.views.MessageView
import uz.jagito.jchat.utilits.AppVoicePlayer
import uz.jagito.jchat.utilits.asTime
import uz.jagito.jchat.utilits.gone
import uz.jagito.jchat.utilits.visible

class HolderVoiceMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {

    private val mAppVoicePlayer = AppVoicePlayer()

    private val blocReceivedVoiceMessage: ConstraintLayout = view.bloc_received_voice_message
    private val blocUserVoiceMessage: ConstraintLayout = view.bloc_user_voice_message

    private val chatReceivedVoiceMessageTime: TextView = view.chat_received_voice_message_time
    private val chatUserVoiceMessageTime: TextView = view.chat_user_voice_message_time

    private val chatUserBtnPlay: ImageView = view.chat_user_btn_play
    private val chatUserBtnStop: ImageView = view.chat_user_btn_stop

    private val chatReceivedBtnPlay: ImageView = view.chat_received_btn_play
    private val chatReceivedBtnStop: ImageView = view.chat_received_btn_stop


    override fun drawMessageHolder(messageView: MessageView) {
        if (messageView.from == CURRENT_UID) {

            blocUserVoiceMessage.visibility = View.VISIBLE
            blocReceivedVoiceMessage.visibility = View.GONE
            chatUserVoiceMessageTime.text =
                messageView.timeStamp.asTime()


        } else {
            blocUserVoiceMessage.visibility = View.GONE
            blocReceivedVoiceMessage.visibility = View.VISIBLE
            chatReceivedVoiceMessageTime.text =
                messageView.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {
        mAppVoicePlayer.init()
        if (view.from == CURRENT_UID) {
            chatUserBtnPlay.setOnClickListener {
                chatUserBtnPlay.gone()
                chatUserBtnStop.visible()
                chatUserBtnStop.setOnClickListener {
                    stop {
                        chatUserBtnPlay.visible()
                        chatUserBtnStop.gone()
                    }
                }
                play(view) {
                    chatUserBtnPlay.visible()
                    chatUserBtnStop.gone()
                }
            }
        } else {
            chatReceivedBtnPlay.setOnClickListener {
                chatReceivedBtnPlay.gone()
                chatReceivedBtnStop.visible()
                chatReceivedBtnStop.setOnClickListener {
                    stop {
                        chatReceivedBtnPlay.visible()
                        chatReceivedBtnStop.gone()
                    }
                }
                play(view) {
                    chatReceivedBtnPlay.visible()
                    chatReceivedBtnStop.gone()
                }
            }
        }
    }

    private fun stop(function: () -> Unit) {
        mAppVoicePlayer.stop {
            function()
        }
    }

    private fun play(
        view: MessageView,
        function: () -> Unit
    ) {
        mAppVoicePlayer.play(view.id, view.fileUrl) {
            function()
        }
    }

    override fun onDetach() {
        chatReceivedBtnPlay.setOnClickListener(null)
        chatUserBtnPlay.setOnClickListener(null)
        mAppVoicePlayer.release()
    }
}