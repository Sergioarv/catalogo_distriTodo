package co.com.sergio.catalogodistritodo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import co.com.sergio.catalogodistritodo.utils.ProgressDialog
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    lateinit var emailLogin: EditText
    lateinit var passwordLogin: EditText
    lateinit var loginBtn: Button

    var auth = Firebase.auth

    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var actionBar: ActionBar? = supportActionBar

        actionBar?.title = "Inicio Sesion"
        actionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimaryDark)));
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)

        emailLogin = findViewById(R.id.emailLogin)
        passwordLogin = findViewById(R.id.passwordLogin)
        loginBtn = findViewById(R.id.loginBtn)

//        Evento para ingresar a la sesion
        loginBtn.setOnClickListener(View.OnClickListener {
            //Comvertimos a string los editText email y password
            var email = emailLogin.text.toString()
            var password = passwordLogin.text.toString()

            if (email == "" || password == "") {
                Toast.makeText(this, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show()
            } else {

                //Validacion de correo electronico
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailLogin.error = "Correo invalido"
                    emailLogin.isFocusable = true
                } else if (password.length < 6) {
                    passwordLogin.error = "La contraseña debe ser mayor a 6"
                    passwordLogin.isFocusable = true
                } else {
                    LogginAdmin(email, password)
                }
            }
        })
    }

    private fun LogginAdmin(email: String, password: String) {
        progressDialog = ProgressDialog(this)
        progressDialog.startProgressBar()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    var user: FirebaseUser = auth.currentUser!!

                    var email: String = emailLogin.text.toString()
                    var password: String = passwordLogin.text.toString()

                    val mainIntent = Intent(this, MainAdminActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                    Toast.makeText(this, "Bienvenida(o): " + user.email, Toast.LENGTH_SHORT).show()
                    progressDialog.isDismiss()
                } else {
                    UserInvalid()
                    progressDialog.isDismiss()
                }
            }
            .addOnFailureListener { _->
                progressDialog.isDismiss()
                UserInvalid()
            }
    }

    @SuppressLint("MissingInflatedId")
    private fun UserInvalid() {

        var builderDialog = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.failed_dialog, null)
        var dialogBtn = dialogView.findViewById<View>(R.id.failed_btn)
        var dialogTxt = dialogView.findViewById<TextView>(R.id.failed_txt)
        var dialogTittle = dialogView.findViewById<TextView>(R.id.failed_tittle)
        dialogTittle.text = "!Ha Ocurrido un Error¡"
        dialogTxt.text = "Verifique si el correo o la contraseña son correctos"
        builderDialog.setView(dialogView)
        var alertDialog = builderDialog.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

        dialogBtn.setOnClickListener ( View.OnClickListener {
            alertDialog.dismiss()
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

//    override fun onBackPressed() {
//        if (supportFragmentManager.backStackEntryCount == 0) {
//            finish()
//        } else {
//            supportFragmentManager.popBackStack()
//        }
//    }
}