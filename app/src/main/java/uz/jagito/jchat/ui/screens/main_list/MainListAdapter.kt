package uz.jagito.jchat.ui.screens.main_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.main_list_item.view.*
import uz.jagito.jchat.R
import uz.jagito.jchat.models.CommonModel
import uz.jagito.jchat.ui.screens.single_chat.SingleChatFragment
import uz.jagito.jchat.utilits.*

class MainListAdapter : RecyclerView.Adapter<MainListAdapter.MainListHolder>() {
    private val listItems = mutableListOf<CommonModel>()

    class MainListHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.main_list_item_name
        val itemLastMessage: TextView = view.main_list_item_last_message
        val itemPhoto: CircleImageView = view.main_list_photo
        val itemTime: TextView = view.main_list_item_time
        val itemDeliver: View = view.main_list_item_deliver_line
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.main_list_item, parent, false)
        val holder = MainListHolder(view)
        holder.itemView.setOnClickListener {
            replaceFragment(SingleChatFragment(listItems[holder.adapterPosition]))
        }
        return holder
    }

    override fun getItemCount() = listItems.size

    override fun onBindViewHolder(holder: MainListHolder, position: Int) {
        val item = listItems[position]
        holder.apply {
            itemName.text = item.fullname
            itemPhoto.downloadAndSetImage(item.photoUrl)
            itemLastMessage.text = item.lastMessage
            itemTime.text = item.timeStamp.toString().asTime()
            if (position == itemCount - 1) {
                itemDeliver.invisible()
            } else if (!itemDeliver.isVisible()) {
                itemDeliver.visible()
            }
        }
    }

    fun updateListItems(item: CommonModel) {
        listItems.add(item)
        notifyItemInserted(itemCount)
    }
}