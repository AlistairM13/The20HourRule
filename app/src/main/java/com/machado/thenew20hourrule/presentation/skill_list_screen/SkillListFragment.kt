package com.machado.thenew20hourrule.presentation.skill_list_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.machado.thenew20hourrule.databinding.FragmentSkillListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SkillListFragment : Fragment() {
    private lateinit var binding: FragmentSkillListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSkillListBinding.inflate(inflater, container, false)
        return binding.root
    }

}