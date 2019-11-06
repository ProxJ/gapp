package com.example.myapplication.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ui.dashboard.ExerciseHolder.*


class ExerciseAdapter (private var myData: Array<ExerciseDataContainer>,
                       private var mySubsetData: Array<Array<ExerciseSubsetDataContainer>>,
                       private val activity: ExerciseActivity) :
    RecyclerView.Adapter<ExerciseHolder>() {

    private lateinit var exHolder: ExerciseHolder

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseHolder {

        val inflater = LayoutInflater.from(parent.context)
        // set the view's size, margins, padding and layout parameters
        exHolder = ExerciseHolder(inflater, parent, activity)
        return exHolder
    }
    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ExerciseHolder, position: Int) {
        holder.bind(myData[position], mySubsetData[position])
    }
    // Return the size of your data (invoked by the layout manager)
    override fun getItemCount() = myData.size

    fun addItem(data: ExerciseDataContainer, subData: Array<ExerciseSubsetDataContainer>, position: Int) {
        this.myData += data
        mySubsetData += subData
        notifyItemInserted(position)
    }
    fun updateItem(data: ExerciseDataContainer, subData: Array<ExerciseSubsetDataContainer>, position: Int) {
        this.myData[position] = data
        mySubsetData[position] = subData
        notifyItemChanged(position)
    }
}