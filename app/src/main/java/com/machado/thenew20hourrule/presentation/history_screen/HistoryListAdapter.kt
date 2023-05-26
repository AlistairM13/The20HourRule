package com.machado.thenew20hourrule.presentation.history_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.machado.thenew20hourrule.R
import com.machado.thenew20hourrule.data.local.entities.Session
import com.machado.thenew20hourrule.databinding.NormalListItemHistoryBinding

class HistoryListAdapter : RecyclerView.Adapter<HistoryListAdapter.SkillViewHolder>() {

    private val callback = object : DiffUtil.ItemCallback<Session>() {
        override fun areItemsTheSame(oldItem: Session, newItem: Session): Boolean {
            return oldItem.skillId == newItem.skillId
        }

        override fun areContentsTheSame(oldItem: Session, newItem: Session): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val binding =
            NormalListItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SkillViewHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val session = differ.currentList[position]
        holder.bind(session)
    }

    inner class SkillViewHolder(
        private val binding: NormalListItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(session: Session) {
            binding.apply {
                tvHistorySessionObjective.text = session.objective
                tvHistorySessionDuration.text =
                    root.resources.getString(R.string.tv_session_time_spent, session.sessionDurationInMin)
                tvHistorySessionDateTime.text = session.sessionDateTime

                if (session.sessionDurationInMin > 60) {
                    binding.root.setCardBackgroundColor(
                        root.resources.getColor(
                            R.color.orange,
                            null
                        )
                    )
                }
            }
        }
    }
}