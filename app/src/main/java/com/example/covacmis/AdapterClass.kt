package com.example.covacmis

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class AdapterClass(private val dataList: ArrayList<DataClass>, private var userInfo: User,private val overlayContainer:FrameLayout,private val recyclerViewReadyListener: RecyclerViewReadyListener) :
    RecyclerView.Adapter<AdapterClass.ViewHolderClass>() {
    private var isLoading = false
    private var onLoadMoreListener: OnLoadMoreListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
//        holder.rvAge.text = currentItem.ageGroup
//        holder.rvVaccine.text = currentItem.dataVaccineName
//        holder.rvDoseCount.text = currentItem.doseCount
        holder.bindData(currentItem)

        if (position == dataList.size - 1 && !isLoading) {
            onLoadMoreListener?.onLoadMore()
            isLoading = true
        }

    }

    fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener
    }

    fun setLoaded() {
        isLoading = false
    }

    inner class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvVaccine: TextView = itemView.findViewById(R.id.VaccineName)
        val rvAge: TextView = itemView.findViewById(R.id.Age)
        val rvDoseCount: TextView = itemView.findViewById(R.id.remDose)
//        private val rvAddDoseButton: ImageButton = itemView.findViewById(R.id.imageButton)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                val clickedItem = dataList[position]
                val intent = Intent(itemView.context, VaccineDetail::class.java)
                intent.putExtra("clickedItem", clickedItem)
                intent.putExtra("userDetail",userInfo)
                itemView.context.startActivity(intent)
            }

//            rvAddDoseButton.setOnClickListener {
//                val position = adapterPosition
//                val clickedItem = dataList[position]
//                overlayContainer.visibility = View.VISIBLE
//                rvAddDoseButton.isEnabled = false
//                // Assuming you have a username stored in userInfo
//                val username = userInfo.username
//                val vaccineName = clickedItem.dataVaccineName
//                val newDoseCount = clickedItem.doseCount.toDouble() - 1.0
//                clickedItem.doseCount = newDoseCount.toString()
//
//                val vaccines = userInfo.vaccines.toMutableMap()
//                if (vaccines.containsKey(vaccineName)) {
//                    val vaccineData = vaccines[vaccineName] as MutableMap<String, Any>
//                    vaccineData["dose_count"] = (vaccineData["dose_count"] as? Int ?: 0) + 1
//                    vaccineData["date_of_vaccination"] = "-"
//                } else {
//                    // Create a new map for the vaccine if it doesn't exist
//                    val newVaccineData = mapOf(
//                        "dose_count" to 1,
//                        "date_of_vaccination" to "-"
//                    )
//                    vaccines[vaccineName] = newVaccineData
//                }
//                userInfo = userInfo.copy(vaccines = vaccines)
//
//                // Update the UI first
//
//                if (newDoseCount <= 0) {
//                    dataList.removeAt(position)
//                    notifyItemRemoved(position)
//                } else {
//                    // Notify the adapter that the data has changed
//                    notifyItemChanged(position)
//                }
//
//                // Send a request to your API
//                val requestQueue = Volley.newRequestQueue(itemView.context)
//                val url = "https://covacmis.onrender.com/vaccinate"
//                val requestBody = JSONObject()
//                requestBody.put("username", username)
//                requestBody.put("name", vaccineName)
//                println(requestBody)
//
//                val request = JsonObjectRequest(
//                    Request.Method.POST, url, requestBody,
//                    { response ->
//                        if (response.getInt("success") == 1) {
//                            Toast.makeText(
//                                itemView.context,
//                                "$vaccineName Vaccination successful!",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        } else {
//                            Toast.makeText(
//                                itemView.context,
//                                "Failed to register your vaccination of $vaccineName. Please try again.",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                        overlayContainer.visibility = View.GONE
//                        rvAddDoseButton.isEnabled = true
//                    },
//                    { error ->
//                        Log.d("AdapterClass", error.toString())
//                    })

//                requestQueue.add(request)
            }
        fun bindData(data: DataClass) {
            rvVaccine.text = data.dataVaccineName
            rvAge.text = data.ageGroup
            rvDoseCount.text = data.doseCount
        }
        }
    }

interface OnLoadMoreListener {
    fun onLoadMore()
}