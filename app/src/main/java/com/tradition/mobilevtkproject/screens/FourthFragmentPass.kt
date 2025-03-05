package com.tradition.mobilevtkproject.screens

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.Level
import com.tradition.mobilevtkproject.User
import com.tradition.mobilevtkproject.MAIN
import com.tradition.mobilevtkproject.MainActivity
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.databinding.FragmentFourthBinding
import com.tradition.mobilevtkproject.MainActivity.Companion.isPassValid
import com.tradition.mobilevtkproject.MainActivity.Companion.successAuth

@Suppress("DEPRECATION")
class FourthFragmentPass : Fragment() {

    lateinit var binding: FragmentFourthBinding
    lateinit var auth: FirebaseAuth

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
        binding.progressBar.progress = 100

        binding.continueButton.setOnClickListener{
            val pass = binding.editTextPass.text.toString().trim()
            val currentUser = arguments?.getSerializable("info") as User
            if(!isPassValid(pass)){
                Toast.makeText(MAIN, "Пароль должен быть не меньше 8 символов", Toast.LENGTH_SHORT).show()
            }
            if(pass.length > 40){
                Toast.makeText(MAIN, "Пароль слишком длинный", Toast.LENGTH_SHORT).show()
            }
            else{
                currentUser.pass = pass
                auth = Firebase.auth
                auth.createUserWithEmailAndPassword(currentUser.email,currentUser.pass).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val db = Firebase.firestore
                        var id = auth.currentUser?.uid.toString()
                        db.collection("users")
                            .whereEqualTo("email", currentUser.email)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                if (querySnapshot.isEmpty) {
                                    val user = hashMapOf(
                                        "authId" to id,
                                        "email" to currentUser.email,
                                        "accessLevel" to Level.RegularUser,
                                        "name" to currentUser.name,
                                        "surname" to currentUser.surname,
                                        "age" to currentUser.age,
                                        "balance" to 0
                                    )
                                    db.collection("users")
                                        .add(user)
                                        .addOnSuccessListener {
                                            Log.d(TAG, "DocumentSnapshot added with ID: ${currentUser.email}")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(TAG, "Error adding document", e)
                                        }
                                } else {
                                    Log.d(TAG, "User with email ${currentUser.email} already exists.")
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error getting documents: ", e)
                            }
                        successAuth(auth.currentUser!!.email!!)
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(MAIN,"Нет подключения к интернету",Toast.LENGTH_LONG).show()
                }
            }



        }
        binding.imageButton.setOnClickListener{
            (activity as? MainActivity)?.onBackPressed()
        }
    }

}