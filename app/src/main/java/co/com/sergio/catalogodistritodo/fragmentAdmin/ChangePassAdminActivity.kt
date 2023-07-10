package co.com.sergio.catalogodistritodo.fragmentAdmin

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import co.com.sergio.catalogodistritodo.LoginActivity
import co.com.sergio.catalogodistritodo.R
import co.com.sergio.catalogodistritodo.utils.ProgressDialog
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ChangePassAdminActivity : AppCompatActivity() {

    lateinit var currentPass: TextView
    lateinit var currentPassEt: EditText
    lateinit var newPassEt: EditText
    lateinit var changePassBtn: Button
    lateinit var goToHome: Button

    var dbAdministradores = Firebase.database.getReference("ADMINISTRADORES")
    var authF = Firebase.auth
    var user = authF.currentUser

    var progressDialog: ProgressDialog = ProgressDialog(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pass_admin)

        var actionBar = supportActionBar;
        actionBar!!.setTitle("Cambiar contraseña")
        actionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimaryDark)));
        actionBar?.setDisplayHomeAsUpEnabled(true)

        currentPass = findViewById(R.id.currentPass)
        currentPassEt = findViewById(R.id.currentPassEt)
        newPassEt = findViewById(R.id.newPassEt)
        changePassBtn = findViewById(R.id.changePassBtn)
        goToHome = findViewById(R.id.goToHome)

        //Consulta en DB
        var query = dbAdministradores.orderByChild("email").equalTo(user?.email)
        query.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children){
                    var pass = ""+ds.child("password").value
                    currentPass.setText(pass)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        changePassBtn.setOnClickListener(View.OnClickListener {
            var current_pass = currentPassEt.text.toString().trim()
            var new_pass = newPassEt.text.toString().trim()

            //Condiciones

            if(TextUtils.isEmpty(current_pass)){
                Toast.makeText(this, "El campo contraseña actual esta vacio", Toast.LENGTH_SHORT).show()
            }

            if(TextUtils.isEmpty(new_pass)){
                Toast.makeText(this, "El campo nueva contraseña esta vacio", Toast.LENGTH_SHORT).show()
            }

            if(!new_pass.equals("") || new_pass.length >= 6){
                changePassword(current_pass, new_pass)
            }else{
                newPassEt.error = "La contraseña debe ser mayor a 6"
                newPassEt.isFocusable = true
            }

        })
    }

    private fun changePassword(current_Pass: String, new_Pass: String) {
        progressDialog.startProgressBar()

        var authCredential = EmailAuthProvider.getCredential(user?.email.toString(), current_Pass)
        user!!.reauthenticate(authCredential)
            .addOnSuccessListener {
                user!!.updatePassword(new_Pass)
                    .addOnSuccessListener {
                        var new_Pass_new = newPassEt.text.toString().trim()
                        var result = HashMap<String, String>()
                        result["password"] = new_Pass_new
                        //Actualizar contraseña en DB
                        dbAdministradores.child(user!!.uid).updateChildren(result as Map<String, Any>)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                                progressDialog.isDismiss()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, ""+e.message, Toast.LENGTH_SHORT).show()
                                progressDialog.isDismiss()
                            }
                        //Cerrar Sesion
                        authF.signOut()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {e ->
                        Toast.makeText(this, ""+e.message, Toast.LENGTH_SHORT).show()
                        progressDialog.isDismiss()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, ""+e.message, Toast.LENGTH_SHORT).show()
                progressDialog.isDismiss()
            }
    }

    override fun onBackPressed() {

    }
}