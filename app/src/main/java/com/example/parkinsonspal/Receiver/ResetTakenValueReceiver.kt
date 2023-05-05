package com.example.parkinsonspal.Receiver

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.parkinsonspal.Model.DataBaseHelper

class ResetTakenValueReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "ResetTakenValueReceiver.onReceive() called")
        if (intent.action == "android.intent.action.DATE_CHANGED") {
            Log.d(TAG, "Received ACTION_DATE_CHANGED intent")
            // update the database to reset the taken value to 0 for all medications
            val dbHelper = DataBaseHelper(context)
            dbHelper.resetTakenValueForAllMedications()
        }
    }
}
