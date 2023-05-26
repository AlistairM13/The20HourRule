package com.machado.thenew20hourrule.presentation.skill_detail_screen

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.machado.thenew20hourrule.R
import com.machado.thenew20hourrule.data.local.entities.Session
import com.machado.thenew20hourrule.data.local.entities.Skill
import com.machado.thenew20hourrule.databinding.FragmentSkillDetailBinding
import com.machado.thenew20hourrule.util.TimeHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SkillDetailFragment : Fragment() {
    private lateinit var binding: FragmentSkillDetailBinding
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    private val viewModel: SkillDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSkillDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: SkillDetailFragmentArgs by navArgs()
        val skill = args.skill

        binding.toolBar.apply {
            title = skill.skillName
            setTitleTextColor(Color.WHITE)
            setOnMenuItemClickListener {
                if (it.itemId == R.id.miHistory) {
                    Toast.makeText(context, "View history", Toast.LENGTH_SHORT).show()
                }
                true
            }
        }

        viewModel.currentTimeInSeconds.observe(viewLifecycleOwner) {
            binding.tvTime.text = TimeHelper.getFormattedTime(it)
        }
        viewModel.isSessionFinished.observe(viewLifecycleOwner) {
            if (it) {
                saveSessionDialog(skill)
            }
        }

        viewModel.session.observe(viewLifecycleOwner) { session ->
            if (session != null) {
                binding.apply {
                    tvSessionObjective.text =
                        getString(R.string.session_objective, session.objective)
                    tvSessionDuration.text =
                        getString(R.string.session_duration, session.sessionDurationInMin.toInt())
                }
            } else {
                binding.apply {
                    tvSessionObjective.text = getString(R.string.session_objective_not_set)
                    tvSessionDuration.text = getString(R.string.session_duration_not_set)
                }
            }
        }

        viewModel.isSessionStarted.observe(viewLifecycleOwner) { isStarted ->
            if (isStarted) {
                binding.btnStartOrStop.text = getString(R.string.btn_text_stop)
            } else {
                binding.btnStartOrStop.text = getString(R.string.btn_text_start)
            }
        }

        binding.btnStartOrStop.setOnClickListener {
            if (viewModel.session.value == null) {
                createOrEditSessionDialog(null, skill)
                return@setOnClickListener
            }
            viewModel.startOrStopSession()
        }

        binding.btnReset.setOnClickListener {
            viewModel.resetSession()
        }

        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.currentTimeInSeconds.value == 0L) {
                    navigateBackToSkillListScreen()
                } else {
                    saveSessionDialog(skill)
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback.remove()
    }

    private fun navigateBackToSkillListScreen() {
        findNavController().navigate(SkillDetailFragmentDirections.actionSkillDetailFragmentToSkillListFragment())
    }


    private fun createOrEditSessionDialog(session: Session?, skill: Skill) {
        val customDialogLayout =
            layoutInflater.inflate(R.layout.create_session_dialog, null)

        val sessionObjective =
            customDialogLayout.findViewById<TextInputEditText>(R.id.etDialogBoxSessionObjective)
        val sessionDuration =
            customDialogLayout.findViewById<TextInputEditText>(R.id.etDialogBoxSessionDuration)

        with(MaterialAlertDialogBuilder(requireContext())) {
            setTitle("Start new session")
            setView(customDialogLayout)
            setPositiveButton("Done", null)
            create().apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        if (sessionObjective.text.isNullOrBlank()) sessionObjective.error =
                            "Have a goal for this session"
                        if (sessionDuration.text.isNullOrBlank()) sessionDuration.error =
                            "Try going 30 minutes for a start"
                        if (!sessionObjective.text.isNullOrBlank() && !sessionDuration.text.isNullOrBlank()) {

                            val newSession = Session(
                                objective = sessionObjective.text.toString(),
                                sessionDurationInMin = sessionDuration.text.toString().toDouble(),
                                skillId = skill.skillId!!
                            )
                            // Set datetime when done
                            viewModel.setupSession(newSession)
                            viewModel.startOrStopSession()
                            dismiss()
                        }
                    }
                }
                if (session != null) {
                    setTitle("Edit Session")
                    sessionObjective.setText(session.objective)
                    sessionDuration.setText(session.sessionDurationInMin.toString())
                    setNegativeButton("Cancel") { _, _ -> }
                }
            }.show()
        }
    }

    private fun saveSessionDialog(skill: Skill) {
        with(MaterialAlertDialogBuilder(requireContext())) {
            setTitle("Well Done")
            setCancelable(false)
            setPositiveButton("Save session") { _, _ ->
                Log.i("MYTAG", "saved")
                viewModel.updateSession(skill)
                navigateBackToSkillListScreen()
            }
            if (viewModel.currentTimeInSeconds.value != 0L && viewModel.isSessionFinished.value == false) {
                setTitle("Save your work?")
                setMessage("Progress will be lost otherwise")
                setNegativeButton("Exit without saving") { _, _ ->
                    navigateBackToSkillListScreen()
                }
            }
            create()
            show()
        }
    }


}
