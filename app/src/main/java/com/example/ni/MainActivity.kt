package com.example.ni

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ni.ui.theme.NiTheme

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var shakeThreshold = 20.0f // Adjust sensitivity for shake detection
    private var showUI by mutableStateOf(false)
    private var isCallInProgress by mutableStateOf(false) // Track if call is in progress
    private var isAnonymousReportingVisible by mutableStateOf(false) // Track if anonymous reporting form is visible

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Sensor Manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            MaterialTheme {
                if (isCallInProgress) {
                    PostCallScreen(
                        onSaferRoutesClicked = { /* Navigate to safer routes screen */ },
                        onVolunteerClicked = { /* Volunteer action */ },
                        onAnonymousReportingClicked = { isAnonymousReportingVisible = true }
                    )
                } else if (showUI) {
                    ShakeDetectedScreen(makePhoneCall = { makePhoneCall() })
                } else {
                    DefaultScreen()
                }

                if (isAnonymousReportingVisible) {
                    AnonymousReportingForm(
                        onDismiss = { isAnonymousReportingVisible = false }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val acceleration = Math.sqrt((event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]).toDouble()).toFloat()
            if (acceleration > shakeThreshold) {
                makePhoneCall() // Call immediately after shake detection
                showUI = true
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun makePhoneCall() {
        val phoneNumber = "tel:7017680425"
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            val callIntent = Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber))
            startActivity(callIntent)
            isCallInProgress = true // Update state to show post-call screen
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                makePhoneCall()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
}

@Composable
fun DefaultScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Welcome to AshSakhi App. Shake your phone to start!",
            style = TextStyle(color = Color.Black, fontSize = 20.sp)
        )
    }
}

@Composable
fun ShakeDetectedScreen(makePhoneCall: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Shake Detected!",
                style = TextStyle(color = Color.Black, fontSize = 24.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { makePhoneCall() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "Call Now", color = Color.White)
            }
        }
    }
}

@Composable
fun PostCallScreen(
    onSaferRoutesClicked: () -> Unit,
    onVolunteerClicked: () -> Unit,
    onAnonymousReportingClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Call is in Progress. What would you like to do?",
            style = TextStyle(color = Color.Black, fontSize = 24.sp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onSaferRoutesClicked,
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text(text = "Safer Routes", color = Color.White)
        }

        Button(
            onClick = onVolunteerClicked,
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text(text = "Be a Volunteer", color = Color.White)
        }

        Button(
            onClick = onAnonymousReportingClicked,
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text(text = "Anonymous Reporting", color = Color.White)
        }
    }
}

@Composable
fun AnonymousReportingForm(onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Anonymous Reporting",
            style = TextStyle(color = Color.Black, fontSize = 24.sp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Criminal Details") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("When did it happen?") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("What happened?") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { /* Submit report */ },
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text(text = "Submit Report", color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text(text = "Cancel", color = Color.White)
        }
    }
}
