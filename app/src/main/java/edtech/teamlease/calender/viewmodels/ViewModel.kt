package edtech.teamlease.calender.viewmodels


import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vipin Shrivatri on 16-11-2021.
 */

class ViewModel : ViewModel() {

    // define variable initial value is null that why ? use as null safety operator


    // call view listener interface for use as callback to activity
    var viewListener: ViewListener? = null


    fun OnaddSingleEvent(view: View) {
        viewListener?.OnaddSingleEvent()
    }
    fun OnaddMultiEvent(view: View) {
        viewListener?.OnaddMultiEvent()
    }
    fun OndeleteEvent(view: View) {
        viewListener?.OndeleteEvent()
    }

    interface ViewListener {

        // this method is use for when login operation started
        fun OnaddSingleEvent()
        fun OnaddMultiEvent()
        fun OndeleteEvent()


    }

}