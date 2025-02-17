package com.tradition.mobilevtkproject.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.tradition.mobilevtkproject.AppDatabase
import com.tradition.mobilevtkproject.User
import com.tradition.mobilevtkproject.MAIN
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.databinding.FragmentFourthBinding
import com.tradition.mobilevtkproject.MainActivity.Companion.isPassValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
class FourthFragmentPass : Fragment() {

    lateinit var binding: FragmentFourthBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFourthBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    val bundle = Bundle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.setProgress(100)

        binding.continueButton.setOnClickListener{
            val pass = binding.editTextPass.text.toString().trim()
            val currentUser = arguments?.getSerializable("info") as User
            if(!isPassValid(pass)){
                Toast.makeText(MAIN, "Пароль должен быть не меньше 8 символов", Toast.LENGTH_SHORT).show()
            }
            else{
                currentUser.pass = pass
                var db = AppDatabase.getInstance(MAIN)
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        db.getDao().insertItem(currentUser)
                    }
                    val user = db.getDao().getUserByEmail(currentUser.email)
                    bundle.putInt("UserId", user?.id?.toInt() ?: -1)
                    MAIN.navController.navigate(R.id.action_fourthFragment_to_mainFragment, bundle)
                }
            }



        }
        binding.imageButton.setOnClickListener{
            MAIN.navController.popBackStack()
        }
    }

}