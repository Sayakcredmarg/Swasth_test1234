package com.example.swasth

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig

class VideoCallActivity : AppCompatActivity() {

    companion object {
        // Replace with your actual ZEGO App ID and App Sign
        private const val APP_ID: Long = 495570697L // Your ZEGO App ID
        private const val APP_SIGN = "d6e6176e106250d8adb809264920bd0438a1278504ef3fcf44eb740b0787c8bc" // Your ZEGO App Sign
        private const val DEFAULT_CALL_ID = "12345"
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    private var editUserId: EditText? = null
    private var editUserName: EditText? = null
    private var editCallId: EditText? = null
    private var btnStartCall: Button? = null
    private var btnJoinCall: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if this is a direct call (from intent data) or setup screen
        val userID = intent.getStringExtra("user_id")
        val userName = intent.getStringExtra("user_name")
        val callID = intent.getStringExtra("call_id")

        if (userID != null && userName != null && callID != null) {
            // Direct call - check permissions first
            if (checkPermissions()) {
                // Make sure setContentView is called first with fragment container layout
                setContentView(R.layout.activity_call_setup)
                startVideoCall(userID, userName, callID)
            } else {
                // Show setup screen first, then request permissions
                setContentView(R.layout.activity_video_call_setup)
                initializeViews()
                setupClickListeners()
                // Set the values from intent
                editUserId?.setText(userID)
                editUserName?.setText(userName)
                editCallId?.setText(callID)
                // Request permissions immediately
                requestPermissions()
            }
        } else {
            // Setup screen - show input fields
            setContentView(R.layout.activity_video_call_setup)
            initializeViews()
            setupClickListeners()
        }
    }

    private fun initializeViews() {
        editUserId = findViewById(R.id.edit_user_id)
        editUserName = findViewById(R.id.edit_user_name)
        editCallId = findViewById(R.id.edit_call_id)
        btnStartCall = findViewById(R.id.btn_start_call)
        btnJoinCall = findViewById(R.id.btn_join_call)

        // Set default values only if fields are empty
        if (editUserId?.text?.toString()?.isEmpty() == true) {
            editUserId?.setText("user_${System.currentTimeMillis()}")
        }
        if (editUserName?.text?.toString()?.isEmpty() == true) {
            editUserName?.setText("User")
        }
        if (editCallId?.text?.toString()?.isEmpty() == true) {
            editCallId?.setText(DEFAULT_CALL_ID)
        }
    }

    private fun setupClickListeners() {
        btnStartCall?.setOnClickListener {
            if (validateInputs()) {
                if (checkPermissions()) {
                    switchToCallView()
                } else {
                    requestPermissions()
                }
            }
        }

        btnJoinCall?.setOnClickListener {
            if (validateInputs()) {
                if (checkPermissions()) {
                    switchToCallView()
                } else {
                    requestPermissions()
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val userId = editUserId?.text.toString().trim()
        val userName = editUserName?.text.toString().trim()
        val callId = editCallId?.text.toString().trim()

        if (userId.isEmpty()) {
            editUserId?.error = "User ID is required"
            editUserId?.requestFocus()
            return false
        }
        if (userName.isEmpty()) {
            editUserName?.error = "User Name is required"
            editUserName?.requestFocus()
            return false
        }
        if (callId.isEmpty()) {
            editCallId?.error = "Call ID is required"
            editCallId?.requestFocus()
            return false
        }
        return true
    }

    private fun switchToCallView() {
        val userId = editUserId?.text.toString().trim() ?: ""
        val userName = editUserName?.text.toString().trim() ?: ""
        val callId = editCallId?.text.toString().trim() ?: ""

        // Switch to fragment layout with container - THIS IS THE KEY FIX
        setContentView(R.layout.activity_call_setup) // This layout has fragment_container
        startVideoCall(userId, userName, callId)
    }

    private fun checkPermissions(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

        // Check if we should show rationale
        val shouldShowRationale = permissions.any {
            ActivityCompat.shouldShowRequestPermissionRationale(this, it)
        }

        if (shouldShowRationale) {
            Toast.makeText(
                this,
                "ক্যামেরা এবং মাইক্রোফোন অনুমতি ভিডিও কলের জন্য প্রয়োজন",
                Toast.LENGTH_LONG
            ).show()
        }

        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permissions granted, proceed with call
                if (editUserId != null) {
                    // From setup screen
                    if (validateInputs()) {
                        switchToCallView()
                    }
                } else {
                    // From direct intent - make sure we have the call layout
                    setContentView(R.layout.activity_call_setup) // Fragment container layout
                    val userID = intent.getStringExtra("user_id") ?: "default_user"
                    val userName = intent.getStringExtra("user_name") ?: "User"
                    val callID = intent.getStringExtra("call_id") ?: DEFAULT_CALL_ID
                    startVideoCall(userID, userName, callID)
                }
            } else {
                Toast.makeText(
                    this,
                    "ক্যামেরা এবং মাইক্রোফোন অনুমতি ভিডিও কলের জন্য আবশ্যক",
                    Toast.LENGTH_LONG
                ).show()

                // Don't finish immediately, let user try again
                // finish()
            }
        }
    }

    private fun startVideoCall(userID: String, userName: String, callID: String) {
        try {
            android.util.Log.d("VideoCall", "Starting video call with UserID: $userID, UserName: $userName, CallID: $callID")
            Toast.makeText(this, "Starting video call", Toast.LENGTH_SHORT).show()

            // Make sure setContentView is called first
            // setContentView(R.layout.activity_video_call) - Already called before this method

            // Verify fragment container exists
            val container = findViewById<FrameLayout>(R.id.fragment_container)
            val invitationConfig = ZegoUIKitPrebuiltCallInvitationConfig()
            if (container == null) {
                android.util.Log.e("VideoCall", "Fragment container not found!")
                Toast.makeText(this, "Fragment container not found", Toast.LENGTH_SHORT).show()
                return
            }
            android.util.Log.d("VideoCall", "Fragment container found successfully")
            Toast.makeText(this, "Fragment container found", Toast.LENGTH_SHORT).show()

            // Initialize ZEGO service first
            android.util.Log.d("VideoCall", "Initializing ZEGO service...")
            Toast.makeText(this, "Initializing ZEGO service", Toast.LENGTH_SHORT).show()
            ZegoUIKitPrebuiltCallService.init(
                application,
                APP_ID,
                APP_SIGN,
                userID,
                userName,
                invitationConfig
            )
            android.util.Log.d("VideoCall", "ZEGO service initialized")
            Toast.makeText(this, "ZEGO service initialized", Toast.LENGTH_SHORT).show()

            // Create call configuration
            val config = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall()
            android.util.Log.d("VideoCall", "Call config created")
            Toast.makeText(this, "Call config created", Toast.LENGTH_SHORT).show()

            // Create fragment with proper parameters
            android.util.Log.d("VideoCall", "Creating ZEGO fragment...")
            Toast.makeText(this, "Creating ZEGO fragment", Toast.LENGTH_SHORT).show()
            val fragment = ZegoUIKitPrebuiltCallFragment.newInstance(
                APP_ID,
                APP_SIGN,
                userID,
                userName,
                callID,
                config
            )
            android.util.Log.d("VideoCall", "Fragment created successfully")
            Toast.makeText(this, "Fragment created successfully", Toast.LENGTH_SHORT).show()

            // Then add fragment with matching ID
            android.util.Log.d("VideoCall", "Adding fragment to container...")
            Toast.makeText(this, "Adding fragment to container", Toast.LENGTH_SHORT).show()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment) // Must match XML ID
                .commitNow()
            android.util.Log.d("VideoCall", "Fragment transaction committed")
            Toast.makeText(this, "Video call started", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            android.util.Log.e("VideoCall", "Error starting video call: ${e.message}", e)
            e.printStackTrace()
            Toast.makeText(this, "Failed to start video call: ${e.message}", Toast.LENGTH_LONG).show()
            // Don't finish immediately, let user try again
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up ZEGO service when activity is destroyed
        try {
            ZegoUIKitPrebuiltCallService.unInit()
        } catch (e: Exception) {
            android.util.Log.e("VideoCall", "Error uninitializing ZEGO service", e)
        }
    }

    override fun onBackPressed() {
        // Handle back press gracefully
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}

//package com.example.swasth
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import android.widget.FrameLayout
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig
//import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment
//import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
//
//class VideoCallActivity : AppCompatActivity() {
//
//    companion object {
//        // Replace with your actual ZEGO App ID and App Sign
//        private const val APP_ID: Long = 495570697L // Your ZEGO App ID
//        private const val APP_SIGN = "d6e6176e106250d8adb809264920bd0438a1278504ef3fcf44eb740b0787c8bc" // Your ZEGO App Sign
//        private const val DEFAULT_CALL_ID = "12345"
//        private const val PERMISSION_REQUEST_CODE = 1001
//    }
//
//    private var editUserId: EditText? = null
//    private var editUserName: EditText? = null
//    private var editCallId: EditText? = null
//    private var btnStartCall: Button? = null
//    private var btnJoinCall: Button? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Check if this is a direct call (from intent data) or setup screen
//        val userID = intent.getStringExtra("user_id")
//        val userName = intent.getStringExtra("user_name")
//        val callID = intent.getStringExtra("call_id")
//
//        if (userID != null && userName != null && callID != null) {
//            // Direct call - check permissions first
//            if (checkPermissions()) {
//                setContentView(R.layout.activity_video_call_setup)
//                startVideoCall(userID, userName, callID)
//            } else {
//                // Show setup screen first, then request permissions
//                setContentView(R.layout.activity_video_call_setup)
//                initializeViews()
//                setupClickListeners()
//                // Set the values from intent
//                editUserId?.setText(userID)
//                editUserName?.setText(userName)
//                editCallId?.setText(callID)
//                // Request permissions immediately
//                requestPermissions()
//            }
//        } else {
//            // Setup screen - show input fields
//            setContentView(R.layout.activity_video_call_setup)
//            initializeViews()
//            setupClickListeners()
//        }
//    }
//
//    private fun initializeViews() {
//        editUserId = findViewById(R.id.edit_user_id)
//        editUserName = findViewById(R.id.edit_user_name)
//        editCallId = findViewById(R.id.edit_call_id)
//        btnStartCall = findViewById(R.id.btn_start_call)
//        btnJoinCall = findViewById(R.id.btn_join_call)
//
//        // Set default values only if fields are empty
//        if (editUserId?.text?.toString()?.isEmpty() == true) {
//            editUserId?.setText("user_${System.currentTimeMillis()}")
//        }
//        if (editUserName?.text?.toString()?.isEmpty() == true) {
//            editUserName?.setText("User")
//        }
//        if (editCallId?.text?.toString()?.isEmpty() == true) {
//            editCallId?.setText(DEFAULT_CALL_ID)
//        }
//    }
//
//    private fun setupClickListeners() {
//        btnStartCall?.setOnClickListener {
//            if (validateInputs()) {
//                if (checkPermissions()) {
//                    switchToCallView()
//                } else {
//                    requestPermissions()
//                }
//            }
//        }
//
//        btnJoinCall?.setOnClickListener {
//            if (validateInputs()) {
//                if (checkPermissions()) {
//                    switchToCallView()
//                } else {
//                    requestPermissions()
//                }
//            }
//        }
//    }
//
//    private fun validateInputs(): Boolean {
//        val userId = editUserId?.text.toString().trim()
//        val userName = editUserName?.text.toString().trim()
//        val callId = editCallId?.text.toString().trim()
//
//        if (userId.isEmpty()) {
//            editUserId?.error = "User ID is required"
//            editUserId?.requestFocus()
//            return false
//        }
//        if (userName.isEmpty()) {
//            editUserName?.error = "User Name is required"
//            editUserName?.requestFocus()
//            return false
//        }
//        if (callId.isEmpty()) {
//            editCallId?.error = "Call ID is required"
//            editCallId?.requestFocus()
//            return false
//        }
//        return true
//    }
//
//    private fun switchToCallView() {
//        val userId = editUserId?.text.toString().trim() ?: ""
//        val userName = editUserName?.text.toString().trim() ?: ""
//        val callId = editCallId?.text.toString().trim() ?: ""
//
//        // Switch to fragment layout with container
//        setContentView(R.layout.activity_video_call_setup)
//        startVideoCall(userId, userName, callId)
//    }
//
//    private fun checkPermissions(): Boolean {
//        val permissions = arrayOf(
//            Manifest.permission.CAMERA,
//            Manifest.permission.RECORD_AUDIO
//        )
//
//        return permissions.all {
//            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
//        }
//    }
//
//    private fun requestPermissions() {
//        val permissions = arrayOf(
//            Manifest.permission.CAMERA,
//            Manifest.permission.RECORD_AUDIO
//        )
//
//        // Check if we should show rationale
//        val shouldShowRationale = permissions.any {
//            ActivityCompat.shouldShowRequestPermissionRationale(this, it)
//        }
//
//        if (shouldShowRationale) {
//            Toast.makeText(
//                this,
//                "ক্যামেরা এবং মাইক্রোফোন অনুমতি ভিডিও কলের জন্য প্রয়োজন",
//                Toast.LENGTH_LONG
//            ).show()
//        }
//
//        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
//                // Permissions granted, proceed with call
//                if (editUserId != null) {
//                    // From setup screen
//                    if (validateInputs()) {
//                        switchToCallView()
//                    }
//                } else {
//                    // From direct intent - make sure we have the call layout
//                    setContentView(R.layout.activity_video_call_setup)
//                    val userID = intent.getStringExtra("user_id") ?: "default_user"
//                    val userName = intent.getStringExtra("user_name") ?: "User"
//                    val callID = intent.getStringExtra("call_id") ?: DEFAULT_CALL_ID
//                    startVideoCall(userID, userName, callID)
//                }
//            } else {
//                Toast.makeText(
//                    this,
//                    "ক্যামেরা এবং মাইক্রোফোন অনুমতি ভিডিও কলের জন্য আবশ্যক",
//                    Toast.LENGTH_LONG
//                ).show()
//
//                // Don't finish immediately, let user try again
//                // finish()
//            }
//        }
//    }
//
//    private fun startVideoCall(userID: String, userName: String, callID: String) {
//        try {
//            android.util.Log.d("VideoCall", "Starting video call with UserID: $userID, UserName: $userName, CallID: $callID")
//
//            // Verify fragment container exists
//            val container = findViewById<FrameLayout>(R.id.fragment_container)
//            if (container == null) {
//                android.util.Log.e("VideoCall", "Fragment container not found!")
//                Toast.makeText(this, "Fragment container not found", Toast.LENGTH_SHORT).show()
//                return
//            }
//            android.util.Log.d("VideoCall", "Fragment container found successfully")
//
//            // Initialize ZEGO service first
//            android.util.Log.d("VideoCall", "Initializing ZEGO service...")
//            ZegoUIKitPrebuiltCallService.init(
//                application,
//                APP_ID,
//                APP_SIGN,
//                userID,
//                userName,
//                null
//            )
//            android.util.Log.d("VideoCall", "ZEGO service initialized")
//
//            // Create call configuration
//            val config = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall()
//            android.util.Log.d("VideoCall", "Call config created")
//
//            // Create fragment with proper parameters
//            android.util.Log.d("VideoCall", "Creating ZEGO fragment...")
//            val fragment = ZegoUIKitPrebuiltCallFragment.newInstance(
//                APP_ID,
//                APP_SIGN,
//                userID,
//                userName,
//                callID,
//                config
//            )
//            android.util.Log.d("VideoCall", "Fragment created successfully")
//
//            // Add fragment to container
//            android.util.Log.d("VideoCall", "Adding fragment to container...")
//            val transaction = supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.fragment_container, fragment)
//            transaction.commitAllowingStateLoss() // Use commitAllowingStateLoss to avoid state loss issues
//            android.util.Log.d("VideoCall", "Fragment transaction committed")
//
//        } catch (e: Exception) {
//            android.util.Log.e("VideoCall", "Error starting video call: ${e.message}", e)
//            e.printStackTrace()
//            Toast.makeText(this, "Failed to start video call: ${e.message}", Toast.LENGTH_LONG).show()
//            // Don't finish immediately, let user try again
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        // Clean up ZEGO service when activity is destroyed
//        try {
//            ZegoUIKitPrebuiltCallService.unInit()
//        } catch (e: Exception) {
//            android.util.Log.e("VideoCall", "Error uninitializing ZEGO service", e)
//        }
//    }
//
//    override fun onBackPressed() {
//        // Handle back press gracefully
//        if (supportFragmentManager.backStackEntryCount > 0) {
//            supportFragmentManager.popBackStack()
//        } else {
//            super.onBackPressed()
//        }
//    }
//}







//package com.example.swasth
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig
//import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment
//import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
//
//class VideoCallActivity : AppCompatActivity() {
//
//    companion object {
//        // Replace with your actual ZEGO App ID and App Sign
//        private const val APP_ID: Long = 495570697L // Your ZEGO App ID
//        private const val APP_SIGN = "d6e6176e106250d8adb809264920bd0438a1278504ef3fcf44eb740b0787c8bc" // Your ZEGO App Sign
//        private const val DEFAULT_CALL_ID = "12345"
//        private const val PERMISSION_REQUEST_CODE = 1001
//    }
//
//    private lateinit var editUserId: EditText
//    private lateinit var editUserName: EditText
//    private lateinit var editCallId: EditText
//    private lateinit var btnStartCall: Button
//    private lateinit var btnJoinCall: Button
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Check if this is a direct call (from intent data) or setup screen
//        val userID = intent.getStringExtra("user_id")
//        val userName = intent.getStringExtra("user_name")
//        val callID = intent.getStringExtra("call_id")
//
//        if (userID != null && userName != null && callID != null) {
//            // Direct call - switch to call layout
//            setContentView(R.layout.activity_video_call)
//            if (checkPermissions()) {
//                startVideoCall(userID, userName, callID)
//            } else {
//                requestPermissions()
//            }
//        } else {
//            // Setup screen - show input fields
//            setContentView(R.layout.activity_video_call)
//            initializeViews()
//            setupClickListeners()
//        }
//    }
//
//    private fun initializeViews() {
//        editUserId = findViewById(R.id.edit_user_id)
//        editUserName = findViewById(R.id.edit_user_name)
//        editCallId = findViewById(R.id.edit_call_id)
//        btnStartCall = findViewById(R.id.btn_start_call)
//        btnJoinCall = findViewById(R.id.btn_join_call)
//
//        // Set default values
//        editUserId.setText("user_${System.currentTimeMillis()}")
//        editUserName.setText("User")
//        editCallId.setText(DEFAULT_CALL_ID)
//    }
//
//    private fun setupClickListeners() {
//        btnStartCall.setOnClickListener {
//            if (validateInputs()) {
//                if (checkPermissions()) {
//                    switchToCallView()
//                } else {
//                    requestPermissions()
//                }
//            }
//        }
//
//        btnJoinCall.setOnClickListener {
//            if (validateInputs()) {
//                if (checkPermissions()) {
//                    switchToCallView()
//                } else {
//                    requestPermissions()
//                }
//            }
//        }
//    }
//
//    private fun validateInputs(): Boolean {
//        val userId = editUserId.text.toString().trim()
//        val userName = editUserName.text.toString().trim()
//        val callId = editCallId.text.toString().trim()
//
//        if (userId.isEmpty()) {
//            editUserId.error = "User ID is required"
//            return false
//        }
//        if (userName.isEmpty()) {
//            editUserName.error = "User Name is required"
//            return false
//        }
//        if (callId.isEmpty()) {
//            editCallId.error = "Call ID is required"
//            return false
//        }
//        return true
//    }
//
//    private fun switchToCallView() {
//        val userId = editUserId.text.toString().trim()
//        val userName = editUserName.text.toString().trim()
//        val callId = editCallId.text.toString().trim()
//
//        // Switch to fragment layout
//        setContentView(R.layout.activity_video_call)
//        startVideoCall(userId, userName, callId)
//    }
//
//    private fun checkPermissions(): Boolean {
//        val permissions = arrayOf(
//            Manifest.permission.CAMERA,
//            Manifest.permission.RECORD_AUDIO
//        )
//
//        return permissions.all {
//            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
//        }
//    }
//
//    private fun requestPermissions() {
//        val permissions = arrayOf(
//            Manifest.permission.CAMERA,
//            Manifest.permission.RECORD_AUDIO
//        )
//        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
//                // Permissions granted, proceed with call
//                if (::editUserId.isInitialized) {
//                    // From setup screen
//                    switchToCallView()
//                } else {
//                    // From direct intent
//                    val userID = intent.getStringExtra("user_id") ?: "default_user"
//                    val userName = intent.getStringExtra("user_name") ?: "User"
//                    val callID = intent.getStringExtra("call_id") ?: DEFAULT_CALL_ID
//                    startVideoCall(userID, userName, callID)
//                }
//            } else {
//                Toast.makeText(this, "Camera and microphone permissions are required for video calls", Toast.LENGTH_LONG).show()
//                finish()
//            }
//        }
//    }
//
//    private fun startVideoCall(userID: String, userName: String, callID: String) {
//        try {
//            // Initialize ZEGO service first
//            ZegoUIKitPrebuiltCallService.init(
//                application,
//                APP_ID,
//                APP_SIGN,
//                userID,
//                userName,
//                null
//            )
//
//            // Create call configuration
//            val config = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall()
//
//            // Create fragment with proper parameters
//            val fragment = ZegoUIKitPrebuiltCallFragment.newInstance(
//                APP_ID,
//                APP_SIGN,
//                userID,
//                userName,
//                callID,
//                config
//            )
//
//            // Add fragment to container
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, fragment)
//                .commitNow()
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Toast.makeText(this, "Failed to start video call: ${e.message}", Toast.LENGTH_LONG).show()
//            finish()
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        // Clean up ZEGO service when activity is destroyed
//        try {
//            ZegoUIKitPrebuiltCallService.unInit()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//}