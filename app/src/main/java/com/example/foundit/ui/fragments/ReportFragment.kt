package com.example.foundit.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.foundit.R
import com.example.foundit.data.local.AppDatabase
import com.example.foundit.data.local.entities.Post
import com.example.foundit.databinding.FragmentReportBinding
import kotlinx.coroutines.launch
import java.util.Calendar

class ReportFragment : Fragment(R.layout.fragment_report) {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReportBinding.bind(view)

        setupToolbar()
        setupDatePicker()
        setupSubmitButton()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    binding.etDate.setText("$d/${m + 1}/$y")
                },
                year, month, day
            )
            datePicker.show()
        }
    }

    private fun setupSubmitButton() {
        binding.btnSubmitReport.setOnClickListener {
            val type = if (binding.toggleGroup.checkedButtonId == R.id.btnLost) "Lost" else "Found"
            val name = binding.etItemName.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()
            val date = binding.etDate.text.toString().trim()
            val contact = binding.etContactName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()

            if (name.isEmpty() || description.isEmpty() || location.isEmpty() || contact.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            } else {
                // Create a Post object (imageUri left null for now, add image picker later)
                val post = Post(
                    title = name,
                    description = description,
                    location = location,
                    date = date.ifEmpty { "Just now" },
                    imageUri = null,
                    postedBy = contact,
                    isFound = type == "Found",
                    phone = phone,
                    email = email.ifEmpty { "N/A" }
                )

                // Save to DB
                val db = AppDatabase.getDatabase(requireContext())
                lifecycleScope.launch {
                    db.postDao().insertPost(post)

                    // Navigate to home or details
                    findNavController().navigateUp()
                    Toast.makeText(requireContext(), "Report submitted!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
