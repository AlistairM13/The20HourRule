package com.machado.thenew20hourrule.presentation.skill_list_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.machado.thenew20hourrule.R
import com.machado.thenew20hourrule.data.local.entities.Skill
import com.machado.thenew20hourrule.databinding.GridItemSkillBinding

class SkillListAdapter : RecyclerView.Adapter<SkillListAdapter.SkillViewHolder>() {

    private val callback = object : DiffUtil.ItemCallback<Skill>() {
        override fun areItemsTheSame(oldItem: Skill, newItem: Skill): Boolean {
            return oldItem.skillId == newItem.skillId
        }

        override fun areContentsTheSame(oldItem: Skill, newItem: Skill): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val binding =
            GridItemSkillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SkillViewHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val skill = differ.currentList[position]
        holder.bind(skill)
    }

    inner class SkillViewHolder(
        private val binding: GridItemSkillBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(skill: Skill) {
            binding.root.doOnLayout {
                val rootView = binding.skillCard
                val progressView = binding.skillProgressView

                val layoutParams = progressView.layoutParams
                val progress = if (skill.timeSpent > 20.0) 20 else skill.timeSpent.toInt()
                layoutParams.height = (rootView.height * progress / 20)
                progressView.layoutParams = layoutParams
            }

            binding.apply {
                tvSkillName.text = skill.skillName
                tvTimeSpent.text = itemView.context.getString(
                    R.string.skill_total_time_spent,
                    String.format("%.1f", skill.timeSpent)
                )
            }

            binding.root.setOnClickListener {
                onItemClickListener?.let {
                    it(skill)
                }
            }

            binding.btnDeleteSkill.setOnClickListener {
                onItemDeleteListener?.let {
                    it(skill)
                }
            }
            binding.btnEditSkill.setOnClickListener {
                onItemEditListener?.let {
                    it(skill)
                }
            }
            binding.btnShareSkillProgress.setOnClickListener {
                onItemShareSkillProgress?.let {
                    it(skill)
                }
            }
        }
    }


    private var onItemClickListener: ((Skill) -> Unit)? = null
    private var onItemDeleteListener: ((Skill) -> Unit)? = null
    private var onItemEditListener: ((Skill) -> Unit)? = null
    private var onItemShareSkillProgress: ((Skill) -> Unit)? = null

    fun setOnSkillItemClickListener(listener: (Skill) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnSkillEditListener(listener: (Skill) -> Unit) {
        onItemEditListener = listener
    }

    fun setOnSkillDeleteListener(listener: (Skill) -> Unit) {
        onItemDeleteListener = listener
    }

    fun setOnSkillShareProgressListener(listener: (Skill) -> Unit) {
        onItemShareSkillProgress = listener
    }

}