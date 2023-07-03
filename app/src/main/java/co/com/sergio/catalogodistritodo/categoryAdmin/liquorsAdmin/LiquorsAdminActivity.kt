package co.com.sergio.catalogodistritodo.categoryAdmin.liquorsAdmin

import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.com.sergio.catalogodistritodo.R
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
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import io.reactivex.rxjava3.annotations.NonNull
import java.lang.Exception
import java.util.Objects


class LiquorsAdminActivity : AppCompatActivity() {

    lateinit var recyclerViewLiquor: RecyclerView;
    lateinit var mFirebaseDatabase: FirebaseDatabase;
    lateinit var mReference: DatabaseReference;

    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<Liquor, ViewHolderLiquors>;
    lateinit var options: FirebaseRecyclerOptions<Liquor>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liquors_admin)

        var actionBar: ActionBar? = supportActionBar
        actionBar?.title = "Licores"
        actionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimaryDark)));
        actionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerViewLiquor = findViewById(R.id.recyclerViewLiquor)
        recyclerViewLiquor.setHasFixedSize(true)

        mFirebaseDatabase = Firebase.database
        mReference = mFirebaseDatabase.getReference("LICORES")

        ListImageLiquors();
    }

    private fun ListImageLiquors() {
        options = FirebaseRecyclerOptions.Builder<Liquor>().setQuery(mReference, Liquor::class.java)
            .build()

        firebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<Liquor, ViewHolderLiquors>(options) {
                override fun onBindViewHolder(
                    viewHolderLiquors: ViewHolderLiquors,
                    i: Int,
                    liquor: Liquor
                ) {
                    viewHolderLiquors.setterLiquors(
                        applicationContext,
                        liquor.image,
                        liquor.name,
                        liquor.price,
                        liquor.description
                    )
                }

                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): ViewHolderLiquors {
                    val itemView: View = LayoutInflater.from(parent.context)
                        .inflate(
                            co.com.sergio.catalogodistritodo.R.layout.item_liquors,
                            parent,
                            false
                        )
                    val viewHolderLiquors = ViewHolderLiquors(itemView)
                    viewHolderLiquors.setOnClickListener(object : ViewHolderLiquors.ClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            Toast.makeText(
                                this@LiquorsAdminActivity,
                                "Item Click",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onItemLongClick(view: View, position: Int) {

                            var name = getItem(position).name
                            var price = getItem(position).price
                            var description = getItem(position).description
                            var imagen = getItem(position).image

                            var builderDialog = AlertDialog.Builder(this@LiquorsAdminActivity)
                            var opc: Array<String> = arrayOf("Actualizar", "Eliminar")
                            builderDialog.setItems(opc) { _, pos ->
                                when (pos) {
                                    0 -> {
                                        var intent: Intent = Intent(this@LiquorsAdminActivity, AddLiquorsActivity::class.java)
                                        intent.putExtra("currentName", name)
                                        intent.putExtra("currentPrice", price)
                                        intent.putExtra("currentDescription", description)
                                        intent.putExtra("currentImage", imagen)

                                        startActivity(intent)
                                        finish()
                                    }

                                    1 -> {
                                        DeletedImageLiquors(name, imagen)
                                    }
                                }
                            }

                            builderDialog.create().show()
                        }
                    })
                    return viewHolderLiquors
                }
            }

        recyclerViewLiquor.layoutManager = GridLayoutManager(this@LiquorsAdminActivity, 2)
        firebaseRecyclerAdapter.startListening()
        recyclerViewLiquor.adapter = firebaseRecyclerAdapter
    }

    private fun DeletedImageLiquors(currentName: String, currentImage: String) {

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
                        var liquorsAxu: Liquor? = ds.getValue(Liquor::class.java)
                        if(liquorsAxu?.image.equals(currentImage)) {
                            ds.ref.removeValue()
                        }
                    }
                    Toast.makeText(
                        this@LiquorsAdminActivity,
                        "La imagen ha sido eliminada",
                        Toast.LENGTH_SHORT
                    ).show();
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@LiquorsAdminActivity,
                        "" + error.getMessage(),
                        Toast.LENGTH_SHORT
                    ).show();
                }
            })

            var imagenSelect: StorageReference = Firebase.storage.getReferenceFromUrl(currentImage)
            imagenSelect.delete().addOnSuccessListener(object : OnSuccessListener<Void> {
                override fun onSuccess(aVoid: Void?) {
                    Toast.makeText(
                        this@LiquorsAdminActivity,
                        "La imagen ha sido eliminada",
                        Toast.LENGTH_SHORT
                    ).show();
                }
            }).addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    Toast.makeText(
                        this@LiquorsAdminActivity,
                        "" + e.message,
                        Toast.LENGTH_SHORT
                    ).show();
                }
            })

            alertDialog.dismiss()
        })

        dialogNotBtn.setOnClickListener(View.OnClickListener {

            Toast.makeText(
                this@LiquorsAdminActivity,
                "Cancelado por administrador",
                Toast.LENGTH_SHORT
            ).show();

            alertDialog.dismiss()
        })

    }

    override fun onStart() {
        super.onStart()
        if (firebaseRecyclerAdapter != null) {
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
                startActivity(Intent(this, AddLiquorsActivity::class.java))
                finish()
            }

            R.id.viewItemBtn -> {
                Toast.makeText(this, "Listar item", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}