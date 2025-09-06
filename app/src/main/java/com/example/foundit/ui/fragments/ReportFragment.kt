package com.example.foundit.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.foundit.R
import com.example.foundit.data.model.Post
import com.example.foundit.databinding.FragmentReportBinding
import com.example.foundit.ui.viewmodel.PostViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReportFragment : Fragment(R.layout.fragment_report) {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private val postViewModel: PostViewModel by viewModels()

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
                    val selectedDate = Calendar.getInstance().apply {
                        set(y, m, d)
                    }
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    binding.etDate.setText(dateFormat.format(selectedDate.time))
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
                val post = Post(
                    id = "",
                    title = name,
                    description = description,
                    location = location,
                    date = date.ifEmpty { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().time) },
                    imageUri = null,
                    postedBy = contact,
                    isFound = type == "Found",
                    phone = phone,
                    email = email.ifEmpty { "N/A" }
                )

                // Use the ViewModel to save the post
                setLoadingState(true)
                lifecycleScope.launch {
                    try {
                        postViewModel.savePost(post)
                        Toast.makeText(requireContext(), "Report submitted!", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Failed to submit report: ${e.message}", Toast.LENGTH_LONG).show()
                    } finally {
                        setLoadingState(false)
                    }
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.btnSubmitReport.isEnabled = !isLoading
        binding.tilItemName.isEnabled = !isLoading
        binding.tilDescription.isEnabled = !isLoading
        binding.tilLocation.isEnabled = !isLoading
        binding.tilDate.isEnabled = !isLoading
        binding.tilContactName.isEnabled = !isLoading
        binding.tilPhone.isEnabled = !isLoading
        binding.tilEmail.isEnabled = !isLoading

        // Show/hide the progress bar and button text
        binding.pbSubmit.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSubmitReport.text = if (isLoading) "" else "Submit Report"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}