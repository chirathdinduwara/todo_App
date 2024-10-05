package com.example.doit



import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.doit.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var viewTasks: ViewTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFrag(Home())

        binding.navigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFrag(Home())
                R.id.todo -> replaceFrag(TodoFragment())
                R.id.profile -> replaceFrag(profile())


                else -> {}
            }
            true
        }

    }


    private fun replaceFrag(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }


}

