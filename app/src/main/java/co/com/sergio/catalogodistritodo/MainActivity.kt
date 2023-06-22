package co.com.sergio.catalogodistritodo

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import co.com.sergio.catalogodistritodo.fragmentAdmin.AddAdminFragment
import co.com.sergio.catalogodistritodo.fragmentAdmin.HomeAdminFragment
import co.com.sergio.catalogodistritodo.fragmentCliente.AboutClienteFragment
import co.com.sergio.catalogodistritodo.fragmentCliente.HomeClienteFragment
import co.com.sergio.catalogodistritodo.ui.theme.Catalogo_distriTodoTheme
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
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true;
    }
}
