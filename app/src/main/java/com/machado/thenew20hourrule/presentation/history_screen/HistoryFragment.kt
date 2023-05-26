package com.machado.thenew20hourrule.presentation.history_screen

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.machado.thenew20hourrule.R
import com.machado.thenew20hourrule.databinding.FragmentHistoryBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: HistoryFragmentArgs by navArgs()
        val skill = args.skill

        binding.toolBarHistory.apply {
            title = skill.skillName
            setTitleTextColor(Color.WHITE)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.miSortByDuration -> {
                        viewModel.orderByDuration(false)
                    }

                    R.id.miSortByDurationReversed -> {
                        viewModel.orderByDuration(true)
                    }

                    R.id.miSortByDate -> {
                        viewModel.orderByDate(false)
                    }

                    R.id.miSortByDateReversed -> {
                        viewModel.orderByDate(true)
                    }
                }
                true
            }
        }

        viewModel.getAllSessions(skill)

        val historyListAdapter = HistoryListAdapter()
        binding.rvSessionHistory.apply {
            adapter = historyListAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        viewModel.allSessions.observe(viewLifecycleOwner) {
            it?.let { skillWithSessions ->
                if (skillWithSessions.sessions.isEmpty()) {
                    binding.apply {
                        tvNoHistoryFound.visibility = View.VISIBLE
                        rvSessionHistory.visibility = View.GONE
                    }
                } else {
                    binding.apply {
                        tvNoHistoryFound.visibility = View.GONE
                        rvSessionHistory.visibility = View.VISIBLE
                    }
                }
                binding.tvHistorySkillGoalName.text = skillWithSessions.skill.finalGoal
                historyListAdapter.differ.submitList(skillWithSessions.sessions)
            }
        }

    }
}