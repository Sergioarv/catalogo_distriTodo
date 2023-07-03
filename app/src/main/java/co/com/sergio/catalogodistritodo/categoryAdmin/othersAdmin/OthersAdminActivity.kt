package co.com.sergio.catalogodistritodo.categoryAdmin.othersAdmin

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

class OthersAdminActivity : AppCompatActivity() {

    lateinit var recyclerViewOther: RecyclerView;
    lateinit var mFirebaseDatabase: FirebaseDatabase;
    lateinit var mReference: DatabaseReference;

    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<Other, ViewHolderOthers>;
    lateinit var options: FirebaseRecyclerOptions<Other>

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
        mReference = mFirebaseDatabase.getReference("LICORES")

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
                            Toast.makeText(this@OthersAdminActivity, "Long Click", Toast.LENGTH_SHORT).show()
                        }
                    })
                    return viewHolderOthers
                }
            }

        recyclerViewOther.layoutManager = GridLayoutManager(this@OthersAdminActivity, 2)
        firebaseRecyclerAdapter.startListening()
        recyclerViewOther.adapter = firebaseRecyclerAdapter
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