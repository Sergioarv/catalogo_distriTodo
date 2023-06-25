package co.com.sergio.catalogodistritodo.utils

import android.app.Activity
import android.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import co.com.sergio.catalogodistritodo.R
import co.com.sergio.catalogodistritodo.fragmentAdmin.RegisterAdminFragment

class ProgressDialogF(val myActivity: Fragment){

    private lateinit var isDialog: AlertDialog

    fun startProgressBar() {
        val dialogView = myActivity.layoutInflater.inflate(R.layout.progress_dialog, null) // that is your view
        val builder = AlertDialog.Builder(myActivity.requireContext()) // and that is the builder

        /** Set Dialog */
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()
    }

    fun isDismiss(){
        isDialog.dismiss()
    }
}