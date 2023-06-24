package co.com.sergio.catalogodistritodo

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    lateinit var emailLogin: EditText;
    lateinit var passwordLogin: EditText;
    lateinit var loginBtn: Button;

    var auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var actionBar: ActionBar? = supportActionBar;

        actionBar?.setTitle("Inicio Sesion")
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)

        emailLogin = findViewById(R.id.emailLogin)
        passwordLogin = findViewById(R.id.passwordLogin)
        loginBtn = findViewById(R.id.loginBtn)

//        Evento para ingresar a la sesion
        loginBtn.setOnClickListener(View.OnClickListener {
            //Comvertimos a string los editText email y password
            var email = emailLogin.text.toString();
            var password = passwordLogin.text.toString();

            if (email == "" || password == "") {
                Toast.makeText(this, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show();
            } else {

                //Validacion de correo electronico
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailLogin.error = "Correo invalido";
                    emailLogin.isFocusable = true;
                } else if (password.length < 6) {
                    passwordLogin.error = "La contraseña debe ser mayor a 6";
                    passwordLogin.isFocusable = true;
                } else {
                    LogginAdmin(email, password);
                }
            }
        });
    }

    private fun LogginAdmin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var user: FirebaseUser = auth.currentUser!!;

                    var email: String = emailLogin.text.toString();
                    var password: String = passwordLogin.text.toString();

                    val mainIntent = Intent(this, MainAdminActivity::class.java)
                    startActivity(mainIntent)
                    Toast.makeText(this, "Bienvenida(o)" + user?.email, Toast.LENGTH_SHORT).show()
                    this?.finish()
                } else {
                    UserInvalid()
                }
            }
            .addOnFailureListener { task ->
                Toast.makeText(this, task.message.toString(), Toast.LENGTH_SHORT)
            }
    }

    private fun UserInvalid() {

        val alertDialog: AlertDialog? = this?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton("Ok",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User clicked OK button
                    })
                setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            }
            // Set other dialog properties

            // Create the AlertDialog
            builder.create()
        }
        alertDialog?.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}