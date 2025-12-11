package com.eyepax.mvvm_app.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eyepax.mvvm_app.R
import com.eyepax.mvvm_app.model.Country
import java.text.NumberFormat
import java.util.*

class CountryAdapter :
    ListAdapter<Country, CountryAdapter.CountryViewHolder>(CountryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_country, parent, false)
        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCountryName: TextView = itemView.findViewById(R.id.tvCountryName)
        private val tvCountryCode: TextView = itemView.findViewById(R.id.tvCountryCode)
        private val tvCountryRegion: TextView = itemView.findViewById(R.id.tvCountryRegion)
        private val tvCountryCapital: TextView = itemView.findViewById(R.id.tvCountryCapital)
        private val tvCountryPopulation: TextView = itemView.findViewById(R.id.tvCountryPopulation)

        fun bind(country: Country) {
            tvCountryName.text = country.name.common
            tvCountryCode.text = "Code: ${country.code}"
            tvCountryRegion.text = "Region: ${country.region ?: "Unknown"}"

            val capital = country.capital?.firstOrNull() ?: "N/A"
            tvCountryCapital.text = "Capital: $capital"

            val population = country.population?.let {
                NumberFormat.getNumberInstance(Locale.US).format(it)
            } ?: "Unknown"
            tvCountryPopulation.text = "Population: $population"
        }
    }

    class CountryDiffCallback : DiffUtil.ItemCallback<Country>() {
        override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean =
            oldItem.code == newItem.code

        override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean =
            oldItem == newItem
    }
}
