package com.example.myapplication.ui.dashboard

import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.DatabaseHelper.Companion.TABLE_DAYS
import com.example.myapplication.DatabaseHelper.Companion.DATE
import com.example.myapplication.DatabaseHelper.Companion.TIME
import com.example.myapplication.MainActivity.Companion.myDB
import com.example.myapplication.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var dashboardViewModel: DashboardViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)


        viewManager = LinearLayoutManager(this.context)
        viewAdapter = MyAdapter(getData(), this.activity)


        recyclerView = root.findViewById<RecyclerView>(R.id.rv).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }

        val startButton: FloatingActionButton = root.findViewById(R.id.start_exercise)
        startButton.setOnClickListener {

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val currentDate = sdf.format(Date())
            val data = arrayOf<String>(currentDate, "0")
            myDB?.doUpdate ("Insert into $TABLE_DAYS($DATE, $TIME) values (?,?);", data)
            (viewAdapter as MyAdapter).addItem(data, 0)

            val intent = Intent(this.activity, ExerciseActivity::class.java)
            intent.putExtra("date", currentDate)
            startActivity(intent)
        }

        return root
    }

    private fun getData(): Array<Array<String>> {

        var data = arrayOf<Array<String>>()

        val c: Cursor? = myDB?.doQuery("select $DATE, $TIME from $TABLE_DAYS")

        if (c != null) {
            while (c.moveToNext()) {
                data += arrayOf(c.getString (c.getColumnIndex (DATE)),
                    c.getString (c.getColumnIndex (TIME)))
            }
            c.close()
        }
        return data
    }
}