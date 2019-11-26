package com.example.myapplication.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ui.dashboard.ExerciseHolder.*
import android.content.ContentValues
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.SystemClock
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.*
import com.example.myapplication.MainActivity.Companion.myDB
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*
import com.example.myapplication.DatabaseHelper.Companion.COMMENT
import com.example.myapplication.DatabaseHelper.Companion.DATE
import com.example.myapplication.DatabaseHelper.Companion.DESCRIPTION
import com.example.myapplication.DatabaseHelper.Companion.EType
import com.example.myapplication.DatabaseHelper.Companion.MGroup
import com.example.myapplication.DatabaseHelper.Companion.MType
import com.example.myapplication.DatabaseHelper.Companion.REPS
import com.example.myapplication.DatabaseHelper.Companion.RTime
import com.example.myapplication.DatabaseHelper.Companion.SUBSET
import com.example.myapplication.DatabaseHelper.Companion.TABLE_EXERCISE
import com.example.myapplication.DatabaseHelper.Companion.TABLE_SUBSETS
import com.example.myapplication.DatabaseHelper.Companion.TIME
import com.example.myapplication.DatabaseHelper.Companion.WEIGHT
import com.example.myapplication.R
import kotlinx.android.synthetic.main.exercise_card.view.weight
import kotlinx.android.synthetic.main.exercise_card_edit_wr.view.*
import kotlin.collections.ArrayList


class ExerciseActivity  : AppCompatActivity(){

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var date: String = ""
    private var restTime: String = "00:00"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        date = intent.getStringExtra("date")
        viewManager = LinearLayoutManager(this)
        val myData = getData()
        viewAdapter = ExerciseAdapter(myData, getSubData(myData), this)

        recyclerView = findViewById<RecyclerView>(R.id.rv).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        restBtn()
        addExercise()
    }

    private fun restBtn() {
        val restBtn: Button = findViewById(R.id.restBtn)
        val chronometer = findViewById<Chronometer>(R.id.chronometer)
        restBtn.setOnClickListener {
            if(chronometer.isVisible) {
                chronometer.visibility = View.INVISIBLE
                restBtn.text = resources.getString(R.string.rest)
                restTime = chronometer.text.toString()
                chronometer.stop()
            } else {
                restBtn.text = ""
                chronometer.visibility = View.VISIBLE
                chronometer.base = SystemClock.elapsedRealtime()
                chronometer.start()
            }
        }
//        restBtn.setOnLongClickListener {
//            setRestTime()
//            true
//        }
    }

    private fun setRestTime() {

    }

    private fun getSubData(data: ArrayList<ExerciseDataContainer>): ArrayList<Array<ExerciseSubsetDataContainer>> {
        var subData = arrayListOf<Array<ExerciseSubsetDataContainer>>()
        data.forEach {
            if(it.subSets > 0) {
                val c = myDB?.doQuery("select * from $TABLE_SUBSETS where $TIME = '${it.time}'")
                if (c != null) {
                    var myData = arrayOf<ExerciseSubsetDataContainer>()
                    while (c.moveToNext()) {
                        myData += ExerciseSubsetDataContainer(
                            it.time,
                            c.getString(c.getColumnIndex(WEIGHT)),
                            c.getString(c.getColumnIndex(REPS)),
                            c.getString(c.getColumnIndex(RTime)),
                            c.getString(c.getColumnIndex(DESCRIPTION))
                        )
                    }
                    subData.add(myData)
                    c.close()
                }
            } else subData.add(arrayOf())
        }
        return subData
    }

    private fun saveExercise(data2: ExerciseDataContainer) {
        val params = ContentValues()
        val fields = arrayOf(MGroup, MType, EType, WEIGHT,
            REPS, COMMENT, RTime, SUBSET)
        val data: Array<String?> = arrayOf(data2.mGroup, data2.mType,
            data2.eType, data2.weight, data2.reps,
            data2.comment, data2.rTime, data2.subSets.toString())
        for (i in fields.indices) {
            params.put(fields[i], data[i])
        }
        myDB?.updateDetails (TABLE_EXERCISE, params, TIME,"'${data2.time}'")
    }

    private fun getData(): ArrayList<ExerciseDataContainer> {
        var myArray: ArrayList<ExerciseDataContainer> = arrayListOf<ExerciseDataContainer>()

        val c = myDB?.doQuery("select * from $TABLE_EXERCISE where $DATE = '$date'")
        if (c != null) {
        while (c.moveToNext()) {
            myArray.add( ExerciseDataContainer(
                date,
                c.getString(c.getColumnIndex(TIME)),
                c.getString(c.getColumnIndex(MGroup)),
                c.getString(c.getColumnIndex(MType)),
                c.getString(c.getColumnIndex(EType)),
                c.getString(c.getColumnIndex(WEIGHT)),
                c.getString(c.getColumnIndex(REPS)),
                c.getString(c.getColumnIndex(RTime)),
                c.getString(c.getColumnIndex(COMMENT)),
                c.getInt(c.getColumnIndex(SUBSET))
            ))
        }
            c.close()
        }
        return myArray
    }


    private fun addExercise() {
        val addExButton: FloatingActionButton = findViewById(R.id.addExerciseFAB)
        addExButton.setOnClickListener {
            openAlertDialog(null, null, viewAdapter.itemCount)
        }
    }

    fun openAlertDialog (
        holder: ExerciseDataContainer?,
        mySubsetData: Array<ExerciseSubsetDataContainer>?,
        position: Int
    ) {
        val builder = AlertDialog.Builder(this)
        val title: TextView = setOADTitle()
        val titleLayout = LinearLayout(this)
        titleLayout.orientation = LinearLayout.HORIZONTAL
        titleLayout.addView(title)
        val delBtn = TextView(this)
        if (holder != null) titleLayout.addView(delBtn)
        builder.setCustomTitle(titleLayout)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.exercise_card_edit, null)
        val mGroup: EditText = dialogView.findViewById(R.id.mGroup)
        val mType: EditText = dialogView.findViewById(R.id.mType)
        val eType: EditText = dialogView.findViewById(R.id.eType)
        val weight: EditText = dialogView.findViewById(R.id.weight)
        val reps: EditText = dialogView.findViewById(R.id.reps)
        val rTime: EditText = dialogView.findViewById(R.id.rTime)
        val comment: EditText = dialogView.findViewById(R.id.comments)
        dialogView.findViewById<EditText>(R.id.subset_description).visibility = View.GONE
        dialogView.findViewById<ImageButton>(R.id.sSets).visibility = View.GONE
        val subsetLayout: LinearLayout = dialogView.findViewById(R.id.subset_layout)
        var subset = holder?.subSets ?: 0

        if (subset > 0) {
            subsetDialogHelper(inflater, subsetLayout, mySubsetData)
        }

        builder.setView(dialogView)

        mGroup.setText(holder?.mGroup)
        mType.setText(holder?.mType)
        eType.setText(holder?.eType)
        weight.setText(holder?.weight)
        reps.setText(holder?.reps)
        comment.setText(holder?.comment)
        rTime.setText(holder?.rTime ?: restTime)
        builder.setPositiveButton("SAVE") { _, _ ->
            val time: String = holder?.time ?: SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val data = arrayOf(date, time)
            var subsetData = arrayOf<ExerciseSubsetDataContainer>()
            if (subsetLayout.childCount > 0) {
                subset = 1
                subsetLayout.forEach {
                    subsetData += (ExerciseSubsetDataContainer(time, it.weight.text.toString(), it.reps.text.toString(), "00:00")) }
            }
            val data2 = ExerciseDataContainer(date, time,
                mGroup.text.toString(), mType.text.toString(), eType.text.toString(),
                weight.text.toString(), reps.text.toString(), rTime.text.toString(),
                comment.text.toString(), subset)
            if (holder == null) myDB?.doUpdate ("Insert into $TABLE_EXERCISE($DATE, $TIME) values (?,?)", data)
//            else updateExerciseDetails(data2)
            if (subset > 0) saveSubset(subsetData)
            if(holder == null) (viewAdapter as ExerciseAdapter).addItem(data2, subsetData, position)
            else (viewAdapter as ExerciseAdapter).updateItem(data2, subsetData, position)
            saveExercise(data2)
        }
        builder.setNeutralButton("Add Subset") { _, _ ->}

        builder.setNegativeButton("Cancel"){ dialog, _ -> dialog.cancel() }

        val dialog: AlertDialog = builder.create()
        if (holder != null) setDelBtn(dialog, delBtn, holder.time, position)
        dialog.show()

        val nBut = dialog.getButton(DialogInterface.BUTTON_NEUTRAL)
        nBut.setOnClickListener {
            subsetDialogHelper(inflater,subsetLayout, null)
        }
    }

    private fun setDelBtn(
        alertDialog: AlertDialog,
        del: TextView,
        time: String,
        pos: Int) {
        del.text = getString(R.string.delete)
        del.setTextColor(getColor(R.color.colorAccent))
        del.gravity = Gravity.END
        del.typeface = Typeface.DEFAULT_BOLD
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            weight = 1.0f
            gravity = Gravity.END
            setMargins(0, 0, 40, 0)
        }
        del.setPadding(20, 30, 20, 20)
        del.layoutParams = params

        del.setOnClickListener {
            this.let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setNegativeButton("CANCEL") { _, _ -> }
                    setMessage("Are you sure you want to delete this workout?")
                    setPositiveButton("YES") { _, _ ->
                        deleteWorkout(time, pos)
                        alertDialog.cancel()
                    }
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }
    }

    private fun deleteWorkout(time: String, pos: Int) {
        if (myDB?.deleteEntry(TABLE_EXERCISE, TIME, "'$time'")!!) {
            (viewAdapter as ExerciseAdapter).removeItem(pos)
        } else {
            Toast.makeText(this,
                "failed to delete workout, try again later",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun setOADTitle(): TextView {
        val title = TextView(this)
        title.text = getString(R.string.add_exercise)
        title.textSize = 18f
        title.typeface = Typeface.DEFAULT_BOLD
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(20, 20, 0, 0)
        title.layoutParams = params
        return title
    }

    private fun subsetDialogHelper(
        inflater: LayoutInflater,
        subsetLayout: LinearLayout,
        data: Array<ExerciseSubsetDataContainer>?
    ) {
        data?.forEach {
            val dialogViewSubset = inflater.inflate(R.layout.exercise_card_edit_wr, null)
            val sWeight = dialogViewSubset.findViewById<EditText>(R.id.weight)
            val sReps = dialogViewSubset.findViewById<EditText>(R.id.reps)
            val sRTime = dialogViewSubset.findViewById<EditText>(R.id.rTime)
            val sDesc = dialogViewSubset.findViewById<EditText>(R.id.subset_description)
            val but = dialogViewSubset.findViewById<ImageButton>(R.id.sSets)
            sWeight.setText(it.weight)
            sReps.setText(it.reps)
            sRTime.setText(it.rTime)
            sDesc.setText(it.description)
            but.setOnClickListener { subsetLayout.removeView(dialogViewSubset) }
            subsetLayout.addView(dialogViewSubset)
        }
        if (data == null) {
            val dialogViewSubset = inflater.inflate(R.layout.exercise_card_edit_wr, null)
            dialogViewSubset.findViewById<ImageButton>(R.id.sSets).setOnClickListener {
                subsetLayout.removeView(dialogViewSubset)
            }
            subsetLayout.addView(dialogViewSubset)
        }

    }

    private fun saveSubset(data: Array<ExerciseSubsetDataContainer>) {
        myDB?.deleteEntry(TABLE_SUBSETS, TIME, "'${data[0].time}'")
        data.forEach {
            val myData = arrayOf(it.time, it.weight, it.reps, it.rTime, it.description)
            myDB?.doUpdate("insert or replace into $TABLE_SUBSETS ($TIME, $WEIGHT, $REPS, $RTime, $DESCRIPTION) values (?,?,?,?,?)", myData)
        }
    }

}
