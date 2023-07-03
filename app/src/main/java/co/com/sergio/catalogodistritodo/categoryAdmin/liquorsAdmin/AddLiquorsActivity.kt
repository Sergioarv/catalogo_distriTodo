package co.com.sergio.catalogodistritodo.categoryAdmin.liquorsAdmin

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.com.sergio.catalogodistritodo.R
import co.com.sergio.catalogodistritodo.utils.ProgressDialog
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class AddLiquorsActivity : AppCompatActivity() {

    lateinit var nameTextLiquors: EditText
    lateinit var priceLiquors: EditText
    lateinit var descriptionLiquors: EditText
    lateinit var imageLiquors: ImageView
    lateinit var addLiquorsBtn: Button

    var sourceStorage: String = "licores_almacenados/"
    var sourceDataBase: String = "LICORES"
    lateinit var sourceUri: Uri

    var searchImage = false;

    lateinit var progressDialog: ProgressDialog

    var storageRef: FirebaseStorage = Firebase.storage;
    var databaseRef: FirebaseDatabase = Firebase.database;

    var storage = storageRef.reference
    var database = databaseRef.getReference(sourceDataBase)

    val CODIGO_DE_SOLICITUD_IMAGEN = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_liquors)

        var actionBar = supportActionBar;
        actionBar?.title = "Agregar Licor"
        actionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimaryDark)));
        actionBar?.setDisplayHomeAsUpEnabled(true)

        nameTextLiquors = findViewById(R.id.nameTextLiquors)
        priceLiquors = findViewById(R.id.priceLiquors)
        descriptionLiquors = findViewById(R.id.descriptionLiquors)
        imageLiquors = findViewById(R.id.imageLiquors)
        addLiquorsBtn = findViewById(R.id.addLiquorsBtn)

        imageLiquors.setOnClickListener(View.OnClickListener {

            var intent: Intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(
                Intent.createChooser(intent, "Seleccionar imagen"),
                CODIGO_DE_SOLICITUD_IMAGEN
            )
        })

        addLiquorsBtn.setOnClickListener(View.OnClickListener {
            progressDialog = ProgressDialog(this)
            progressDialog.startProgressBar()

            var nameLiquors = nameTextLiquors.text.toString()
            var priceLiquors = priceLiquors.text.toString()
            var descriptionLiquor = descriptionLiquors.text.toString()

            if (nameLiquors == "" || priceLiquors == "" || descriptionLiquor == "" || !searchImage) {
                progressDialog.isDismiss()
                Toast.makeText(this, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                subirImagen()
            }
        })
    }

    private fun subirImagen() {

        if (sourceUri != null) {
            progressDialog = ProgressDialog(this)
            progressDialog.startProgressBar()

            var mStorageReference = storage.child(
                sourceStorage + System.currentTimeMillis() + "." + ObtenerExtencionArchivo(
                    sourceUri
                )
            )
            mStorageReference.putFile(sourceUri)
                .addOnSuccessListener { taskSnapshot ->
                    var uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                    while (!uriTask.isSuccessful) {
                    }

                    var donwloadUri: Uri = uriTask.result

                    var mNombre = nameTextLiquors.text.toString()
                    var mPrice = priceLiquors.text.toString()
                    var mDescripcion = descriptionLiquors.text.toString()

                    var liquorsObj: Liquor =
                        Liquor(donwloadUri.toString(), mNombre, mPrice, mDescripcion)
                    var ID_IMAGEN: String = database.push().key.toString()

                    database.child(ID_IMAGEN).setValue(liquorsObj)

                    progressDialog.isDismiss()
                    Toast.makeText(this, "Subido exitosamente", Toast.LENGTH_SHORT).show()
                    searchImage = false

                    startActivity(Intent(this, LiquorsAdminActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    progressDialog.isDismiss()
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener {
                    progressDialog.isDismiss()
                    Toast.makeText(this, "Procesando imagen", Toast.LENGTH_SHORT).show()
                }
            progressDialog.isDismiss()
        } else {
            progressDialog.isDismiss()
            Toast.makeText(this, "Debe asignar una imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun ObtenerExtencionArchivo(uri: Uri): String {
        var contentResolver: ContentResolver = getContentResolver()
        var mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)).toString();
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODIGO_DE_SOLICITUD_IMAGEN
            && resultCode == RESULT_OK
            && data != null
            && data.data != null
        ) {
            sourceUri = data.data!!

            try {
                var bitMap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, sourceUri)
                imageLiquors.setImageBitmap(bitMap)
                searchImage = true
            } catch (e: Exception) {
                Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}