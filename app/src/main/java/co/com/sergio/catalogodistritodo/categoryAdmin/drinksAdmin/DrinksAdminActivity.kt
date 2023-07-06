package co.com.sergio.catalogodistritodo.categoryAdmin.drinksAdmin

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

class DrinksAdminActivity : AppCompatActivity() {

    lateinit var recyclerViewDrink: RecyclerView;
    lateinit var mFirebaseDatabase: FirebaseDatabase;
    lateinit var mReference: DatabaseReference;

    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<Drink, ViewHolderDrinks>;
    lateinit var options: FirebaseRecyclerOptions<Drink>

    lateinit var dialogSort: Dialog
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drinks_admin)

        var actionBar: ActionBar? = supportActionBar
        actionBar?.title = "Bebidas"
        actionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimaryDark)));
        actionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerViewDrink = findViewById(R.id.recyclerViewDrink)
        recyclerViewDrink.setHasFixedSize(true)

        mFirebaseDatabase = Firebase.database
        mReference = mFirebaseDatabase.getReference("BEBIDAS")

        dialogSort = Dialog(this@DrinksAdminActivity)

        ListImageDrinks();
    }

    private fun ListImageDrinks() {
        options = FirebaseRecyclerOptions.Builder<Drink>().setQuery(mReference, Drink::class.java)
            .build()

        firebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<Drink, ViewHolderDrinks>(options) {
                override fun onBindViewHolder(
                    viewHolderDrinks: ViewHolderDrinks,
                    i: Int,
                    drink: Drink
                ) {
                    viewHolderDrinks.setterDrinks(
                        applicationContext,
                        drink.image,
                        drink.name,
                        drink.price,
                        drink.description
                    )
                }

                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): ViewHolderDrinks {
                    val itemView: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_drinks, parent, false)
                    val viewHolderDrinks = ViewHolderDrinks(itemView)
                    viewHolderDrinks.setOnClickListener(object : ViewHolderDrinks.ClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            Toast.makeText(this@DrinksAdminActivity, "Item Click", Toast.LENGTH_SHORT).show()
                        }

                        override fun onItemLongClick(view: View, position: Int) {
                            var name = getItem(position).name
                            var imagen = getItem(position).image
                            var price = getItem(position).price
                            var description = getItem(position).description

                            var builderDialog = AlertDialog.Builder(this@DrinksAdminActivity)
                            var opc: Array<String> = arrayOf("Actualizar", "Eliminar")
                            builderDialog.setItems(opc) { _, pos ->
                                when (pos) {
                                    0 -> {
                                        var intent: Intent = Intent(this@DrinksAdminActivity, AddDrinksActivity::class.java)
                                        intent.putExtra("currentName", name)
                                        intent.putExtra("currentPrice", price)
                                        intent.putExtra("currentDescription", description)
                                        intent.putExtra("currentImage", imagen)

                                        startActivity(intent)
                                        finish()
                                    }

                                    1 -> {
                                        DeletedImageDrinks(name, imagen)
                                    }
                                }
                            }

                            builderDialog.create().show()
                        }
                    })
                    return viewHolderDrinks
                }
            }

        sharedPreferences = this@DrinksAdminActivity.getSharedPreferences("LICORES", MODE_PRIVATE)
        var ordenar_en = sharedPreferences.getString("Ordenar", "Dos")

        if (ordenar_en.equals("Dos")) {
            recyclerViewDrink.layoutManager = GridLayoutManager(this@DrinksAdminActivity, 2)
            firebaseRecyclerAdapter.startListening()
            recyclerViewDrink.adapter = firebaseRecyclerAdapter
        } else if (ordenar_en.equals("Tres")) {
            recyclerViewDrink.layoutManager = GridLayoutManager(this@DrinksAdminActivity, 3)
            firebaseRecyclerAdapter.startListening()
            recyclerViewDrink.adapter = firebaseRecyclerAdapter
        }
    }

    private fun DeletedImageDrinks(currentName: String, currentImage: String) {

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
                        var drinksAxu: Drink? = ds.getValue(Drink::class.java)
                        if(drinksAxu?.image.equals(currentImage)) {
                            ds.ref.removeValue()
                        }
                    }
                    Toast.makeText(
                        this@DrinksAdminActivity,
                        "La imagen ha sido eliminada",
                        Toast.LENGTH_SHORT
                    ).show();
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@DrinksAdminActivity,
                        "" + error.getMessage(),
                        Toast.LENGTH_SHORT
                    ).show();
                }
            })

            var imagenSelect: StorageReference = Firebase.storage.getReferenceFromUrl(currentImage)
            imagenSelect.delete().addOnSuccessListener(object : OnSuccessListener<Void> {
                override fun onSuccess(aVoid: Void?) {
                    Toast.makeText(
                        this@DrinksAdminActivity,
                        "La imagen ha sido eliminada",
                        Toast.LENGTH_SHORT
                    ).show();
                }
            }).addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    Toast.makeText(
                        this@DrinksAdminActivity,
                        "" + e.message,
                        Toast.LENGTH_SHORT
                    ).show();
                }
            })

            alertDialog.dismiss()
        })

        dialogNotBtn.setOnClickListener(View.OnClickListener {

            Toast.makeText(
                this@DrinksAdminActivity,
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
                startActivity(Intent(this, AddDrinksActivity::class.java))
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
