SWASTH – Smart Healthcare System

SWASTH is a smart healthcare system that integrates IoT-based patient monitoring with an Android application to provide real-time health tracking, doctor-patient connectivity, and emergency assistance.

This project leverages NodeMCU with biomedical sensors for continuous monitoring and Firebase Cloud + Android app for secure data synchronization, visualization, and communication.

🚀 Features
📱 Android App (Frontend – Kotlin + Xml)

Real-time Graphs – Displays live health parameters (Heart Rate, SpO₂, Temperature, ECG) synced via Firebase.

Abnormality Alerts – Detects unusual readings and sends alerts to patients, doctors, or caretakers.

Hospital Locator – Google Maps-based locator to find nearby hospitals in emergencies.

Doctor-Patient Profiles – Secure login & profile management for both doctors and patients.

WebRTC Audio/Video Calling – Enables direct doctor-patient consultations through real-time calls.

Firebase Sync – Ensures continuous data storage, retrieval, and device-independent access.

🔧 IoT Hardware System (NodeMCU + Sensors)

MAX30102 (Pulse Oximeter Sensor) – Measures Heart Rate & SpO₂ levels.

AD8232 (ECG Sensor) – Captures ECG signals for cardiac monitoring.

Temperature Sensor – Tracks patient’s body temperature in real-time.

NodeMCU ESP8266 – Collects data from sensors and transmits to Firebase.

🏗️ System Architecture

Sensors → Collect biomedical signals (Heart Rate, SpO₂, ECG, Temperature).

NodeMCU (ESP8266) → Processes sensor data and uploads to Firebase.

Firebase Realtime Database → Acts as a cloud backend for storing & syncing health data.

Android App → Visualizes data in graphs, triggers alerts, provides hospital locator, and supports WebRTC calls.

Doctor Portal (via App) → Doctors can monitor patients, receive alerts, and provide consultation.

📸 Screenshots / Demo :


![WhatsApp Image 2025-09-06 at 16 26 18_480cb561](https://github.com/user-attachments/assets/61e1d571-92d0-49a7-8f90-cd9bf92c14e6)
![WhatsApp Image 2025-09-06 at 16 26 18_471a45d7](https://github.com/user-attachments/assets/cb7c340b-49df-4450-8aef-68424842d29f)
![WhatsApp Image 2025-09-06 at 16 26 18_79bd5751](https://github.com/user-attachments/assets/27c4a532-34cc-4d15-a9e9-112573641e69)



📊 Real-time Health Graphs

⚠️ Abnormality Alerts Screen

🏥 Hospital Locator

👨‍⚕️ Doctor-Patient Profiles

📞 Video Call (WebRTC) - Zegocloud third p

🛠️ Tech Stack
Android App

Language: Kotlin

UI Framework: Jetpack Compose

Database: Firebase Realtime Database

Realtime Communication: WebRTC, Firebase

Other: Google Maps API, Android Architecture Components

IoT Hardware

Controller: NodeMCU ESP8266

Sensors: MAX30102, AD8232, LM35/DS18B20 (Temperature)


Protocol: HTTP/MQTT for data sync to Firebase

Backend / Cloud

Firebase Realtime Database – Cloud data storage & sync

Firebase Authentication – Secure user management

Firebase Cloud Messaging (FCM) – Notifications & alerts

📡 Data Flow
[ MAX30102 / AD8232 / Temp Sensor ] 
           │
           ▼
     [ NodeMCU ESP8266 ]
           │
    WiFi → Firebase
           │
           ▼
   [ Android App (SWASTH) ]
           │
   Graphs, Alerts, Calls

🎯 Use Cases

Remote Health Monitoring – Doctors monitor patient vitals from anywhere.

Emergency Alerts – Automatic alerts when abnormal readings are detected.

Telemedicine – Patients consult doctors via in-app video/audio calls.

Preventive Healthcare – Continuous tracking to detect early health issues.

📌 Future Enhancements

AI-based health risk prediction.

Offline caching in the app for remote areas.

Multi-device doctor dashboard (web + mobile).

Integration with wearables (smartwatch, fitness bands).

👨‍💻 Contributors

Sayak Banerjee – Android App Development, Firebase Integration, WebRTC.

Team Members (Sagnik Chakraborty, Sanjana Das) – IoT Hardware, Sensor Integration.

