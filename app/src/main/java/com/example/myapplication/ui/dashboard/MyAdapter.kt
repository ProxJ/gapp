package com.example.myapplication.ui.dashboard

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R



class MyAdapter(private var myData: Array<Array<String>>, private val context: Context?) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup, private val context: Context?) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.exercise_day, parent, false)) {
        private var day: TextView? = null
        private var time: TextView? = null
        init {
            day = itemView.findViewById(R.id.date)
            time = itemView.findViewById(R.id.time)
        }

        fun bind(data: Array<String>) {
            day?.text = data[0]
            time?.text = data[1]
            itemView.setOnClickListener {
                openDay(data[0])
            }
        }

        private fun openDay(date: String) {
            val intent = Intent(context, ExerciseActivity::class.java)
            intent.putExtra("date", date)
            context?.startActivity(intent)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        // set the view's size, margins, padding and layout parameters

        return MyViewHolder(inflater, parent, context)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(myData[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myData.size

    fun addItem(data: Array<String>, position: Int) {
        this.myData += data
        notifyDataSetChanged()
//        notifyItemInserted(position)
    }
}
