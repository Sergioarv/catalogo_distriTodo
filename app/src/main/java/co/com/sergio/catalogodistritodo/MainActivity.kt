package co.com.sergio.catalogodistritodo

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import co.com.sergio.catalogodistritodo.fragmentCliente.AboutClienteFragment
import co.com.sergio.catalogodistritodo.fragmentCliente.HomeClienteFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        var toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)

        var navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.itemIconTintList = null

        var toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction().replace(R.id.fragment_content_c, HomeClienteFragment()).commit()
            navigationView.setCheckedItem(R.id.homeCliente)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.homeCliente -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_content_c, HomeClienteFragment()).commit()
            }
            R.id.about -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_content_c, AboutClienteFragment()).commit()
            }
            R.id.login -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true;
    }
}
