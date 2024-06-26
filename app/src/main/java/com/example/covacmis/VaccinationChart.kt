package com.example.covacmis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView

class VaccinationChart : AppCompatActivity() ,RecyclerViewReadyListener,OnLoadMoreListener{

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<DataClass>
    private lateinit var vaccineName: ArrayList<String>
    private lateinit var ageGroup: ArrayList<String>
    private lateinit var overlayContainer: FrameLayout
    private lateinit var myAdapter: AdapterClass
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var userInfo:User
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vaccination_chart)


        overlayContainer = findViewById(R.id.overlayContainer)
        overlayContainer.visibility = View.GONE

        userInfo = intent.getSerializableExtra("user") as User

        vaccineName = arrayListOf()

        ageGroup = arrayListOf()

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutVaccine)
        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.setOnRefreshListener {
            val intent = Intent(this,VaccinationChart::class.java)
            intent.putExtra("user",userInfo)
            startActivity(intent)
            swipeRefreshLayout.isRefreshing = false
        }
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        dataList = arrayListOf<DataClass>()
        getVaccines()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_home

        bottomNavigationView.setOnNavigationItemSelectedListener {
                menuItem ->
            when (menuItem.itemId) {
                R.id.bottom_home -> {
                    true
                }
                R.id.bottom_completed -> {
                    val intent = Intent(this, Completed::class.java).putExtra("user",userInfo)
                    startActivity(intent)
                    true
                }
                R.id.bottom_Placed -> {
                    val intent = Intent(this, OrdersPlaced::class.java).putExtra("user",userInfo)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }


    private fun getVaccines() {
        dataList.clear()
        val url = "https://covacmis.onrender.com/vaccines"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                for (key in response.keys()) {
                    val vaccineData = response.getJSONObject(key)
                    val ageGroupList = vaccineData.optJSONArray("Age_group")

                    val ageGroupString = when {
                        ageGroupList == null -> "any"
                        ageGroupList.length() == 1 -> ">${ageGroupList.getDouble(0)}"
                        ageGroupList.getDouble(0) == ageGroupList.getDouble(1) -> "${ageGroupList.getDouble(0)}"
                        else -> "${ageGroupList.getDouble(0)} - ${ageGroupList.getDouble(1)}"
                    }

                    val userVaccineData = userInfo.vaccines[key]
//                    println(userVaccineData)
                    val userInfoDoseCount = if (userVaccineData is Map<*, *>) {
                        (userVaccineData["dose_count"] as Double)
                    } else {
                        0.0
                    }
                    val doseCount = vaccineData.optInt("dose_count").toDouble() - userInfoDoseCount

                    if (doseCount > 0) {
                        val doseCountString = doseCount.toString()
                        val dataClass = DataClass(key, ageGroupString, doseCountString)
                        dataList.add(dataClass)
                    }
                    else{
                        continue
                    }
                }

                myAdapter = AdapterClass(dataList,userInfo,overlayContainer,this)
                recyclerView.adapter = myAdapter
            },
            { error ->
                Log.d("VaccinationChart", error.toString())
            })

        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    override fun onLoadMore() {
        fetchMoreData()
    }

    private fun fetchMoreData() {
        dataList.addAll(dataList)
        myAdapter.notifyDataSetChanged()
        myAdapter.setLoaded()
    }

    override fun onRecyclerViewReady() {
        navigateToVaccinationDetailScreen()
    }

    private fun navigateToVaccinationDetailScreen() {
        val intent = Intent(this, VaccineDetail::class.java)
        // Pass necessary data to VaccineDetail activity
        startActivity(intent)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finishAffinity()
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press again to exit", Toast.  LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

}
