package co.com.sergio.catalogodistritodo.categoryAdmin.candysAdmin

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

class CandysAdminActivity : AppCompatActivity() {

    lateinit var recyclerViewCandy: RecyclerView;
    lateinit var mFirebaseDatabase: FirebaseDatabase;
    lateinit var mReference: DatabaseReference;

    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<Candy, ViewHolderCandys>;
    lateinit var options: FirebaseRecyclerOptions<Candy>

    lateinit var dialogSort: Dialog
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candys_admin)

        var actionBar: ActionBar? = supportActionBar
        actionBar?.title = "Dulces"
        actionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimaryDark)));
        actionBar?.setDisplayHomeAsUpEnabled(true)


        recyclerViewCandy = findViewById(R.id.recyclerViewCandy)
        recyclerViewCandy.setHasFixedSize(true)

        mFirebaseDatabase = Firebase.database
        mReference = mFirebaseDatabase.getReference("DULCES")

        dialogSort = Dialog(this@CandysAdminActivity)

        ListImageCandys();

    }

    private fun ListImageCandys() {
        options = FirebaseRecyclerOptions.Builder<Candy>().setQuery(mReference, Candy::class.java)
            .build()

        firebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<Candy, ViewHolderCandys>(options) {
                override fun onBindViewHolder(
                    viewHolderCandys: ViewHolderCandys,
                    i: Int,
                    candy: Candy
                ) {
                    viewHolderCandys.setterCandys(
                        applicationContext,
                        candy.image,
                        candy.name,
                        candy.price,
                        candy.description
                    )
                }

                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): ViewHolderCandys {
                    val itemView: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_candys, parent, false)
                    val viewHolderCandys = ViewHolderCandys(itemView)
                    viewHolderCandys.setOnClickListener(object : ViewHolderCandys.ClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            Toast.makeText(this@CandysAdminActivity, "Item Click", Toast.LENGTH_SHORT).show()
                        }

                        override fun onItemLongClick(view: View, position: Int) {
                            var name = getItem(position).name
                            var price = getItem(position).price
                            var description = getItem(position).description
                            var imagen = getItem(position).image

                            var builderDialog = AlertDialog.Builder(this@CandysAdminActivity)
                            var opc: Array<String> = arrayOf("Actualizar", "Eliminar")
                            builderDialog.setItems(opc) { _, pos ->
                                when (pos) {
                                    0 -> {
                                        var intent: Intent = Intent(this@CandysAdminActivity, AddCandysActivity::class.java)
                                        intent.putExtra("currentName", name)
                                        intent.putExtra("currentPrice", price)
                                        intent.putExtra("currentDescription", description)
                                        intent.putExtra("currentImage", imagen)

                                        startActivity(intent)
                                        finish()
                                    }

                                    1 -> {
                                        DeletedImageCandys(name, imagen)
                                    }
                                }
                            }

                            builderDialog.create().show()
                        }
                    })
                    return viewHolderCandys
                }
            }

        sharedPreferences = this@CandysAdminActivity.getSharedPreferences("LICORES", MODE_PRIVATE)
        var ordenar_en = sharedPreferences.getString("Ordenar", "Dos")

        if (ordenar_en.equals("Dos")) {
            recyclerViewCandy.layoutManager = GridLayoutManager(this@CandysAdminActivity, 2)
            firebaseRecyclerAdapter.startListening()
            recyclerViewCandy.adapter = firebaseRecyclerAdapter
        } else if (ordenar_en.equals("Tres")) {
            recyclerViewCandy.layoutManager = GridLayoutManager(this@CandysAdminActivity, 3)
            firebaseRecyclerAdapter.startListening()
            recyclerViewCandy.adapter = firebaseRecyclerAdapter
        }
    }

    private fun DeletedImageCandys(currentName: String, currentImage: String) {

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
                        var candysAxu: Candy? = ds.getValue(Candy::class.java)
                        if(candysAxu?.image.equals(currentImage)) {
                            ds.ref.removeValue()
                        }
                    }
                    Toast.makeText(
                        this@CandysAdminActivity,
                        "La imagen ha sido eliminada",
                        Toast.LENGTH_SHORT
                    ).show();
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@CandysAdminActivity,
                        "" + error.getMessage(),
                        Toast.LENGTH_SHORT
                    ).show();
                }
            })

            var imagenSelect: StorageReference = Firebase.storage.getReferenceFromUrl(currentImage)
            imagenSelect.delete().addOnSuccessListener(object : OnSuccessListener<Void> {
                override fun onSuccess(aVoid: Void?) {
                    Toast.makeText(
                        this@CandysAdminActivity,
                        "La imagen ha sido eliminada",
                        Toast.LENGTH_SHORT
                    ).show();
                }
            }).addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    Toast.makeText(
                        this@CandysAdminActivity,
                        "" + e.message,
                        Toast.LENGTH_SHORT
                    ).show();
                }
            })

            alertDialog.dismiss()
        })

        dialogNotBtn.setOnClickListener(View.OnClickListener {

            Toast.makeText(
                this@CandysAdminActivity,
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
                startActivity(Intent(this, AddCandysActivity::class.java))
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
