package co.com.sergio.catalogodistritodo.utils

import android.app.Activity
import android.app.AlertDialog
import androidx.fragment.app.Fragment
import co.com.sergio.catalogodistritodo.R

class ProgressDialog(val myActivity: Activity) {

    private lateinit var isDialog: AlertDialog

    fun startProgressBar() {
        /** Set view */
        val inflater = myActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_dialog, null)

        /** Set Dialog */
        val builder = AlertDialog.Builder(myActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()
    }

    fun isDismiss(){
        isDialog.dismiss()
    }
}