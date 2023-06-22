package co.com.sergio.catalogodistritodo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity

/**
 * @Author: Sergio Rodriguez Vasquez
 * @Author: carlos Hernan Vasquez
 * @Email: ingsergiorodriguezvasquez@gmail.com
 * @Date: 14/06/2023
 */

class LoadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load);

        // Tiempo de carga antes de entrar al menu
        val TIME: Long = 3000;

        Handler(Looper.getMainLooper()).postDelayed({
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }, TIME);
    }
}
