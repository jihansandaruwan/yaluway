package com.example.yaluway.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yaluway.R
import com.example.yaluway.model.PostRequest
import com.example.yaluway.util.ImageBinder
import com.google.android.material.button.MaterialButton

class RequestAdapter(
    private var items: List<PostRequest>,
    private val incoming: Boolean,
    private val onAccept: (PostRequest) -> Unit,
    private val onDecline: (PostRequest) -> Unit
) : RecyclerView.Adapter<RequestAdapter.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imgRequestThumb)
        val title: TextView = view.findViewById(R.id.tvRequestTitle)
        val status: TextView = view.findViewById(R.id.tvRequestStatus)
        val actions: View = view.findViewById(R.id.rowActions)
        val accept: MaterialButton = view.findViewById(R.id.btnAccept)
        val decline: MaterialButton = view.findViewById(R.id.btnDecline)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_request, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context
        ImageBinder.bind(holder.image, item.postImage, R.drawable.yaluway_onboard_market)
        if (incoming) {
            holder.title.text = context.getString(R.string.asked_for, item.requesterName, item.postTitle)
            holder.actions.visibility = if (item.status == "PENDING") View.VISIBLE else View.GONE
            holder.status.text = when (item.status) {
                "ACCEPTED" -> context.getString(R.string.request_accepted, item.ownerContact)
                "DECLINED" -> context.getString(R.string.request_declined)
                else -> context.getString(R.string.request_pending)
            }
        } else {
            holder.title.text = context.getString(R.string.you_asked_for, item.postTitle)
            holder.actions.visibility = View.GONE
            holder.status.text = when (item.status) {
                "ACCEPTED" -> context.getString(R.string.request_accepted, item.ownerContact)
                "DECLINED" -> context.getString(R.string.request_declined)
                else -> context.getString(R.string.request_pending)
            }
        }
        holder.accept.setOnClickListener { onAccept(item) }
        holder.decline.setOnClickListener { onDecline(item) }
    }

    fun submitList(newItems: List<PostRequest>) {
        items = newItems
        notifyDataSetChanged()
    }
}
