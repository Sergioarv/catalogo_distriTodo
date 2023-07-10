package co.com.sergio.catalogodistritodo.fragmentAdmin

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import co.com.sergio.catalogodistritodo.MainAdminActivity
import co.com.sergio.catalogodistritodo.R
import co.com.sergio.catalogodistritodo.utils.ProgressDialogF
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso


class ProfileAdminFragment : Fragment() {

    var firebaseAuth = Firebase.auth
    var user = firebaseAuth.currentUser!!
    var db_administradores = Firebase.database.getReference("ADMINISTRADORES")
    var source_storage = Firebase.storage.getReference("foto_perfil_administrador/")

    //Solicitudes de galeria
    private val CODIGO_SOLICITUD_ALMACENAMIENTO = 300
    private val CODIGO_GALERIA_SELECION_IMAGEN = 5

//    lateinit var permisos_almacenamiento: Array<String>

    lateinit var imageUri: Uri
    lateinit var progressDialog: ProgressDialogF

    lateinit var image_profile: ImageView
    lateinit var UID_profile: TextView
    lateinit var name_profile: TextView
    lateinit var last_name_profile: TextView
    lateinit var email_profile: TextView
    lateinit var password_profile: TextView
    lateinit var update_password_profile: Button
    lateinit var update_data_profile: Button

    var searchImage = false;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreate(savedInstanceState)

        var view = inflater.inflate(R.layout.fragment_profile_admin, container, false)

        image_profile = view.findViewById(R.id.image_profile)
        UID_profile = view.findViewById(R.id.UID_profile)
        name_profile = view.findViewById(R.id.name_profile)
        last_name_profile = view.findViewById(R.id.last_name_profile)
        email_profile = view.findViewById(R.id.email_profile)
        password_profile = view.findViewById(R.id.password_profile)
        update_password_profile = view.findViewById(R.id.update_password_profile)
        update_data_profile = view.findViewById(R.id.update_data_profile)

        progressDialog = ProgressDialogF(this)

        db_administradores.child(user.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var uid = "" + snapshot.child("UID").getValue()
                    var name = "" + snapshot.child("name").getValue()
                    var lastname = "" + snapshot.child("lastname").getValue()
                    var email = "" + snapshot.child("email").getValue()
                    var password = "" + snapshot.child("password").getValue()
                    var image = "" + snapshot.child("imagen").getValue()

                    UID_profile.setText(uid)
                    name_profile.setText(name)
                    last_name_profile.setText(lastname)
                    email_profile.setText(email)
                    password_profile.setText(password)

                    try {
                        //Si existe imagen
                        Picasso.get().load(image).placeholder(R.drawable.register)
                            .into(image_profile)
                    } catch (e: Exception) {
                        //No existe imagen
                        Picasso.get().load(R.drawable.register).into(image_profile)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        update_password_profile.setOnClickListener(View.OnClickListener {
            startActivity(Intent(activity, ChangePassAdminActivity::class.java))
            activity?.finish()
        })

        image_profile.setOnClickListener(View.OnClickListener {
            ElegirDeGaleria()
        })

        return view
    }


    private fun ElegirDeGaleria() {
        progressDialog.startProgressBar()
        var galleryIntent: Intent = Intent()
        galleryIntent.setType("image/*")
        galleryIntent.setAction(Intent.ACTION_PICK)
        startActivityForResult(
            galleryIntent, CODIGO_GALERIA_SELECION_IMAGEN
        )
    }

    private fun ObtenerExtencionArchivo(uri: Uri): String {
        var contentResolver: ContentResolver = this.requireContext().contentResolver
        var mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)).toString();
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODIGO_GALERIA_SELECION_IMAGEN
            && resultCode == AppCompatActivity.RESULT_OK
            && data != null
            && data.data != null
        ) {
            imageUri = data.data!!

            try {
                ActualizarFotoDB(imageUri)
            } catch (e: Exception) {

                Toast.makeText(activity, "" + e.message, Toast.LENGTH_SHORT).show()
                progressDialog.isDismiss()
            }
        }
    }

    private fun ActualizarFotoDB(newImageUri: Uri) {

        var sourceFileAndName = user.uid + "." + ObtenerExtencionArchivo(newImageUri)
        var source_storage_ref = source_storage.child(sourceFileAndName)
        source_storage_ref.putFile(newImageUri)
            .addOnSuccessListener { taskSnapshot ->
                var uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful) {
                }

                var downloadUri: Uri = uriTask.result

                if (uriTask.isSuccessful) {
                    val results = HashMap<String, String>()
                    results["imagen"] = downloadUri.toString();
                    db_administradores.child(user.uid).updateChildren(results as Map<String, Any>)
                        .addOnSuccessListener {
                            startActivity(Intent(activity, MainAdminActivity::class.java))
                            requireActivity().finish()
                            Toast.makeText(
                                activity,
                                "Imagen cambiada con exito",
                                Toast.LENGTH_SHORT
                            ).show()
                            searchImage = false
                            progressDialog.isDismiss()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                activity,
                                "" + e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            progressDialog.isDismiss()
                        }
                } else {
                    Toast.makeText(activity, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
                    progressDialog.isDismiss()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    activity,
                    "" + e.message,
                    Toast.LENGTH_SHORT
                ).show()
                progressDialog.isDismiss()
            }
    }

}