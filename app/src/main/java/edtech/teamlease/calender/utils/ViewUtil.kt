package edtech.teamlease.trainingportal.util

import android.content.Context
import android.widget.Toast

/**
 * Created by Vipin Shrivatri on 16-11-2021.
 */


// functions to show show
fun Context.myToast(message: String)
{
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

