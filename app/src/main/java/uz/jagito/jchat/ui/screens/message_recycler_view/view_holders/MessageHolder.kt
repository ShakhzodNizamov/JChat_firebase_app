package uz.jagito.jchat.ui.screens.message_recycler_view.view_holders

import uz.jagito.jchat.ui.screens.message_recycler_view.views.MessageView

interface MessageHolder {
    fun drawMessageHolder(messageView: MessageView)
    fun onAttach(view: MessageView)
    fun onDetach()
}