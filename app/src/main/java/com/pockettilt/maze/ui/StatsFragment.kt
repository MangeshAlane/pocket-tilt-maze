package com.pockettilt.maze.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pockettilt.maze.R
import com.pockettilt.maze.data.PreferencesManager
import com.pockettilt.maze.databinding.FragmentStatsBinding
import com.pockettilt.maze.databinding.ItemHistoryBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Stats fragment showing completion history
 */
class StatsFragment : Fragment() {
    
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefsManager: PreferencesManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        prefsManager = PreferencesManager(requireContext())
        
        setupUI()
    }
    
    private fun setupUI() {
        // Total completed
        val totalCompleted = prefsManager.getTotalCompleted()
        binding.tvTotalCompleted.text = getString(R.string.total_completed, totalCompleted)
        
        // History
        val history = prefsManager.getHistory(7)
        
        if (history.isEmpty()) {
            binding.tvNoHistory.visibility = View.VISIBLE
            binding.rvHistory.visibility = View.GONE
        } else {
            binding.tvNoHistory.visibility = View.GONE
            binding.rvHistory.visibility = View.VISIBLE
            
            val historyList = history.map { (date, time) ->
                HistoryItem(date, time)
            }.sortedByDescending { it.date }
            
            binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
            binding.rvHistory.adapter = HistoryAdapter(historyList, prefsManager)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    data class HistoryItem(val date: String, val timeMs: Long)
    
    class HistoryAdapter(
        private val items: List<HistoryItem>,
        private val prefsManager: PreferencesManager
    ) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
        
        class ViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }
        
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            
            // Format date
            val date = LocalDate.parse(item.date, DateTimeFormatter.ISO_LOCAL_DATE)
            val dateStr = date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
            
            // Format time
            val timeStr = prefsManager.formatTime(item.timeMs)
            
            holder.binding.tvDate.text = dateStr
            holder.binding.tvTime.text = timeStr
        }
        
        override fun getItemCount() = items.size
    }
}
