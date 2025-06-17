//package com.example.swasth.fragments
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import com.example.swasth.R
//import com.example.swasth.VideoCallActivity
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import com.jjoe64.graphview.GraphView
//import com.jjoe64.graphview.series.DataPoint
//import com.jjoe64.graphview.series.LineGraphSeries
//
//data class HealthData(
//    val bpm: Int = 0,
//    val heartRate: Int = 0,
//    val spo2: Int = 0,
//    val temperatureC: Double = 0.0,
//    val temperatureF: Double = 0.0
//)
//
//class HomeFragment : Fragment() {
//
//    private lateinit var videoCallButton: ImageView
//
//    private lateinit var healthValue: TextView   // For bpm
//    private lateinit var heartValue: TextView    // For heart rate
//    private lateinit var oxygenValue: TextView   // For spo2
//    private lateinit var tempValue: TextView     // For temperatureC
//
//    private lateinit var databaseReference: DatabaseReference
//    private lateinit var heartRateChart: GraphView
//    private lateinit var heartRateSeries: LineGraphSeries<DataPoint>
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_home, container, false)
//
//        // Initialize UI elements
//        videoCallButton = view.findViewById(R.id.videoCallButton)
//        healthValue = view.findViewById(R.id.healthValue)
//        heartValue = view.findViewById(R.id.heartValue)
//        oxygenValue = view.findViewById(R.id.oxygenValue)
//        tempValue = view.findViewById(R.id.tempValue)
//
//        heartRateChart = view.findViewById(R.id.heartRateChart)
//        heartRateSeries = LineGraphSeries()
//        heartRateChart.addSeries(heartRateSeries)
//        heartRateChart.title = "Heart Rate History"
//        heartRateChart.gridLabelRenderer.horizontalAxisTitle = "Time"
//        heartRateChart.gridLabelRenderer.verticalAxisTitle = "BPM"
//
//        // Set initial placeholder values
//        healthValue.text = "--"
//        heartValue.text = "--"
//        oxygenValue.text = "--"
//        tempValue.text = "--"
//
//        // Firebase reference to "healthData"
//        databaseReference = FirebaseDatabase.getInstance().getReference("healthData")
//
//        // Realtime listener for health data
//        databaseReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val data = snapshot.getValue(HealthData::class.java)
//                data?.let {
//                    healthValue.text = if (it.bpm != 0) "${it.bpm}" else "--"
//                    heartValue.text = if (it.heartRate != 0) "${it.heartRate}" else "--"
//                    oxygenValue.text = if (it.spo2 != 0) "${it.spo2}" else "--"
//                    tempValue.text = if (it.temperatureC != 0.0) "${it.temperatureC}°C" else "--"
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Optional: Log or show error
//            }
//        })
//// Listen for heart rate history from Firebase
//        val heartRateHistoryRef = FirebaseDatabase.getInstance().getReference("heartRateHistory")
//        heartRateHistoryRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val dataPoints = mutableListOf<DataPoint>()
//                var x = 0.0
//                for (data in snapshot.children) {
//                    val hr = data.getValue(Int::class.java) ?: continue
//                    dataPoints.add(DataPoint(x, hr.toDouble()))
//                    x += 1.0
//                }
//                heartRateSeries.resetData(dataPoints.toTypedArray())
//            }
//            override fun onCancelled(error: DatabaseError) {}
//        })
//        // Video call button click
//        videoCallButton.setOnClickListener {
//            val intent = Intent(requireContext(), VideoCallActivity::class.java)
//            startActivity(intent)
//        }
//
//        return view
//    }
//}



package com.example.swasth.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.swasth.R
import com.example.swasth.VideoCallActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.text.SimpleDateFormat
import java.util.*
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

data class HealthData(
    val bpm: Long = 0,
    val heartRate: Long = 0,
    val spo2: Long = 0,
    val temperatureC: Double = 0.0,
    val temperatureF: Double = 0.0,
    val Sp02Valid: Long = 0
)

data class HistoricalHealthData(
    val bpm: Long = 0,
    val heartRate: Long = 0,
    val spo2: Long = 0,
    val temperatureC: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

class HomeFragment : Fragment() {

    private lateinit var videoCallButton: ImageView
    private lateinit var healthValue: TextView   // For bpm
    private lateinit var heartValue: TextView    // For heart rate
    private lateinit var oxygenValue: TextView   // For spo2
    private lateinit var tempValue: TextView     // For temperatureC

    private lateinit var databaseReference: DatabaseReference
    private lateinit var historyReference: DatabaseReference
    private lateinit var heartRateChart: GraphView
    private lateinit var heartRateSeries: LineGraphSeries<DataPoint>

    // Local storage for graph data (alternative to Firebase history)
    private val heartRateHistory = mutableListOf<DataPoint>()
    private var dataPointCounter = 0.0
    private fun showHealthNotification(isGood: Boolean, bpm: Long, heartRate: Long, spo2: Long, tempC: Double) {
        val channelId = "health_status_channel"
        val channelName = "Health Status"
        val notificationId = 1

        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val status = if (isGood) "Good" else "Good but needs attention"
        val message = "BPM: $bpm\nHeart Rate: $heartRate\nSpO₂: $spo2\nTemp: ${String.format("%.1f", tempC)}°C"

        val builder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.ic_heart) // Use your app icon or a health icon
            .setContentTitle("Your health data is $status")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
    }

    private fun isHealthDataGood(bpm: Long, heartRate: Long, spo2: Long, tempC: Double): Boolean {
        // Example thresholds, adjust as needed
        return bpm in 60..100 && heartRate in 60..100 && spo2 >= 95 && tempC in 33.0..37.5
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize UI elements
        videoCallButton = view.findViewById(R.id.videoCallButton)
        healthValue = view.findViewById(R.id.healthValue)
        heartValue = view.findViewById(R.id.heartValue)
        oxygenValue = view.findViewById(R.id.oxygenValue)
        tempValue = view.findViewById(R.id.tempValue)

        // Initialize chart
        setupHeartRateChart(view)

        // Set initial placeholder values
        healthValue.text = "--"
        heartValue.text = "--"
        oxygenValue.text = "--"
        tempValue.text = "--"

        // Firebase references
        databaseReference = FirebaseDatabase.getInstance().getReference("healthData")
        historyReference = FirebaseDatabase.getInstance().getReference("healthHistory")

        // Listen for real-time health data
        setupHealthDataListener()

        // Listen for historical data (if exists)
        setupHistoryListener()

        // Video call button click
        videoCallButton.setOnClickListener {
            val intent = Intent(requireContext(), VideoCallActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun setupHeartRateChart(view: View) {
        heartRateChart = view.findViewById(R.id.heartRateChart)
        heartRateSeries = LineGraphSeries()

        // Configure chart
        heartRateChart.addSeries(heartRateSeries)
        heartRateChart.title = "Heart Rate History"
        heartRateChart.gridLabelRenderer.horizontalAxisTitle = "Time"
        heartRateChart.gridLabelRenderer.verticalAxisTitle = "BPM"

        // Style the series
        heartRateSeries.color = resources.getColor(android.R.color.holo_red_light)
        heartRateSeries.thickness = 8
        heartRateSeries.isDrawDataPoints = true
        heartRateSeries.dataPointsRadius = 10f

        // Set viewport to be scrollable and scalable
        heartRateChart.viewport.isScrollable = true
        heartRateChart.viewport.isScalable = true
        heartRateChart.viewport.setScalableY(true)
        heartRateChart.viewport.setScrollableY(true)

        // Set Y-axis bounds for heart rate (40-200 BPM)
        heartRateChart.viewport.setMinY(40.0)
        heartRateChart.viewport.setMaxY(200.0)
        heartRateChart.viewport.isYAxisBoundsManual = true

        // Set grid lines
        heartRateChart.gridLabelRenderer.isHorizontalLabelsVisible = true
        heartRateChart.gridLabelRenderer.isVerticalLabelsVisible = true
        heartRateChart.gridLabelRenderer.gridStyle = com.jjoe64.graphview.GridLabelRenderer.GridStyle.BOTH
    }

    private fun setupHealthDataListener() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("HomeFragment", "Health data received: ${snapshot.value}")

                try {
                    // Parse data manually to handle Long values
                    val bpm = snapshot.child("bpm").getValue(Long::class.java) ?: 0L
                    val heartRate = snapshot.child("heartRate").getValue(Long::class.java) ?: 0L
                    val spo2 = snapshot.child("spo2").getValue(Long::class.java) ?: 0L
                    val temperatureC = snapshot.child("temperatureC").getValue(Double::class.java) ?: 0.0

                    // Update UI
                    healthValue.text = if (bpm > 0) "$bpm" else "--"
                    heartValue.text = if (heartRate > 0) "$heartRate" else "--"
                    oxygenValue.text = if (spo2 > 0 && spo2 != -999L) "$spo2%" else "--"
                    tempValue.text = if (temperatureC > 0) "${String.format("%.1f", temperatureC)}°C" else "--"

                    val isGood = isHealthDataGood(bpm, heartRate, spo2, temperatureC)
                    showHealthNotification(isGood, bpm, heartRate, spo2, temperatureC)

                    // Add to graph if heart rate is valid
                    if (heartRate > 0 && heartRate < 300) { // Reasonable heart rate range
                        addDataPointToGraph(heartRate.toDouble())

                        // Optional: Save to Firebase history
                        saveToHistory(bpm, heartRate, spo2, temperatureC)
                    }

                } catch (e: Exception) {
                    Log.e("HomeFragment", "Error parsing health data: ${e.message}", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Health data error: ${error.message}")
            }
        })
    }

    private fun setupHistoryListener() {
        // Listen for existing historical data
        historyReference.limitToLast(50).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("HomeFragment", "History data received: ${snapshot.childrenCount} items")

                val dataPoints = mutableListOf<DataPoint>()
                var x = 0.0

                for (data in snapshot.children.sortedBy { it.key }) {
                    try {
                        val heartRate = data.child("heartRate").getValue(Long::class.java)
                        if (heartRate != null && heartRate > 0 && heartRate < 300) {
                            dataPoints.add(DataPoint(x, heartRate.toDouble()))
                            x += 1.0
                        }
                    } catch (e: Exception) {
                        Log.e("HomeFragment", "Error parsing history data: ${e.message}")
                    }
                }

                if (dataPoints.isNotEmpty()) {
                    heartRateHistory.clear()
                    heartRateHistory.addAll(dataPoints)
                    dataPointCounter = x
                    updateGraph()
                    Log.d("HomeFragment", "Loaded ${dataPoints.size} historical data points")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "History data error: ${error.message}")
            }
        })
    }

    private fun addDataPointToGraph(heartRate: Double) {
        // Add new data point
        heartRateHistory.add(DataPoint(dataPointCounter, heartRate))
        dataPointCounter += 1.0

        // Keep only last 50 points for performance
        if (heartRateHistory.size > 50) {
            heartRateHistory.removeAt(0)
            // Re-index remaining points
            heartRateHistory.forEachIndexed { index, dataPoint ->
                heartRateHistory[index] = DataPoint(index.toDouble(), dataPoint.y)
            }
            dataPointCounter = heartRateHistory.size.toDouble()
        }

        updateGraph()
        Log.d("HomeFragment", "Added heart rate: $heartRate, total points: ${heartRateHistory.size}")
    }

    private fun updateGraph() {
        if (heartRateHistory.isNotEmpty()) {
            try {
                // Update graph data
                heartRateSeries.resetData(heartRateHistory.toTypedArray())

                // Adjust viewport to show all data
                if (heartRateHistory.size > 1) {
                    heartRateChart.viewport.setMinX(0.0)
                    heartRateChart.viewport.setMaxX((heartRateHistory.size - 1).toDouble())
                    heartRateChart.viewport.isXAxisBoundsManual = true
                }

                // Force refresh
                heartRateChart.onDataChanged(true, true)

                Log.d("HomeFragment", "Graph updated successfully with ${heartRateHistory.size} points")
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error updating graph: ${e.message}", e)
            }
        }
    }

    private fun saveToHistory(bpm: Long, heartRate: Long, spo2: Long, temperatureC: Double) {
        // Optional: Save to Firebase for persistence across app restarts
        val timestamp = System.currentTimeMillis()
        val historyData = HistoricalHealthData(bpm, heartRate, spo2, temperatureC, timestamp)

        historyReference.child(timestamp.toString()).setValue(historyData)
            .addOnSuccessListener {
                Log.d("HomeFragment", "Data saved to history")
            }
            .addOnFailureListener { e ->
                Log.e("HomeFragment", "Failed to save history: ${e.message}")
            }
    }
}