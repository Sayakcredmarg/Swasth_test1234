package com.example.swasth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swasth.R
import com.example.swasth.model.Medication
import com.example.swasth.repository.MedicationAdapter

class MedicationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MedicationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_medication, container, false)

        recyclerView = view.findViewById(R.id.rvMedications)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val emergencyMeds = listOf(
            Medication("Paracetamol", "Fever, mild pain", "500mg every 6-8 hrs", "Use only when temperature exceeds 100°F."),
            Medication("ORS Solution", "Dehydration", "1 packet in 1L water", "Prescribed by Dr. A. Sen (AIIMS)"),
            Medication("Cetirizine", "Allergy, cold", "10mg once a day", "Avoid if drowsiness affects you"),
            Medication("Ibuprofen", "Muscle pain", "400mg every 8 hours", "Use with food to prevent acidity"),
            Medication("Loperamide", "Diarrhea", "2mg after each loose stool", "Do not exceed 8mg/day"),
            Medication("Domperidone", "Nausea, vomiting", "10mg before meals", "Avoid in case of heart issues"),
            Medication("Ranitidine", "Acidity", "150mg twice daily", "Avoid long-term use"),
            Medication("Amoxicillin", "Bacterial infections", "500mg every 8 hours", "Complete full course"),
            Medication("Azithromycin", "Throat infection", "500mg once daily for 3 days", "Take 1 hour before or 2 hours after food"),
            Medication("Salbutamol Inhaler", "Asthma", "1–2 puffs every 4–6 hrs", "Shake well before use"),
            Medication("Metronidazole", "Diarrhea due to infection", "400mg thrice a day", "Avoid alcohol"),
            Medication("Betadine Gargle", "Throat infection", "Gargle 2–3 times daily", "Do not swallow"),
            Medication("Calamine Lotion", "Skin rash", "Apply 2–3 times daily", "Do not apply on open wounds"),
            Medication("Clotrimazole Cream", "Fungal infection", "Apply twice daily", "Continue for 2 weeks"),
            Medication("Antacid Gel", "Acidity, heartburn", "1–2 tsp after meals", "Shake before use"),
            Medication("Dicyclomine", "Abdominal cramps", "10mg before meals", "Avoid in elderly"),
            Medication("Hydrocortisone Cream", "Skin allergy", "Apply thin layer twice daily", "Avoid long-term use"),
            Medication("Neosporin Powder", "Wound infection", "Sprinkle once or twice daily", "Do not inhale"),
            Medication("Chlorpheniramine", "Cold, allergy", "4mg every 4–6 hrs", "May cause drowsiness"),
            Medication("Dexamethasone", "Severe allergy", "0.5–2mg per dose", "Use under doctor supervision"),
            Medication("Vitamin C", "Immunity boost", "500–1000mg daily", "Take after meals"),
            Medication("Zinc Sulphate", "Viral illness", "20mg once daily", "Do not exceed 2 weeks"),
            Medication("Pantoprazole", "Acidity, gastritis", "40mg before breakfast", "Avoid long-term use"),
            Medication("Aspirin", "Mild pain, fever", "300–600mg every 4–6 hrs", "Avoid in children under 16"),
            Medication("Mupirocin Ointment", "Skin wounds", "Apply 2–3 times/day", "Avoid eyes and mucous membranes"),
            Medication("Levocetirizine", "Allergy", "5mg at bedtime", "Non-drowsy alternative to cetirizine"),
            Medication("Sodium Chloride Nasal Drops", "Nasal congestion", "2–3 drops in each nostril", "Safe for infants too"),
            Medication("Naproxen", "Menstrual cramps", "250–500mg twice daily", "Take with food"),
            Medication("Cough Syrup (Dextromethorphan)", "Dry cough", "5–10ml every 6 hrs", "Not for productive cough"),
            Medication("Expectorant Syrup", "Wet cough", "5–10ml thrice a day", "Drink warm water"),
            Medication("Throat Lozenges", "Sore throat", "1 lozenge every 2–3 hrs", "Do not chew or swallow whole"),
            Medication("ORS + Zinc Kit", "Child diarrhea", "1 ORS + 1 zinc daily", "Complete full 14-day zinc course"),
            Medication("Paracetamol Infant Drops", "Fever in babies", "10–15mg/kg/dose", "Use dropper carefully"),
            Medication("Ivermectin", "Parasitic infections", "200mcg/kg once daily", "Use only under doctor guidance"),
            Medication("B-complex Tablets", "Weakness, fatigue", "1 tablet daily", "Take after meals"),
            Medication("Amlodipine", "High BP", "5–10mg once daily", "Take at the same time every day"),
            Medication("Glucose Powder", "Fatigue, weakness", "Mix 1 tbsp in water", "Avoid in diabetic patients"),
            Medication("Dextrose IV", "Severe hypoglycemia", "As per IV drip rate", "Hospital use only"),
            Medication("Adrenaline Auto-Injector", "Anaphylaxis", "0.3mg IM injection", "Emergency use only"),
            Medication("Atorvastatin", "High cholesterol", "10mg once at night", "Monitor liver enzymes"),
            Medication("Calcium Tablets", "Bone health", "500mg twice daily", "Take with Vitamin D"),
            Medication("Vitamin D3", "Deficiency", "600–2000 IU/day", "Take after meals"),
            Medication("Glimepiride", "Type 2 diabetes", "1–2mg daily before breakfast", "Monitor sugar levels"),
            Medication("Insulin Injection", "Type 1 or 2 diabetes", "As per doctor’s dosage", "Refrigerate properly"),
            Medication("Multivitamin Syrup", "Nutritional supplement", "10ml once daily", "Shake well before use"),
            Medication("Iron-Folic Acid Tablets", "Anemia", "Once daily after food", "May cause black stools"),
            Medication("Nystatin Drops", "Oral thrush", "Use as directed", "Shake before use"),
            Medication("Cotrimoxazole", "UTI, chest infection", "1 tablet twice daily", "Ensure hydration"),
            Medication("Magnesium Hydroxide", "Constipation", "10–15ml before bed", "Short-term use only"),
            Medication("Digene Tabs", "Gas, acidity", "Chew 1–2 tabs after meals", "Do not exceed 6/day"),
            Medication("Miconazole Cream", "Fungal skin infection", "Apply thinly twice daily", "Continue for 1 week after symptoms resolve"),
            Medication("Silver Sulfadiazine Cream", "Burn wounds", "Apply 1-2 times daily", "Keep wound covered"),
            Medication("Ondansetron", "Severe nausea, vomiting", "4-8mg every 8 hours", "Can be taken sublingually"),
            Medication("Prednisolone", "Severe inflammation, allergy", "5-60mg daily", "Taper dose as per doctor's advice"),
            Medication("Metformin", "Type 2 Diabetes", "500mg twice daily with meals", "Monitor kidney function"),
            Medication("Gliclazide", "Type 2 Diabetes", "40-80mg before breakfast", "Risk of hypoglycemia"),
            Medication("Telmisartan", "High BP", "40-80mg once daily", "Monitor potassium levels"),
            Medication("Clopidogrel", "Blood thinner", "75mg once daily", "Risk of bleeding"),
            Medication("Warfarin", "Blood thinner", "Dose adjusted based on INR", "Regular blood tests required"),
            Medication("Furosemide", "Fluid retention, edema", "20-40mg once or twice daily", "Monitor electrolytes"),
            Medication("Spironolactone", "Heart failure, high BP", "25-100mg daily", "Can cause high potassium"),
            Medication("Nitrofurantoin", "Urinary Tract Infection", "100mg twice daily for 5-7 days", "Take with food"),
            Medication("Ciprofloxacin Eye Drops", "Bacterial conjunctivitis", "1-2 drops every 2-4 hours", "Do not touch dropper tip to eye"),
            Medication("Timolol Eye Drops", "Glaucoma", "1 drop twice daily", "Press on tear duct after application"),
            Medication("Saline Nasal Spray", "Nasal dryness, congestion", "Spray 1-2 times in each nostril as needed", "Gentle relief"),
            Medication("Lactulose Solution", "Constipation", "15-30ml once or twice daily", "May cause bloating initially"),
            Medication("Bisacodyl Tablets", "Constipation", "5-10mg at bedtime", "For occasional use"),
            Medication("Activated Charcoal", "Poisoning, overdose", "50-100g as a single dose", "Administer as soon as possible"),
            Medication("Promethazine", "Motion sickness, nausea", "25mg 30 mins before travel", "May cause significant drowsiness"),
            Medication("Permethrin Cream", "Scabies, lice", "Apply thoroughly from neck down, wash off after 8-14 hours", "Treat all household contacts")
        )

        adapter = MedicationAdapter(emergencyMeds)
        recyclerView.adapter = adapter

        return view
    }
}