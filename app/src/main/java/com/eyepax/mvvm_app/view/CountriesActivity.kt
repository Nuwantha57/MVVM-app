package com.eyepax.mvvm_app.view

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eyepax.mvvm_app.R
import com.eyepax.mvvm_app.util.Resource
import com.eyepax.mvvm_app.viewmodel.CountryViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CountriesActivity : AppCompatActivity() {

    private val viewModel: CountryViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvStatus: TextView
    private lateinit var fabRefresh: FloatingActionButton
    private lateinit var adapter: CountryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_countries)

        // Set title
        title = "Countries (Room + REST)"

        initViews()
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewCountries)
        progressBar = findViewById(R.id.progressBar)
        tvStatus = findViewById(R.id.tvStatus)
        fabRefresh = findViewById(R.id.fabRefresh)
    }

    private fun setupRecyclerView() {
        adapter = CountryAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        // Observe countries from Room database
        viewModel.countries.observe(this) { countries ->
            if (countries.isNotEmpty()) {
                adapter.submitList(countries)
                tvStatus.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            } else {
                tvStatus.text = "No countries available. Pull to refresh."
                tvStatus.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe refresh status
        viewModel.refreshStatus.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    Toast.makeText(
                        this,
                        "Countries updated successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Error -> {
                    Toast.makeText(
                        this,
                        "Error: ${result.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                is Resource.Loading -> {
                    // Already handled by isLoading observer
                }
            }
        }
    }

    private fun setupListeners() {
        fabRefresh.setOnClickListener {
            viewModel.refreshCountries()
        }
    }
}
