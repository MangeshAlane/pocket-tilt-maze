package com.pockettilt.maze.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pockettilt.maze.MainActivity
import com.pockettilt.maze.R
import com.pockettilt.maze.data.PreferencesManager
import com.pockettilt.maze.databinding.FragmentHomeBinding

/**
 * Home screen fragment with main menu
 */
class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefsManager: PreferencesManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        prefsManager = PreferencesManager(requireContext())
        
        // Show tutorial on first launch
        if (prefsManager.isFirstLaunch()) {
            showTutorial()
        }
        
        setupUI()
        setupBannerAd()
    }
    
    private fun setupUI() {
        // Update best time display
        updateBestTimeDisplay()
        
        // Button listeners
        binding.btnPlayDaily.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeToGame(
                isDaily = true,
                mazeSize = 15 // Daily maze is always 15x15
            )
            findNavController().navigate(action)
        }
        
        binding.btnPlayQuick.setOnClickListener {
            val mazeSize = prefsManager.getMazeSize()
            val action = HomeFragmentDirections.actionHomeToGame(
                isDaily = false,
                mazeSize = mazeSize
            )
            findNavController().navigate(action)
        }
        
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_settings)
        }
        
        binding.btnStats.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_stats)
        }
    }
    
    private fun updateBestTimeDisplay() {
        val bestTime = prefsManager.getDailyMazeBestTime()
        binding.tvBestTime.text = if (bestTime != null) {
            getString(R.string.todays_best_time, prefsManager.formatTime(bestTime))
        } else {
            getString(R.string.todays_best_time, getString(R.string.no_time_yet))
        }
    }
    
    private fun setupBannerAd() {
        val adManager = (requireActivity() as MainActivity).adManager
        val bannerAd = adManager.createBannerAd()
        binding.adContainer.removeAllViews()
        binding.adContainer.addView(bannerAd)
    }
    
    private fun showTutorial() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.app_name))
            .setMessage(getString(R.string.tutorial_message))
            .setPositiveButton(getString(R.string.got_it), null)
            .show()
    }
    
    override fun onResume() {
        super.onResume()
        updateBestTimeDisplay()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
