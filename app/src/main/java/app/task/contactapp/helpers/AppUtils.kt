package app.task.contactapp.helpers

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar


/*
 * Created by venkatesh on 19-July-19.
 */

class AppUtils {

    companion object {

        fun showSnackBar(view: View, snackBarMessage: String) {
            Snackbar.make(view, snackBarMessage, Snackbar.LENGTH_LONG).show()
        }

        fun showShortSnackBar(view: View, snackBarMessage: String) {
            Snackbar.make(view, snackBarMessage, Snackbar.LENGTH_SHORT).show()
        }

        fun showSnackBarWithActionOK(view: View, snackBarMessage: String) {
            Snackbar.make(view, snackBarMessage, Snackbar.LENGTH_INDEFINITE).setAction("OK") { }.show()
        }

        fun showLog(logType: Int, logTag: String, logMsg: String) {
            when (logType) {
                Log.VERBOSE, Log.ASSERT -> Log.v(logTag, logMsg)
                Log.DEBUG -> Log.d(logTag, logMsg)
                Log.ERROR -> Log.e(logTag, logMsg)
                Log.INFO -> Log.i(logTag, logMsg)
                Log.WARN -> Log.w(logTag, logMsg)
                else -> Log.v(logTag, logMsg)
            }
        }

        fun showVLog(logTag: String, logMsg: String) {
            showLog(Log.VERBOSE, logTag, logMsg)
        }


        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun showShortToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}