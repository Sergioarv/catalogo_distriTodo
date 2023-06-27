package co.com.sergio.catalogodistritodo.fragmentAdmin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import co.com.sergio.catalogodistritodo.R
import co.com.sergio.catalogodistritodo.categoryAdmin.candysAdmin.CandysAdminActivity
import co.com.sergio.catalogodistritodo.categoryAdmin.drinksAdmin.DrinksAdminActivity
import co.com.sergio.catalogodistritodo.categoryAdmin.liquorsAdmin.LiquorsAdminActivity
import co.com.sergio.catalogodistritodo.categoryAdmin.othersAdmin.OthersAdminActivity

class HomeAdminFragment : Fragment() {

    lateinit var licores_btn: Button
    lateinit var bebidas_btn: Button
    lateinit var dulces_btn: Button
    lateinit var otros_btn : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view : View = inflater.inflate(R.layout.fragment_home_admin, container, false)

        licores_btn = view.findViewById(R.id.liquorsBtn)
        bebidas_btn = view.findViewById(R.id.drinksBtn)
        dulces_btn = view.findViewById(R.id.candyBtn)
        otros_btn = view.findViewById(R.id.othersBtn)

        licores_btn.setOnClickListener(View.OnClickListener {
            var mainIntet = Intent(activity, LiquorsAdminActivity::class.java)
            startActivity(mainIntet)
        })

        bebidas_btn.setOnClickListener(View.OnClickListener {
            var mainIntet = Intent(activity, DrinksAdminActivity::class.java)
            startActivity(mainIntet)
        })

        dulces_btn.setOnClickListener(View.OnClickListener {
            var mainIntet = Intent(activity, CandysAdminActivity::class.java)
            startActivity(mainIntet)
        })

        otros_btn.setOnClickListener(View.OnClickListener {
            var mainIntet = Intent(activity, OthersAdminActivity::class.java)
            startActivity(mainIntet)
        })

        return view;
    }
}