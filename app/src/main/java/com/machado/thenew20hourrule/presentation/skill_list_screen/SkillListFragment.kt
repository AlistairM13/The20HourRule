package com.machado.thenew20hourrule.presentation.skill_list_screen

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.machado.thenew20hourrule.R
import com.machado.thenew20hourrule.data.local.entities.Skill
import com.machado.thenew20hourrule.databinding.FragmentSkillListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SkillListFragment : Fragment() {
    private lateinit var binding: FragmentSkillListBinding

    private val viewModel: SkillListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSkillListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val skillListAdapter = SkillListAdapter()

        Log.i("MYTAG", "${viewModel.skill ?: "nooo"} fragment")
        if (viewModel.skill != null) {
            navigateToSkillDetailsScreen(viewModel.skill!!)
        }

        binding.rvSkillList.apply {
            adapter = skillListAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        viewModel.allSkills.observe(viewLifecycleOwner) {
            skillListAdapter.differ.submitList(it)
            if (it.isEmpty()) {
                binding.tvEmptyList.visibility = View.VISIBLE
            } else {
                binding.tvEmptyList.visibility = View.GONE
            }
        }
        skillListAdapter.setOnSkillEditListener {
            createOrEditProjectDialog(it)
        }
        skillListAdapter.setOnSkillDeleteListener {
            viewModel.deleteSkill(it)
            Snackbar.make(binding.cLayout, "Skill Removed", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    viewModel.undoDelete()
                }
                .show()
        }

        skillListAdapter.setOnSkillItemClickListener {
            viewModel.onSkillItemClicked(it)
            navigateToSkillDetailsScreen(it)
        }

        binding.fabAddNewSkill.setOnClickListener {
            createOrEditProjectDialog(null)
        }
    }

    private fun navigateToSkillDetailsScreen(skill: Skill) {
        val action =
            SkillListFragmentDirections.actionSkillListFragmentToSkillDetailFragment(skill)
        findNavController().navigate(action)
    }

    private fun createOrEditProjectDialog(skill: Skill?) {
        val customDialogLayout =
            layoutInflater.inflate(R.layout.create_or_edit_skill_detail_dialog, null)

        val skillName =
            customDialogLayout.findViewById<TextInputEditText>(R.id.etDialogBoxSkillName)
        val skillGoal =
            customDialogLayout.findViewById<TextInputEditText>(R.id.etDialogBoxSkillGoal)

        with(MaterialAlertDialogBuilder(requireContext())) {
            setTitle("Add New Skill")
            setView(customDialogLayout)
            setNegativeButton("Cancel") { _, _ -> }
            setPositiveButton("Done", null)
            create().apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        if (skillName.text.isNullOrBlank()) skillName.error =
                            "Skill name cannot be empty"
                        if (skillGoal.text.isNullOrBlank()) skillGoal.error =
                            "Have a goal my friend, it will help"
                        if (!skillGoal.text.isNullOrBlank() && !skillGoal.text.isNullOrBlank()) {
                            val updatedOrNewSkill = skill?.copy(
                                skillName = skillName.text.toString(),
                                finalGoal = skillGoal.text.toString()
                            )
                                ?: Skill(
                                    skillName = skillName.text.toString(),
                                    timeSpent = 0.0,
                                    finalGoal = skillGoal.text.toString()
                                )
                            Log.i("MYTAG", "$updatedOrNewSkill")
                            viewModel.insertSkill(updatedOrNewSkill)
                            dismiss()
                        }
                    }
                }
                if (skill != null) {
                    setTitle("Edit Skill")
                    skillName.setText(skill.skillName)
                    skillGoal.setText(skill.finalGoal)
                }
            }.show()
        }
    }

}