package damayanti.evi.covidinfo.commons

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import damayanti.evi.covidinfo.R
import damayanti.evi.covidinfo.model.ChatDetailData

class CustomAdapter(private val items: MutableList<ChatDetailData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val BOT = 0
    private val SENDER = 1
    private val TIME = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == BOT) {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_bot, parent, false)
            return CustomAdapterBotViewHolder(itemView)
        } else if (viewType == SENDER) {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_sender, parent, false)
            return CustomAdapterSenderViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_time, parent, false)
            return CustomAdapterTimeViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (items[position].type == "bot") {
            if (holder is CustomAdapterBotViewHolder) {

                val chatDetails = items[position]
                //val context = holder.profileImageView.context

                holder.messageTextView.text = chatDetails.message
                holder.timeTextView.text = chatDetails.timestamp

            }

        } else if (items[position].type == "sender") {
            if (holder is CustomAdapterSenderViewHolder) {

                val chatDetails = items[position]
                //val context = holder.profileImageView.context
                holder.messageTextView.text = chatDetails.message
                holder.timeTextView.text = chatDetails.timestamp
            }

        } else if (items[position].type == "time") {
            if (holder is CustomAdapterTimeViewHolder) {
                holder.timeTextView.text = items[position].message
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        when (items[position].type) {
            "bot" -> return BOT
            "sender" -> return SENDER
            else -> return TIME
        }
    }

    inner class CustomAdapterBotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var profileImageView: ImageView
        internal var messageTextView: TextView
        internal var timeTextView: TextView

        init {
            profileImageView = itemView.findViewById(R.id.profileImageView)
            messageTextView = itemView.findViewById(R.id.messageTextView)
            timeTextView = itemView.findViewById(R.id.timeTextView)
        }
    }

    inner class CustomAdapterSenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var profileImageView: ImageView
        internal var messageTextView: TextView
        internal var timeTextView: TextView

        init {

            profileImageView = itemView.findViewById(R.id.profileImageView)
            messageTextView = itemView.findViewById(R.id.messageTextView)
            timeTextView = itemView.findViewById(R.id.timeTextView)
        }
    }

    inner class CustomAdapterTimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var timeTextView: TextView

        init {

            timeTextView = itemView.findViewById(R.id.dateTextView)
        }
    }



}

