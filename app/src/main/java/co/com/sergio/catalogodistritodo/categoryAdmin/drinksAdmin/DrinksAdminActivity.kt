package co.com.sergio.catalogodistritodo.categoryAdmin.drinksAdmin

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.com.sergio.catalogodistritodo.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DrinksAdminActivity : AppCompatActivity() {

    lateinit var recyclerViewDrink: RecyclerView;
    lateinit var mFirebaseDatabase: FirebaseDatabase;
    lateinit var mReference: DatabaseReference;

    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<Drink, ViewHolderDrinks>;
    lateinit var options: FirebaseRecyclerOptions<Drink>

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
                        .inflate(co.com.sergio.catalogodistritodo.R.layout.item_drinks, parent, false)
                    val viewHolderDrinks = ViewHolderDrinks(itemView)
                    viewHolderDrinks.setOnClickListener(object : ViewHolderDrinks.ClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            Toast.makeText(this@DrinksAdminActivity, "Item Click", Toast.LENGTH_SHORT).show()
                        }

                        override fun onItemLongClick(view: View, position: Int) {
                            Toast.makeText(this@DrinksAdminActivity, "Long Click", Toast.LENGTH_SHORT).show()
                        }
                    })
                    return viewHolderDrinks
                }
            }

        recyclerViewDrink.layoutManager = GridLayoutManager(this@DrinksAdminActivity, 2)
        firebaseRecyclerAdapter.startListening()
        recyclerViewDrink.adapter = firebaseRecyclerAdapter
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
