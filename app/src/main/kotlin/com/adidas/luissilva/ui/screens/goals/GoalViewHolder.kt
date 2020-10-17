package com.adidas.luissilva.ui.screens.goals

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.adidas.luissilva.database.model.Goal
import com.adidas.luissilva.databinding.ItemGoalBinding
import com.adidas.luissilva.ui.base.adapter.BaseViewHolder
import com.adidas.luissilva.ui.base.adapter.ClickType

class GoalViewHolder(itemView: View, adapter: RecyclerView.Adapter<*>? = null, recyclerView: RecyclerView? = null) : BaseViewHolder<Goal, ItemGoalBinding>(itemView, adapter, recyclerView) {

    override fun bind(index: Int, item: Goal, clickListener: (Int, Goal, Enum<*>) -> Unit, viewPool: RecyclerView.RecycledViewPool?) {
        dataBinding?.let { holder ->

            holder.itemGoalTitle.text = item.title
            holder.itemGoalDescription.text = item.description
            holder.itemGoalTrophy.text = item.reward.trophy
            holder.itemGoalReward.text = "${item.reward.points}"


            holder.root.setOnClickListener {
                clickListener.invoke(index, item, ClickType.CARD)
            }
        }
    }

}