package edtech.teamlease.calender.ui


import android.Manifest.permission.READ_CALENDAR
import android.Manifest.permission.WRITE_CALENDAR
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import edtech.teamlease.calender.R
import edtech.teamlease.calender.databinding.ActivityEventBinding
import edtech.teamlease.calender.viewmodels.ViewModel
import edtech.teamlease.trainingportal.util.myToast
import android.view.View
import android.content.ContentUris
import android.content.ContentValues.TAG
import android.util.Log
import kotlin.properties.Delegates


/**
 * Created by Vipin Shrivatri on 16-11-2021.
 */


class EventActivity : AppCompatActivity(), ViewModel.ViewListener {

    private val callbackId = 42
    var eventID by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        val binding: ActivityEventBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_event)
        val viewModel = ViewModelProviders.of(this).get(ViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel.viewListener = this

        checkPermission(callbackId, READ_CALENDAR, WRITE_CALENDAR)
//        readEvents()

        specificEvent()
    }

    private fun checkPermission(callbackId: Int, vararg permissionsId: String) {
        var permissions = true
        for (p in permissionsId) {
            permissions =
                permissions && ContextCompat.checkSelfPermission(this, p) == PERMISSION_GRANTED
        }
        if (!permissions) ActivityCompat.requestPermissions(this, permissionsId, callbackId)
    }


    private fun addToCalender(
        context: Context,
        title: String,
        desc: String,
        needReminder: Boolean,
        needMailService: Boolean
    ) {

        val calID: Long = 3
        var startMillis: Long = 0
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        var startDate: Date? = null

        try {
            startDate = simpleDateFormat.parse("30-11-2021 11:15:00")
            assert(startDate != null)
            startMillis = startDate.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val tz = TimeZone.getDefault()

        val cr = contentResolver
        val values = ContentValues()
        values.put(CalendarContract.Events.DTSTART, startMillis)
        val endDate: Long = startMillis + 1000 * 60 * 60
        values.put(CalendarContract.Events.DTEND, endDate)
        values.put(CalendarContract.Events.TITLE, title)
        values.put(CalendarContract.Events.DESCRIPTION, desc)
        values.put(CalendarContract.Events.CALENDAR_ID, calID)
        values.put(
            CalendarContract.Events.AVAILABILITY,
            CalendarContract.Events.AVAILABILITY_BUSY
        )

        values.put(CalendarContract.Events.EVENT_TIMEZONE, tz.id)
        val uri: Uri? = cr.insert(CalendarContract.Events.CONTENT_URI, values)
        val eventUriString = "content://com.android.calendar/events"


        val eventUri: Uri = context.getApplicationContext().getContentResolver()
            .insert(Uri.parse(eventUriString), values)!!
        eventID = uri?.lastPathSegment!!.toLong()

        if (needReminder) {
            /***************** Event: Reminder(with alert) Adding reminder to event  */
            val reminderUriString = "content://com.android.calendar/reminders"
            val reminderValues = ContentValues()
            reminderValues.put("event_id", eventID)
            reminderValues.put("minutes", 5) // Default value of the
            // system. Minutes is a
            // integer
            reminderValues.put("method", 1) // Alert Methods: Default(0),
            // Alert(1), Email(2),````````````````
            // SMS(3)
            val reminderUri: Uri = this.getApplicationContext().getContentResolver()
                .insert(Uri.parse(reminderUriString), reminderValues)!!
        }


        if (needMailService) {
            val attendeuesesUriString = "content://com.android.calendar/attendees"

//            //Guest 1
//            val attendeesValues = ContentValues()
//            attendeesValues.put("event_id", eventID)
//            attendeesValues.put("attendeeName", "Sachin Yadav") // Attendees name
//            attendeesValues.put("attendeeEmail", "sachin.yadav@teamlease.com") // Attendee
//            attendeesValues.put("attendeeRelationship", 0) // Relationship_Attendee(1),
//            attendeesValues.put("attendeeType", 0) // None(0), Optional(1),
//            attendeesValues.put("attendeeStatus", 0) // NOne(0), Accepted(1),
//            val attendeuesesUri1: Uri = this.getApplicationContext().getContentResolver()
//                .insert(Uri.parse(attendeuesesUriString), attendeesValues)!!

            //Guest 2
            val attendeesValues2 = ContentValues()
            attendeesValues2.put("event_id", eventID)
            attendeesValues2.put("attendeeName", "Vipin Shrivatri") // Attendees name
            attendeesValues2.put("attendeeEmail", "vipin.shrivatri@teamlease.com") // Attendee
            attendeesValues2.put("attendeeRelationship", 0) // Relationship_Attendee(1),
            attendeesValues2.put("attendeeType", 0) // None(0), Optional(1),
            attendeesValues2.put("attendeeStatus", 0) // NOne(0), Accepted(1),
            val attendeuesesUri2: Uri = this.getApplicationContext().getContentResolver()
                .insert(Uri.parse(attendeuesesUriString), attendeesValues2)!!
        }
    }

//    private fun addAlarms(
//        cr: ContentResolver, eventId: Long,
//        minutes: Int, method: Int
//    ) {
//        val reminderUriString = "content://com.android.calendar/reminders"
//        val reminderValues = ContentValues()
//        reminderValues.put(Reminders.EVENT_ID, eventId)
//        reminderValues.put(Reminders.MINUTES, minutes)
//        reminderValues.put(Reminders.METHOD, method)
//
//        cr.insert(Uri.parse(reminderUriString), reminderValues)
//    }

    override fun OnaddSingleEvent() {

        addToCalender(
            this,
            "Trainers Portal Development Meeting 1",
            "https://www.teamleaseedtech.com/",
            true,
            true,

            )
        myToast("Single Event Sent")

    }

    override fun OnaddMultiEvent() {

        for (i in 1..3) {
            if (i == 1) {
                addToCalender(
                    this,
                    "Meeting 1",
                    "https://www.teamleaseedtech.com/",
                    true,
                    true,

                    )
            } else if (i == 2) {
                addToCalender(
                    this,
                    "Meeting 2",
                    "https://www.teamleaseedtech.com/",
                    true,
                    true,

                    )
            } else if (i == 3) {
                addToCalender(
                    this,
                    "Meeting 3",
                    "https://www.teamleaseedtech.com/",
                    true,
                    true
                )
            }
        }
        myToast("Multiple Event Sent")

    }

    override fun OndeleteEvent() {
        deleteEvent()
    }

//    private fun readEvents() {
////        val desc :TextView=findViewById(R.id.desc);
////        desc.setText(Utlity.readCalendarEvent(applicationContext).toString())
//        val cursor: Cursor = this.getContentResolver().query(
//            Uri.parse("content://com.android.calendar/events"),
//            arrayOf("calendar_id", "title", "description", "dtstart", "dtend", "eventLocation"),
//            null,
//            null,
//            null
//        )!!
//        //Cursor cursor = cr.query(Uri.parse("content://calendar/calendars"), new String[]{ "_id", "name" }, null, null, null);
//        //Cursor cursor = cr.query(Uri.parse("content://calendar/calendars"), new String[]{ "_id", "name" }, null, null, null);
//        var add: String? = null
//        cursor.moveToFirst()
//        val CalNames = arrayOfNulls<String>(cursor.getCount())
//        val CalIds = IntArray(cursor.getCount())
//        for (i in CalNames.indices) {
//            CalIds[i] = cursor.getInt(0)
//            CalNames[i] = """ Event${cursor.getInt(0)}:
//                 Title: ${cursor.getString(1)}
//            Description: ${cursor.getString(2)}
//            Start Date: ${Date(cursor.getLong(3))}
//            End Date : ${Date(cursor.getLong(4))}
//            Location : ${cursor.getString(5)}
//            """.trimIndent()
//            if (add == null) add = CalNames[i] else {
//                add += CalNames[i]
//            }
//            (findViewById<View>(R.id.desc) as TextView).text = add
//            cursor.moveToNext()
//        }
//        cursor.close()
//    }

    private fun specificEvent() {
        val projection = arrayOf(
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events.EVENT_LOCATION
        )

        val startTime = Calendar.getInstance()

        startTime[Calendar.HOUR_OF_DAY] = 0
        startTime[Calendar.MINUTE] = 0
        startTime[Calendar.SECOND] = 0

        val endTime = Calendar.getInstance()
        endTime.add(Calendar.DATE, 1)

        val selection =
            "(( " + CalendarContract.Events.DTSTART + " >= " + startTime.timeInMillis + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.timeInMillis + " ) AND ( deleted != 1 ))"
        val cursor: Cursor = this.getContentResolver()
            .query(CalendarContract.Events.CONTENT_URI, projection, selection, null, null)!!

        val events: MutableList<String> = ArrayList()
        if (cursor.count > 0 && cursor.moveToFirst()) {
            do {
                events.add(cursor.getString(1))
                myToast(events.toString())
                val txt = findViewById<View>(R.id.desc) as TextView
                txt.setText(events.toString())

            } while (cursor.moveToNext())
        }
    }


    private fun deleteEvent() {

        var iNumRowsDeleted = 0
        val eventsUri = Uri.parse("content://com.android.calendar/events")
        val cur = contentResolver.query(eventsUri, null, null, null, null)

        while (cur!!.moveToNext()) {
            val eventUri = ContentUris.withAppendedId(eventsUri, eventID)
            iNumRowsDeleted = contentResolver.delete(eventUri, null, null)
            Log.i("TAG", "Deleted " + iNumRowsDeleted + " calendar entry.")
        }
    }

}