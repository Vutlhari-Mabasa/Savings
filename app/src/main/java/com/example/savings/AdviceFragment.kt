package com.example.savings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.savings.databinding.FragmentAdviceBinding
import com.example.savings.data.AdviceInput
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken



class AdviceFragment : Fragment() {

    private var _binding: FragmentAdviceBinding? = null
    private val binding get() = _binding!!
    private val gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdviceBinding.inflate(inflater, container, false)

        binding.btnGetAdvice.setOnClickListener {
            handleAdvice()
        }

        return binding.root
    }

    private fun handleAdvice() {
        val json = binding.etJsonInput.text.toString()

        try {
            val type = object : TypeToken<List<AdviceInput>>() {}.type
            val inputList: List<AdviceInput> = gson.fromJson(json, type)

            if (inputList.isEmpty()) {
                binding.tvAdviceResults.text = "No data provided."
                return
            }

            val max = inputList.maxByOrNull { it.amount }
            val avg = inputList.map { it.amount }.average()
            val suggestions = mutableListOf<String>()

            max?.let {
                suggestions.add("You spend the most on **${it.category}**. Consider reducing this category.")
            }

            inputList.forEach {
                if (it.amount > avg) {
                    suggestions.add("Your spending on ${it.category} is above average. Review it.")
                }
            }

            if (suggestions.isEmpty()) {
                suggestions.add("Your spending looks balanced. Keep it up!")
            }

            binding.tvAdviceResults.text = suggestions.joinToString("\n\n")

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Invalid JSON", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
