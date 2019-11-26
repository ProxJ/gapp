package com.example.myapplication.ui.dashboard

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.DatabaseHelper.Companion.DATE
import com.example.myapplication.DatabaseHelper.Companion.TABLE_DAYS
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
        viewAdapter = MyAdapter(getData(), this)


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
            (viewAdapter as MyAdapter).addItem(data, viewAdapter.itemCount)

            val intent = Intent(this.activity, ExerciseActivity::class.java)
            intent.putExtra("date", currentDate)
            startActivity(intent)
        }

        setActionBar()

        return root
    }

    private fun setActionBar() {

        val actionBarLayout = layoutInflater.inflate(
            R.layout.action_bar, null) as ViewGroup
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.customView = actionBarLayout

        val spinner: Spinner = actionBarLayout.findViewById(R.id.view_spinner)
// Create an ArrayAdapter using the string array and a default spinner layout
        setupSpinner(spinner)

        setupSearch(spinner)
    }

    private fun setupSpinner(spinner: Spinner) {

        setSpinnerToShowViewAdapter(spinner)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }

        }

    }

    private fun setSpinnerToShowWorkOuts(spinner: Spinner) {
        val adapter: ArrayAdapter<String> = SpinnerArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_dropdown_item,
            android.R.id.text1,
            resources.getStringArray(R.array.ws_arr)
        )
        spinner.adapter = adapter
    }

    private fun setSpinnerToShowViewAdapter(spinner: Spinner) {
        ArrayAdapter.createFromResource(
            this.context!!,
            R.array.vs_arr,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun setupSearch(spinner: Spinner) {


        if (Intent.ACTION_SEARCH == activity!!.intent.action) {
            val query = activity!!.intent.getStringExtra(SearchManager.QUERY)
            val c = myDB?.doQueryMatch("",query, null)
            //process Cursor and display results
        }

        val searchBar: SearchView? = activity?.findViewById(R.id.search_bar)
        val query = searchBar?.query
        println(query)

        var selectedViewPosition = -1
        searchBar?.setOnQueryTextFocusChangeListener { _ , hasFocus ->
            if (hasFocus) {
                selectedViewPosition = spinner.selectedItemPosition
                setSpinnerToShowWorkOuts(spinner)
            } else {
                searchBar.isIconified = true
                setSpinnerToShowViewAdapter(spinner)
                spinner.setSelection(if (selectedViewPosition>=0) selectedViewPosition else 0)
                selectedViewPosition = -1
            }
        }
        searchBar?.setOnQueryTextListener (object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                searchWorkouts(query, spinner)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

        })
        searchBar?.suggestionsAdapter
    }

    private fun searchWorkouts(query: CharSequence?, spinner: Spinner) {
        if (query!=null) {
            Log.d("Query", spinner.selectedItem.toString())

        }
    }

    private fun getData(): MutableList<Array<String>> {

        val data = mutableListOf<Array<String>>()

        val c: Cursor? = myDB?.doQuery("select $DATE, $TIME from $TABLE_DAYS")

        if (c != null) {
            while (c.moveToNext()) {
                data.add( arrayOf(c.getString (c.getColumnIndex (DATE)),
                    c.getString (c.getColumnIndex (TIME))))
            }
            c.close()
        }
        return data
    }

    fun deleteDay(day: String, adapterPosition: Int) {

        if (myDB?.deleteEntry(TABLE_DAYS, DATE, "'$day'")!!) {
            (viewAdapter as MyAdapter).removeItem(adapterPosition)
        } else {
            Toast.makeText(this.context,
                "failed to delete workout, try again later",
                Toast.LENGTH_SHORT).show()
        }
    }

    class SpinnerArrayAdapter(
        context: Context,
        private val resourceLayout: Int,
        textViewResourceId: Int,
        units: Array<String>
    ) :
        ArrayAdapter<String>(context, resourceLayout, textViewResourceId, units) {
        private var inflater: LayoutInflater? = null
        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            val view: View = convertView ?: inflater!!.inflate(resourceLayout, parent, false)
            val unit = getItem(position)!!
            val textView = view as TextView
            textView.text = getAbbreviation(unit)
            return view
        }

        companion object {
            fun getAbbreviation(unit: String): String {
                val units = unit.split(" ")
                var abbr = ""
                units.forEach { abbr += it[0] }
                return abbr
            }

        }
        init {
            inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        }
    }
}