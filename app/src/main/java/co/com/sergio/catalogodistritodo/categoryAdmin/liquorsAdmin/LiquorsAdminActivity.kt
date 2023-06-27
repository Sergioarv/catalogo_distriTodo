package co.com.sergio.catalogodistritodo.categoryAdmin.liquorsAdmin

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
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
                        .inflate(co.com.sergio.catalogodistritodo.R.layout.item_liquors, parent, false)
                    val viewHolderLiquors = ViewHolderLiquors(itemView)
                    viewHolderLiquors.setOnClickListener(object : ViewHolderLiquors.ClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            Toast.makeText(this@LiquorsAdminActivity, "Item Click", Toast.LENGTH_SHORT).show()
                        }

                        override fun onItemLongClick(view: View, position: Int) {
                            Toast.makeText(this@LiquorsAdminActivity, "Long Click", Toast.LENGTH_SHORT).show()
                        }
                    })
                    return viewHolderLiquors
                }
            }

        recyclerViewLiquor.layoutManager = GridLayoutManager(this@LiquorsAdminActivity, 2)
        firebaseRecyclerAdapter.startListening()
        recyclerViewLiquor.adapter = firebaseRecyclerAdapter
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
                startActivity(Intent(this, AddLiquorsActivity::class.java))
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