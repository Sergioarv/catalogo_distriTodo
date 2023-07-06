package co.com.sergio.catalogodistritodo.categoryAdmin.othersAdmin

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.com.sergio.catalogodistritodo.R
import co.com.sergio.catalogodistritodo.categoryAdmin.liquorsAdmin.AddLiquorsActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class OthersAdminActivity : AppCompatActivity() {

    lateinit var recyclerViewOther: RecyclerView;
    lateinit var mFirebaseDatabase: FirebaseDatabase;
    lateinit var mReference: DatabaseReference;

    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<Other, ViewHolderOthers>;
    lateinit var options: FirebaseRecyclerOptions<Other>

    lateinit var dialogSort: Dialog
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_others_admin)

        var actionBar: ActionBar? = supportActionBar
        actionBar?.title = "Otros"
        actionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimaryDark)));
        actionBar?.setDisplayHomeAsUpEnabled(true)


        recyclerViewOther = findViewById(R.id.recyclerViewOther)
        recyclerViewOther.setHasFixedSize(true)

        mFirebaseDatabase = Firebase.database
        mReference = mFirebaseDatabase.getReference("OTROS")

        dialogSort = Dialog(this@OthersAdminActivity)

        ListImageOthers();

    }

    private fun ListImageOthers() {
        options = FirebaseRecyclerOptions.Builder<Other>().setQuery(mReference, Other::class.java)
            .build()

        firebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<Other, ViewHolderOthers>(options) {
                override fun onBindViewHolder(
                    viewHolderOthers: ViewHolderOthers,
                    i: Int,
                    other: Other
                ) {
                    viewHolderOthers.setterOthers(
                        applicationContext,
                        other.image,
                        other.name,
                        other.price,
                        other.description
                    )
                }

                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): ViewHolderOthers {
                    val itemView: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_others, parent, false)
                    val viewHolderOthers = ViewHolderOthers(itemView)
                    viewHolderOthers.setOnClickListener(object : ViewHolderOthers.ClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            Toast.makeText(this@OthersAdminActivity, "Item Click", Toast.LENGTH_SHORT).show()
                        }

                        override fun onItemLongClick(view: View, position: Int) {
                            var name = getItem(position).name
                            var price = getItem(position).price
                            var description = getItem(position).description
                            var imagen = getItem(position).image

                            var builderDialog = AlertDialog.Builder(this@OthersAdminActivity)
                            var opc: Array<String> = arrayOf("Actualizar", "Eliminar")
                            builderDialog.setItems(opc) { _, pos ->
                                when (pos) {
                                    0 -> {
                                        var intent: Intent = Intent(this@OthersAdminActivity, AddOthersActivity::class.java)
                                        intent.putExtra("currentName", name)
                                        intent.putExtra("currentPrice", price)
                                        intent.putExtra("currentDescription", description)
                                        intent.putExtra("currentImage", imagen)

                                        startActivity(intent)
                                        finish()
                                    }

                                    1 -> {
                                        DeletedImageOthers(name, imagen)
                                    }
                                }
                            }

                            builderDialog.create().show()
                        }
                    })
                    return viewHolderOthers
                }
            }

        sharedPreferences = this@OthersAdminActivity.getSharedPreferences("LICORES", MODE_PRIVATE)
        var ordenar_en = sharedPreferences.getString("Ordenar", "Dos")

        if (ordenar_en.equals("Dos")) {
            recyclerViewOther.layoutManager = GridLayoutManager(this@OthersAdminActivity, 2)
            firebaseRecyclerAdapter.startListening()
            recyclerViewOther.adapter = firebaseRecyclerAdapter
        } else if (ordenar_en.equals("Tres")) {
            recyclerViewOther.layoutManager = GridLayoutManager(this@OthersAdminActivity, 3)
            firebaseRecyclerAdapter.startListening()
            recyclerViewOther.adapter = firebaseRecyclerAdapter
        }
    }

    private fun DeletedImageOthers(currentName: String, currentImage: String) {

        var builderDialog = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.alert_dialog, null)
        var dialogOkBtn = dialogView.findViewById<View>(R.id.alert_ok_btn)
        var dialogNotBtn = dialogView.findViewById<View>(R.id.alert_not_btn)
        var dialogTxt = dialogView.findViewById<TextView>(R.id.alert_txt)
        var dialogTittle = dialogView.findViewById<TextView>(R.id.alert_tittle)
        dialogTittle.text = "!Eliminar¡"
        dialogTxt.text = "¿Desea eliminar la imagen?"
        builderDialog.setView(dialogView)
        var alertDialog = builderDialog.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

        dialogOkBtn.setOnClickListener(View.OnClickListener {

            var query: Query = mReference.orderByChild("name").equalTo(currentName)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        var othersAxu: Other? = ds.getValue(Other::class.java)
                        if(othersAxu?.image.equals(currentImage)) {
                            ds.ref.removeValue()
                        }
                    }
                    Toast.makeText(
                        this@OthersAdminActivity,
                        "La imagen ha sido eliminada",
                        Toast.LENGTH_SHORT
                    ).show();
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@OthersAdminActivity,
                        "" + error.getMessage(),
                        Toast.LENGTH_SHORT
                    ).show();
                }
            })

            var imagenSelect: StorageReference = Firebase.storage.getReferenceFromUrl(currentImage)
            imagenSelect.delete().addOnSuccessListener(object : OnSuccessListener<Void> {
                override fun onSuccess(aVoid: Void?) {
                    Toast.makeText(
                        this@OthersAdminActivity,
                        "La imagen ha sido eliminada",
                        Toast.LENGTH_SHORT
                    ).show();
                }
            }).addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    Toast.makeText(
                        this@OthersAdminActivity,
                        "" + e.message,
                        Toast.LENGTH_SHORT
                    ).show();
                }
            })

            alertDialog.dismiss()
        })

        dialogNotBtn.setOnClickListener(View.OnClickListener {

            Toast.makeText(
                this@OthersAdminActivity,
                "Cancelado por administrador",
                Toast.LENGTH_SHORT
            ).show();

            alertDialog.dismiss()
        })

    }

    override fun onStart() {
        super.onStart()
        if(firebaseRecyclerAdapter != null){
            firebaseRecyclerAdapter.startListening()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_agregar_item, menu)
        menuInflater.inflate(R.menu.menu_vista_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addItemBtn -> {
                startActivity(Intent(this, AddOthersActivity::class.java))
                finish()
            }
            R.id.viewItemBtn -> {
                sortimage()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun sortimage() {

        var dos_columnas: Button
        var tres_columnas: Button

        dialogSort.setContentView(R.layout.dialog_sort)

        dos_columnas = dialogSort.findViewById(R.id.dos_columnas)
        tres_columnas = dialogSort.findViewById(R.id.tres_columnas)

        dos_columnas.setOnClickListener(View.OnClickListener {
            var editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("Ordenar", "Dos")
            editor.apply()
            recreate()
            dialogSort.dismiss()
        })

        tres_columnas.setOnClickListener(View.OnClickListener {
            var editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("Ordenar", "Tres")
            editor.apply()
            recreate()
            dialogSort.dismiss()
        })

        dialogSort.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}