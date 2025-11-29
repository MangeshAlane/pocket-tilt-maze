package com.pockettilt.maze.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.pockettilt.maze.R
import com.pockettilt.maze.data.PreferencesManager
import com.pockettilt.maze.databinding.FragmentSettingsBinding

/**
 * Settings fragment for user preferences
 */
class SettingsFragment : Fragment() {
    
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefsManager: PreferencesManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        prefsManager = PreferencesManager(requireContext())
        
        loadSettings()
        setupListeners()
    }
    
    private fun loadSettings() {
        // Vibration
        binding.switchVibration.isChecked = prefsManager.isVibrationEnabled()
        
        // Maze size
        val mazeSize = prefsManager.getMazeSize()
        binding.sliderMazeSize.value = when (mazeSize) {
            10 -> 0f
            15 -> 1f
            20 -> 2f
            else -> 1f
        }
        updateMazeSizeLabel(mazeSize)
        
        // Sensitivity
        val sensitivity = prefsManager.getSensitivity()
        when {
            sensitivity < 0.8f -> binding.radioLow.isChecked = true
            sensitivity > 1.2f -> binding.radioHigh.isChecked = true
            else -> binding.radioMedium.isChecked = true
        }
    }
    
    private fun setupListeners() {
        // Vibration switch
        binding.switchVibration.setOnCheckedChangeListener { _, isChecked ->
            prefsManager.setVibrationEnabled(isChecked)
        }
        
        // Maze size slider
        binding.sliderMazeSize.addOnChangeListener { _, value, _ ->
            val size = when (value.toInt()) {
                0 -> 10
                1 -> 15
                2 -> 20
                else -> 15
            }
            prefsManager.setMazeSize(size)
            updateMazeSizeLabel(size)
        }
        
        // Sensitivity radio group
        binding.radioGroupSensitivity.setOnCheckedChangeListener { _, checkedId ->
            val sensitivity = when (checkedId) {
                R.id.radio_low -> 0.6f
                R.id.radio_medium -> 1.0f
                R.id.radio_high -> 1.5f
                else -> 1.0f
            }
            prefsManager.setSensitivity(sensitivity)
        }
    }
    
    private fun updateMazeSizeLabel(size: Int) {
        binding.tvMazeSizeValue.text = when (size) {
            10 -> getString(R.string.maze_size_small)
            15 -> getString(R.string.maze_size_medium)
            20 -> getString(R.string.maze_size_large)
            else -> getString(R.string.maze_size_medium)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
