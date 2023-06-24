package co.com.sergio.catalogodistritodo

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import co.com.sergio.catalogodistritodo.fragmentAdmin.AddAdminFragment
import co.com.sergio.catalogodistritodo.fragmentAdmin.HomeAdminFragment
import co.com.sergio.catalogodistritodo.fragmentAdmin.RegisterAdminFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainAdminActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var drawerLayout: DrawerLayout
    var auth = Firebase.auth
    var user = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin);

        var toolbar: Toolbar = findViewById(R.id.toolbar_a)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout_a)

        var navigationView: NavigationView = findViewById(R.id.nav_view_a)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.itemIconTintList = null

        var toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_content_a, HomeAdminFragment()).commit()
            navigationView.setCheckedItem(R.id.homeAdmin)
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.homeAdmin -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_content_a, HomeAdminFragment()).commit()
            }

            R.id.registerAdmin -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_content_a, RegisterAdminFragment()).commit()
            }

            R.id.addAdmin -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_content_a, AddAdminFragment()).commit()
            }

            R.id.logout -> {
                cerrarSesion()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true;
    }

    override fun onStart() {
        comprobandoInicioSesion()
        super.onStart()
    }

    private fun comprobandoInicioSesion() {
        if(user != null) {
            Toast.makeText(this, "Se ha iniciado sesión", Toast.LENGTH_SHORT).show()
        }else{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun cerrarSesion(){
        auth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
        Toast.makeText(this, "Cerraste sesión exitosamente", Toast.LENGTH_SHORT).show()
    }
}
