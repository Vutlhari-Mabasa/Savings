package com.example.savings

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.savings.data.AdviceInput
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AdviceActivity : AppCompatActivity() {

    private lateinit var etJsonInput: EditText
    private lateinit var btnGetAdvice: Button
    private lateinit var tvAdviceResults: TextView
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advice)

        etJsonInput = findViewById(R.id.etJsonInput)
        btnGetAdvice = findViewById(R.id.btnGetAdvice)
        tvAdviceResults = findViewById(R.id.tvAdviceResults)

        btnGetAdvice.setOnClickListener {
            handleAdvice()
        }
    }

    private fun handleAdvice() {
        val json = etJsonInput.text.toString()

        try {
            val type = object : TypeToken<List<AdviceInput>>() {}.type
            val inputList: List<AdviceInput> = gson.fromJson(json, type)

            if (inputList.isEmpty()) {
                tvAdviceResults.text = "No data provided."
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

            tvAdviceResults.text = suggestions.joinToString("\n\n")

        } catch (e: Exception) {
            Toast.makeText(this, "Invalid JSON", Toast.LENGTH_SHORT).show()
        }
    }
}
