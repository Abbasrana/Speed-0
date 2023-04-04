package com.basindevapp.speed_0.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.basindevapp.speed_0.R

class ThresholdAdapter(val onThresholdSelected: (value: Int) -> Unit) :
    RecyclerView.Adapter<ThresholdAdapter.ThresholdviewHolder>() {

    private val list: ArrayList<Int> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThresholdviewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.threshold_item, parent, false)
        return ThresholdviewHolder(view)
    }


    fun updateItems(currencyList: List<Int>) {
        list.clear()
        list.addAll(currencyList)
        notifyDataSetChanged()
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ThresholdviewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    inner class ThresholdviewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(value: Int, position: Int) {
            var textview = view.findViewById<TextView>(R.id.item)
            textview.text = value.toString()
            textview.setOnClickListener {
                onThresholdSelected(textview.text.toString().toInt())
            }
        }
    }
}