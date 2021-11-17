package edtech.teamlease.calender.ui


import android.Manifest.permission.READ_CALENDAR
import android.Manifest.permission.WRITE_CALENDAR
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
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

/**
 * Created by Vipin Shrivatri on 16-11-2021.
 */


class EventActivity : AppCompatActivity(), ViewModel.ViewListener {

    val callbackId = 42


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        val binding: ActivityEventBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_event)
        val viewModel = ViewModelProviders.of(this).get(ViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel.viewListener = this

        checkPermission(callbackId, READ_CALENDAR, WRITE_CALENDAR)

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
            startDate = simpleDateFormat.parse("20-11-2021 11:15:00")
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
        val eventID = uri?.lastPathSegment!!.toLong()


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

            //Guest 1
            val attendeesValues = ContentValues()
            attendeesValues.put("event_id", eventID)
            attendeesValues.put("attendeeName", "Sachin Yadav") // Attendees name
            attendeesValues.put("attendeeEmail", "sachin.yadav@teamlease.com") // Attendee
            attendeesValues.put("attendeeRelationship", 0) // Relationship_Attendee(1),
            attendeesValues.put("attendeeType", 0) // None(0), Optional(1),
            attendeesValues.put("attendeeStatus", 0) // NOne(0), Accepted(1),
            val attendeuesesUri1: Uri = this.getApplicationContext().getContentResolver()
                .insert(Uri.parse(attendeuesesUriString), attendeesValues)!!

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

}