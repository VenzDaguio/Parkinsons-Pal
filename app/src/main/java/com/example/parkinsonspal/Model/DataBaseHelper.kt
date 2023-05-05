package com.example.parkinsonspal.Model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.parkinsonspal.Medication
import org.mindrot.jbcrypt.BCrypt
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

private val DataBaseName = "AppDataBase.db"
private val ver : Int = 1

class DataBaseHelper(context: Context) : SQLiteOpenHelper(context, DataBaseName, null, ver) {

    companion object {
        const val DataBaseName = "AppDataBase.db"
        const val ver = 1
        const val TABLE_PENDING = "Pending"
        const val COLUMN_ID = "Id"
        const val COLUMN_NAME = "Name"
        const val COLUMN_EMAIL = "Email"
        const val COLUMN_PASSWORD = "Password"
        const val COLUMN_USER_TYPE = "User_Type"
        const val COLUMN_TOKEN = "Token"
        const val TABLE_PATIENT = "Patients"
        const val TABLE_CARER = "Carers"
        const val TABLE_DOCTOR = "Doctors"
        const val TABLE_SYMPTOMS = "Symptoms"
        const val TABLE_MOODS = "Moods"
        const val TABLE_MEDICATIONS = "Medications"
        const val COLUMN_SYMPTOM_ID = "Symptom_Id"
        const val COLUMN_SYMPTOM_DESCRIPTION = "Description"
        const val COLUMN_START_TIME = "Start_Time"
        const val COLUMN_END_TIME = "End_Time"
        const val COLUMN_PATIENT_ID = "Patient_Id"
        const val COLUMN_DATE = "Date"
        const val COLUMN_MOOD = "Mood"
        const val COLUMN_MEDICATION_ID = "Id"
        const val COLUMN_MEDICATION_NAME = "Name"
        const val COLUMN_MEDICATION_TIME = "Time"
        const val COLUMN_MEDICATION_QUANTITY = "Quantity"
        const val COLUMN_MEDICATION_REMINDER = "Reminder"
        const val COLUMN_MEDICATION_TAKEN = "Taken"
        const val COLUMN_MEDICATION_TAKEN_DATE = "Taken_Date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // create the pending table
        db?.execSQL(
            "CREATE TABLE $TABLE_PENDING ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT NOT NULL, " +
                    "$COLUMN_EMAIL TEXT NOT NULL UNIQUE, $COLUMN_PASSWORD TEXT NOT NULL, $COLUMN_USER_TYPE TEXT, $COLUMN_TOKEN TEXT UNIQUE);"
        )

        // create the patients table
        db?.execSQL(
            "CREATE TABLE $TABLE_PATIENT ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT NOT NULL, " +
                    "$COLUMN_EMAIL TEXT NOT NULL UNIQUE, $COLUMN_PASSWORD TEXT NOT NULL, $COLUMN_USER_TYPE TEXT);"
        )

        // create the carers table
        db?.execSQL(
            "CREATE TABLE $TABLE_CARER ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT NOT NULL, " +
                    "$COLUMN_EMAIL TEXT NOT NULL UNIQUE, $COLUMN_PASSWORD TEXT NOT NULL, $COLUMN_USER_TYPE TEXT);"
        )

        // create the doctors table
        db?.execSQL(
            "CREATE TABLE $TABLE_DOCTOR ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT NOT NULL, " +
                    "$COLUMN_EMAIL TEXT NOT NULL UNIQUE, $COLUMN_PASSWORD TEXT NOT NULL, $COLUMN_USER_TYPE TEXT);"
        )

        // create the symptoms table
        db?.execSQL(
            "CREATE TABLE $TABLE_SYMPTOMS ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_PATIENT_ID INTEGER NOT NULL, " +
                    "$COLUMN_SYMPTOM_DESCRIPTION TEXT NOT NULL, $COLUMN_START_TIME TEXT NOT NULL, $COLUMN_END_TIME TEXT NOT NULL, $COLUMN_DATE TEXT NOT NULL, " +
                    "FOREIGN KEY (Patient_Id) REFERENCES $TABLE_PATIENT($COLUMN_ID));"
        )

        //create the medication table
        db?.execSQL(
            "CREATE TABLE $TABLE_MEDICATIONS ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_PATIENT_ID INTEGER NOT NULL, $COLUMN_MEDICATION_NAME TEXT NOT NULL, " +
                    "$COLUMN_MEDICATION_TIME TIME NOT NULL, $COLUMN_MEDICATION_QUANTITY INTEGER NOT NULL, " +
                    "$COLUMN_MEDICATION_TAKEN INTEGER NOT NULL, " + "$COLUMN_MEDICATION_TAKEN_DATE TEXT DEFAULT NULL, " +
                    "FOREIGN KEY ($COLUMN_PATIENT_ID) REFERENCES $TABLE_PATIENT($COLUMN_ID));"
        )


        //create the moods table
        db?.execSQL(
            "CREATE TABLE $TABLE_MOODS($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_PATIENT_ID INTEGER NOT NULL, " +
                    "$COLUMN_MOOD TEXT NOT NULL, $COLUMN_DATE TEXT NOT NULL, " +
                    "FOREIGN KEY (Patient_Id) REFERENCES $TABLE_PATIENT($COLUMN_ID));"
        )
        // create triggers
        db?.execSQL(
            "CREATE TRIGGER constraint_email BEFORE INSERT ON $TABLE_PENDING BEGIN SELECT CASE " +
                    "WHEN (NEW.$COLUMN_EMAIL NOT LIKE '%@%.%') THEN RAISE(FAIL, 'Invalid email') END; END;"
        )
        db?.execSQL(
            "CREATE TRIGGER constraint_user_type BEFORE INSERT ON $TABLE_PENDING BEGIN SELECT CASE" +
                    " WHEN (NEW.$COLUMN_USER_TYPE NOT IN ('patient','carer','doctor')) THEN RAISE(FAIL, 'Invalid user type') END; END;"
        )

    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        if (p1 < p2) {
            db?.execSQL("DROP TABLE IF EXISTS " + TABLE_PENDING)
            db?.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENT)
            db?.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCTOR)
            db?.execSQL("DROP TABLE IF EXISTS " + TABLE_CARER)
            db?.execSQL("DROP TABLE IF EXISTS" + TABLE_SYMPTOMS)
            db?.execSQL("DROP TABLE IF EXISTS" + TABLE_MEDICATIONS)
            onCreate(db)
        }
    }

    fun addPendingUser(name: String, email: String, password: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_NAME, name)
        contentValues.put(COLUMN_EMAIL, email)
        contentValues.put(COLUMN_PASSWORD, password)

        val result = db.insert("pending", null, contentValues)
        return result != (-1).toLong()
    }

    fun addPatient(patient: PatientModel) : Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        val hashedPassword = BCrypt.hashpw(patient.password, BCrypt.gensalt())

        contentValues.put(COLUMN_NAME, patient.name)
        contentValues.put(COLUMN_EMAIL, patient.email)
        contentValues.put(COLUMN_PASSWORD, hashedPassword)
        contentValues.put(COLUMN_USER_TYPE, "Patient")

        val result = db.insert(TABLE_PATIENT, null, contentValues)
        return result != (-1).toLong()
    }

    fun addCarer(carer: CarerModel): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        val hashedPassword = BCrypt.hashpw(carer.password, BCrypt.gensalt())

        contentValues.put(COLUMN_NAME, carer.name)
        contentValues.put(COLUMN_EMAIL, carer.email)
        contentValues.put(COLUMN_PASSWORD, hashedPassword)
        contentValues.put(COLUMN_USER_TYPE, "Carer")

        val result = db.insert(TABLE_CARER, null, contentValues)
        return result != (-1).toLong()
    }
     fun addDoctor(doctor: DoctorModel): Boolean {
         val db = this.writableDatabase
         val contentValues = ContentValues()

         val hashedPassword = BCrypt.hashpw(doctor.password, BCrypt.gensalt())

         contentValues.put(COLUMN_NAME, doctor.name)
         contentValues.put(COLUMN_EMAIL, doctor.email)
         contentValues.put(COLUMN_PASSWORD, hashedPassword)
         contentValues.put(COLUMN_USER_TYPE, "Doctor")

         val result = db.insert(TABLE_DOCTOR, null, contentValues)
         return result != (-1).toLong()
     }



    fun checkLogInPatient(email: String, password: String) : Boolean {
        val db:SQLiteDatabase = this.readableDatabase
        val sqlStatement = "SELECT * FROM $TABLE_PATIENT WHERE $COLUMN_EMAIL = ?"
        val selectionArgs = arrayOf(email)

        val cursor: Cursor = db.rawQuery(sqlStatement, selectionArgs)

        val cursorCount = cursor.count

        if (cursorCount == 0) {
            cursor.close()
            return false
        }

        cursor.moveToFirst()
        val hashedPassword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
        cursor.close()

        return BCrypt.checkpw(password, hashedPassword)
    }

    fun checkLogInCarer(email: String, password: String) : Boolean {
        val db:SQLiteDatabase = this.readableDatabase
        val sqlStatement = "SELECT * FROM $TABLE_CARER WHERE $COLUMN_EMAIL = ?"
        val selectionArgs = arrayOf(email)

        val cursor: Cursor = db.rawQuery(sqlStatement, selectionArgs)

        val cursorCount = cursor.count

        if (cursorCount == 0) {
            cursor.close()
            return false
        }

        cursor.moveToFirst()
        val hashedPassword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
        cursor.close()

        return BCrypt.checkpw(password, hashedPassword)
    }

    fun checkLogInDoctor(email: String, password: String) : Boolean {
        val db:SQLiteDatabase = this.readableDatabase
        val sqlStatement = "SELECT * FROM $TABLE_DOCTOR WHERE $COLUMN_EMAIL = ?"
        val selectionArgs = arrayOf(email)

        val cursor: Cursor = db.rawQuery(sqlStatement, selectionArgs)

        val cursorCount = cursor.count

        if (cursorCount == 0) {
            cursor.close()
            return false
        }

        cursor.moveToFirst()
        val hashedPassword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
        cursor.close()

        return BCrypt.checkpw(password, hashedPassword)
    }

    fun getPatientId(email: String): Int {
        val query = "SELECT $COLUMN_ID FROM $TABLE_PATIENT WHERE $COLUMN_EMAIL = '$email'"
        val cursor = writableDatabase.rawQuery(query, null)
        var patientId = 0
        if (cursor.moveToFirst()) {
            patientId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        }
        cursor.close()
        return patientId
    }

    fun getCarerId(email: String): Int {
        val query = "SELECT $COLUMN_ID FROM $TABLE_CARER WHERE $COLUMN_EMAIL = '$email'"
        val cursor = writableDatabase.rawQuery(query, null)
        var carerId = 0
        if (cursor.moveToFirst()) {
            carerId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        }
        cursor.close()
        return carerId
    }

    fun getDoctorId(email: String): Int {
        val query = "SELECT $COLUMN_ID FROM $TABLE_DOCTOR WHERE $COLUMN_EMAIL = '$email'"
        val cursor = writableDatabase.rawQuery(query, null)
        var doctorId = 0
        if (cursor.moveToFirst()) {
            doctorId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        }
        cursor.close()
        return doctorId
    }

    fun insertSymptom(patientId: Int, description: String, startTime: LocalTime?, endTime: LocalTime?, date: LocalDate): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PATIENT_ID, patientId)
            put(COLUMN_SYMPTOM_DESCRIPTION, description)
            put(COLUMN_START_TIME, startTime.toString())
            put(COLUMN_END_TIME, endTime.toString())
            put(COLUMN_DATE, date.toString())
        }
        return db.insert(TABLE_SYMPTOMS, null, values)
    }

    fun insertMedication(patientId: Int, name: String, time: LocalTime, quantity: Int, taken: Boolean) : Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PATIENT_ID, patientId)
            put(COLUMN_MEDICATION_NAME, name)
            put(COLUMN_MEDICATION_TIME, time.toString())
            put(COLUMN_MEDICATION_QUANTITY, quantity)
            put(COLUMN_MEDICATION_TAKEN, taken)
        }
        return db.insert(TABLE_MEDICATIONS, null, values)
    }

    fun getSymptomsForPatient(patientId: Int, date: LocalDate): List<Symptom> {
        val db = this.readableDatabase
        val symptoms = mutableListOf<Symptom>()
        val query = "SELECT $COLUMN_SYMPTOM_DESCRIPTION, $COLUMN_START_TIME, $COLUMN_END_TIME FROM $TABLE_SYMPTOMS WHERE $COLUMN_PATIENT_ID = ? AND $COLUMN_DATE = ?"
        val cursor = db.rawQuery(query, arrayOf(patientId.toString(), date.toString()))
        if (cursor.moveToFirst()) {
            do {
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SYMPTOM_DESCRIPTION))
                val startTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME))
                val endTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME))
                val symptom = Symptom(description, startTime, endTime)
                symptoms.add(symptom)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return symptoms
    }

    fun getRecordedDatesForPatient(patientId: Int): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        val query = "SELECT DISTINCT $COLUMN_DATE FROM $TABLE_SYMPTOMS WHERE $COLUMN_PATIENT_ID = $patientId"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val dateString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                val date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                dates.add(date)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return dates
    }

    fun getMedicationForPatient(patientId: Int) : List<Medication> {
        val db = this.readableDatabase
        val medications = mutableListOf<Medication>()
        val query = "SELECT $COLUMN_MEDICATION_ID, $COLUMN_MEDICATION_NAME, $COLUMN_MEDICATION_TIME, $COLUMN_MEDICATION_QUANTITY,$COLUMN_MEDICATION_TAKEN, $COLUMN_MEDICATION_TAKEN_DATE FROM $TABLE_MEDICATIONS WHERE $COLUMN_PATIENT_ID = ? "
        val cursor = db.rawQuery(query, arrayOf(patientId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MEDICATION_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDICATION_NAME))
                val timeString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDICATION_TIME))
                val time = LocalTime.parse(timeString)
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MEDICATION_QUANTITY))
                val taken = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MEDICATION_TAKEN))
                val takenDate = cursor.getString(cursor.getColumnIndexOrThrow(
                    COLUMN_MEDICATION_TAKEN_DATE))
                val medication = Medication(id, name, time, quantity, taken, takenDate)
                medications.add(medication)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return medications
    }

    fun insertOrUpdateMood(patientId: Int, mood: String, date: LocalDate): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PATIENT_ID, patientId)
            put(COLUMN_MOOD, mood)
            put(COLUMN_DATE, date.toString())
        }

        val selection = "$COLUMN_PATIENT_ID=? AND $COLUMN_DATE=?"
        val selectionArgs = arrayOf(patientId.toString(), date.toString())

        val cursor = db.query(
            TABLE_MOODS,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            // Record already exists for this patient and date, update it
            db.update(TABLE_MOODS, values, selection, selectionArgs)
            cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
        } else {
            // Record doesn't exist for this patient and date, insert it
            db.insert(TABLE_MOODS, null, values)
        }
    }

    fun getMoodForPatient(patientId: Int, date: LocalDate): String? {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_MOOD FROM $TABLE_MOODS WHERE $COLUMN_PATIENT_ID = ? AND $COLUMN_DATE = ?"
        val cursor = db.rawQuery(query, arrayOf(patientId.toString(), date.toString()))
        var mood: String? = null
        if (cursor.moveToFirst()) {
            mood = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOOD))
        }
        cursor.close()
        return mood
    }

    fun getDatesWithEntriesForPatient(patientId: Int): List<LocalDate> {
        val datesWithEntries = mutableListOf<LocalDate>()
        val db = readableDatabase
        val query = "SELECT DISTINCT date FROM $TABLE_SYMPTOMS WHERE patient_id = ?"
        val selectionArgs = arrayOf(patientId.toString())
        val cursor = db.rawQuery(query, selectionArgs)

        with(cursor) {
            while (moveToNext()) {
                val dateString = getString(getColumnIndexOrThrow("entry_date"))
                val date = LocalDate.parse(dateString)
                datesWithEntries.add(date)
            }
            close()
        }
        return datesWithEntries
    }

    fun updateMedicationTaken(patientId: Int, medicationName: String, taken: Int, takenDate: String?) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MEDICATION_TAKEN, taken)
            put(COLUMN_MEDICATION_TAKEN_DATE, takenDate)
        }
        db.update(
            TABLE_MEDICATIONS,
            values,
            "$COLUMN_PATIENT_ID = ? AND $COLUMN_MEDICATION_NAME = ?",
            arrayOf(patientId.toString(), medicationName)
        )
    }

    fun editMedication(patientId: Int, medicationId: Int, name: String, time: LocalTime, quantity: Int) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_MEDICATION_NAME, name)
            put(COLUMN_MEDICATION_TIME, time.toString())
            put(COLUMN_MEDICATION_QUANTITY, quantity)
        }
        db.update(
            TABLE_MEDICATIONS,
            contentValues,
            "$COLUMN_PATIENT_ID = ? AND $COLUMN_MEDICATION_ID = ?",
            arrayOf(patientId.toString(), medicationId.toString())
        )
        db.close()
    }

    fun deleteMedication(medicationId: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_MEDICATIONS, "$COLUMN_MEDICATION_ID = ?", arrayOf(medicationId.toString()))
        db.close()
    }

    fun resetTakenValueForAllMedications() {
        val db = this.writableDatabase
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.format(Date())
        val values = ContentValues().apply {
            put(COLUMN_MEDICATION_TAKEN, 0)
            put(COLUMN_MEDICATION_TAKEN_DATE, null as String?)
        }
        db.update(TABLE_MEDICATIONS, values, null, null)
    }


}
