package com.tradition.mobilevtkproject.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.Level
import com.tradition.mobilevtkproject.MAIN
import com.tradition.mobilevtkproject.MainActivity
import com.tradition.mobilevtkproject.MainActivity.Companion.isInternetAvailable
import com.tradition.mobilevtkproject.MainActivity.Companion.successAuth
import com.tradition.mobilevtkproject.User
import com.tradition.mobilevtkproject.databinding.FragmentThirdBinding
import com.tradition.mobilevtkproject.utils.TextFormattingUtils

@Suppress("DEPRECATION")
class ThirdFragmentEmail : Fragment() {

    lateinit var binding: FragmentThirdBinding
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThirdBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.progress = 100

        binding.continueButton.setOnClickListener{
            val pass = binding.editTextPass.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            val currentUser = arguments?.getSerializable("info") as User
            if(pass == "" || email == ""){
                Toast.makeText(MAIN, "Поля не должны быть пустыми", Toast.LENGTH_SHORT).show()
            }
            else if(!TextFormattingUtils.isPassValid(pass)){
                Toast.makeText(MAIN, "Пароль должен быть не меньше 8 символов", Toast.LENGTH_SHORT).show()
            }
            else if(pass.length > 40){
                Toast.makeText(MAIN, "Пароль слишком длинный", Toast.LENGTH_SHORT).show()
            }
            else if(!TextFormattingUtils.isEmailValid(email)){
                Toast.makeText(MAIN, "Введите правильный адрес почты", Toast.LENGTH_SHORT).show()
            }
            else {
                currentUser.email = email
                currentUser.pass = pass
                auth = Firebase.auth

                // Проверка соединения
                if (!isInternetAvailable(MAIN)) {
                    Toast.makeText(MAIN, "Нет подключения к интернету", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                auth.createUserWithEmailAndPassword(currentUser.email, currentUser.pass)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            // Обрабатка ошибок
                            when (task.exception) {
                                is FirebaseAuthUserCollisionException -> {
                                    // Email уже занят
                                    Toast.makeText(MAIN, "Этот email уже зарегистрирован", Toast.LENGTH_LONG).show()
                                }
                                is FirebaseNetworkException -> {
                                    // Ошибка сети
                                    Toast.makeText(MAIN, "Ошибка сети при регистрации", Toast.LENGTH_LONG).show()
                                }
                                else -> {
                                    // Другие ошибки
                                    Toast.makeText(MAIN, "Ошибка регистрации: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                            return@addOnCompleteListener
                        }

                        // Успешная регистрация в Auth
                        val userId = auth.currentUser?.uid ?: run {
                            Toast.makeText(MAIN, "Ошибка получения UID пользователя", Toast.LENGTH_LONG).show()
                            return@addOnCompleteListener
                        }

                        val user = hashMapOf(
                            "authId" to userId,
                            "email" to currentUser.email,
                            "accessLevel" to Level.RegularUser.toString(),
                            "name" to currentUser.name,
                            "surname" to currentUser.surname,
                            "age" to currentUser.age,
                            "balance" to 0
                        )

                        Firebase.firestore.collection("users")
                            .document(userId)
                            .set(user)
                            .addOnSuccessListener {
                                successAuth(currentUser.email)
                            }
                            .addOnFailureListener { e ->
                                auth.currentUser?.delete()
                                when (e) {
                                    is FirebaseFirestoreException -> {
                                        if (e.code == FirebaseFirestoreException.Code.UNAVAILABLE) {
                                            Toast.makeText(MAIN, "Ошибка сети при создании профиля", Toast.LENGTH_LONG).show()
                                        } else {
                                            Toast.makeText(MAIN, "Ошибка создания профиля", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                    else -> {
                                        Toast.makeText(MAIN, "Неизвестная ошибка: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                    }
            }



        }
        binding.imageButton.setOnClickListener{
            (activity as? MainActivity)?.onBackPressed()
        }
    }
}