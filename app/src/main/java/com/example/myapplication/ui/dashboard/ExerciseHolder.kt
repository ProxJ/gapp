package com.example.myapplication.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class ExerciseHolder(
    inflater: LayoutInflater,
    val parent: ViewGroup,
    private val activity: ExerciseActivity
) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.exercise_card, parent, false)) {
    var mGroup: TextView = itemView.findViewById(R.id.mGroup)
    var mType: TextView = itemView.findViewById(R.id.mType)
    var eType: TextView =  itemView.findViewById(R.id.eType)
    var weight: TextView = itemView.findViewById(R.id.weight)
    var reps: TextView = itemView.findViewById(R.id.reps_text)
    var rTime: TextView = itemView.findViewById(R.id.rTime)
    var time: TextView = itemView.findViewById(R.id.time)
    var date: TextView = itemView.findViewById(R.id.date)
    var comment: TextView = itemView.findViewById(R.id.comments)
    private val mainLayout: LinearLayout = itemView.findViewById(R.id.subset_container)

    fun bind(
        data: ExerciseDataContainer,
        subsets: Array<ExerciseSubsetDataContainer>
    ) {

        val inflater = activity.layoutInflater
        mGroup.text = data.mGroup
        mType.text = data.mType
        eType.text = data.eType
        weight.text = data.weight
        reps.text = data.reps
        comment.text = data.comment
        rTime.text = data.rTime
        time.text = data.time
        date.text = data.date
        mainLayout.removeAllViews()
        if (data.subSets > 0) {
            subsets.forEach {
                val subsetLayout = inflater.inflate(R.layout.exercise_card_subset, null)
                val sWeight = subsetLayout.findViewById<TextView>(R.id.subset_weight)
                val sReps= subsetLayout.findViewById<TextView>(R.id.subset_reps)
                val sRest= subsetLayout.findViewById<TextView>(R.id.subset_rTime)
                val sDescription= subsetLayout.findViewById<TextView>(R.id.subset_description)
                sWeight.text = it.weight
                sReps.text = it.reps
                sRest.text = it.rTime
                sDescription.text = it.description
                mainLayout.addView(subsetLayout, mainLayout.childCount-1)
            }
        }

        itemView.setOnClickListener {
//            activity.openAlertDialog(ExerciseDataContainer(date.text.toString(), time.text.toString(),
//                mGroup.text.toString(), mType.text.toString(), eType.text.toString(),
//                weight.text.toString(), reps.text.toString(), rTime.text.toString(),
//                comment.text.toString()), subsets ,adapterPosition)
            activity.openAlertDialog(data, subsets , adapterPosition)
        }
    }

    class ExerciseDataContainer (
        var date: String = "",
        var time: String = "",
        var mGroup: String? = "",
        var mType: String? = "",
        var eType: String? = "",
        var weight: String? = "",
        var reps: String? = "",
        var rTime: String? = "",
        var comment: String? = "",
        var subSets: Int = 0)

    class ExerciseSubsetDataContainer (
        var time: String = "",
        var weight: String = "",
        var reps: String = "",
        var rTime: String = "",
        var description: String = ""
    )

}
