package com.machado.thenew20hourrule.presentation.skill_detail_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.machado.thenew20hourrule.databinding.FragmentSkillDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SkillDetailFragment : Fragment() {
    private lateinit var binding: FragmentSkillDetailBinding

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
        val skillId = args.skillId
    }

}