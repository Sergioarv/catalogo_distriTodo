package co.com.sergio.catalogodistritodo.categoryAdmin.candysAdmin

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

class CandysAdminActivity : AppCompatActivity() {

    lateinit var recyclerViewCandy: RecyclerView;
    lateinit var mFirebaseDatabase: FirebaseDatabase;
    lateinit var mReference: DatabaseReference;

    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<Candy, ViewHolderCandys>;
    lateinit var options: FirebaseRecyclerOptions<Candy>

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
        mReference = mFirebaseDatabase.getReference("LICORES")

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
                        .inflate(co.com.sergio.catalogodistritodo.R.layout.item_candys, parent, false)
                    val viewHolderCandys = ViewHolderCandys(itemView)
                    viewHolderCandys.setOnClickListener(object : ViewHolderCandys.ClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            Toast.makeText(this@CandysAdminActivity, "Item Click", Toast.LENGTH_SHORT).show()
                        }

                        override fun onItemLongClick(view: View, position: Int) {
                            Toast.makeText(this@CandysAdminActivity, "Long Click", Toast.LENGTH_SHORT).show()
                        }
                    })
                    return viewHolderCandys
                }
            }

        recyclerViewCandy.layoutManager = GridLayoutManager(this@CandysAdminActivity, 2)
        firebaseRecyclerAdapter.startListening()
        recyclerViewCandy.adapter = firebaseRecyclerAdapter
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
