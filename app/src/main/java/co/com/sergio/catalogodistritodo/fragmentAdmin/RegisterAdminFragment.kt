package co.com.sergio.catalogodistritodo.fragmentAdmin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import co.com.sergio.catalogodistritodo.MainAdminActivity
import co.com.sergio.catalogodistritodo.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterAdminFragment : Fragment() {

    lateinit var nameAdmin: EditText;
    lateinit var lastNameAdmin: EditText;
    lateinit var emailAdmin: EditText;
    lateinit var passwordAdmin: EditText;
    lateinit var registerBtnAdmin: Button;

    var auth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_register_admin, container, false);

        nameAdmin = view.findViewById(R.id.nameAdmin);
        lastNameAdmin = view.findViewById(R.id.lastNameAdmin);
        emailAdmin = view.findViewById(R.id.emailAdmin);
        passwordAdmin = view.findViewById(R.id.passwordAdmin);

        registerBtnAdmin = view.findViewById(R.id.registerBtnAdmin);

        // Evento click en registrar
        registerBtnAdmin.setOnClickListener(View.OnClickListener {
            //Comvertimos a string los editText email y password
            var email = emailAdmin.text.toString();
            var password = passwordAdmin.text.toString();
            var name = nameAdmin.text.toString();
            var lastName = lastNameAdmin.text.toString();

            if(email == "" || password == "" || name == "" || lastName == ""){
                Toast.makeText(this.activity, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show();
            }else {

                //Validacion de correo electronico
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailAdmin.error = "Correo invalido";
                    emailAdmin.isFocusable = true;
                } else if (password.length < 6) {
                    passwordAdmin.error = "La contraseña debe ser mayor a 6";
                    passwordAdmin.isFocusable = true;
                } else {
                    RegistorAdministradores(email, password);
                }
            }
        });

        return view;
    }

    //Metodo para regitrar administradores
    private fun RegistorAdministradores(email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    var user : FirebaseUser = auth.currentUser!!;

                    var UID : String = user.uid
                    var email: String = emailAdmin.text.toString();
                    var password: String = passwordAdmin.text.toString();
                    var name: String = nameAdmin.text.toString();
                    var lastName: String = lastNameAdmin.text.toString();

                    var administradores = HashMap<String, String>();
                    administradores["UID"] = UID;
                    administradores["email"] = email;
                    administradores["password"] = password;
                    administradores["name"] = name;
                    administradores["lastname"] = lastName;
                    administradores["imagen"] = "";

                    //Iniciar database
                    val database = Firebase.database;
                    val reference = database.getReference("db_catalogo_distri_admin")
                    reference.child(UID).setValue(administradores);

                    val mainIntent = Intent(this.activity, MainAdminActivity::class.java)
                    startActivity(mainIntent)
                    Toast.makeText(this.activity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    this.activity?.finish()
                }else{
                    Toast.makeText(this.activity, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { task ->
                Toast.makeText(this.activity, task.message.toString(), Toast.LENGTH_SHORT)
            }
    }
}