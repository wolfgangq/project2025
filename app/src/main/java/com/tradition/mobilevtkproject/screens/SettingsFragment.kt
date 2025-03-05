package com.tradition.mobilevtkproject.screens

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.tradition.mobilevtkproject.MAIN2
import com.tradition.mobilevtkproject.MainActivity.Companion.getUserInfo
import com.tradition.mobilevtkproject.MainActivity.Companion.isPassValid
import com.tradition.mobilevtkproject.TransitionActivity.Companion.setColors
import com.tradition.mobilevtkproject.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.toString
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.TransitionActivity

class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    val auth = FirebaseAuth.getInstance()
    var user = auth.currentUser
    var bundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textViewSize.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val constraintHeight = binding.textViewSize.height
                val constraintWidth = binding.textViewSize.width

                //binding.textViewBalance.height = (constraintHeight * 0.04).toInt()

                val c1 = (constraintHeight * 0.03).toInt()
                binding.textViewName.height = c1
                binding.textViewSurname.height = c1
                binding.textViewEmail.height = c1
                binding.textViewAge.height = c1
                binding.textViewNewPass.height = c1
                binding.textViewOldPass.height = c1

                val c2 = (constraintHeight * 0.06).toInt()
                binding.editTextName2.height = c2
                binding.editTextSurname2.height = c2
                binding.editTextEmail2.height = c2
                binding.editTextAge2.height = c2
                binding.editTextNewPass.height = c2
                binding.editTextOldPass.height = c2

                binding.buttonSaveChanges.height = (constraintHeight * 0.07).toInt()
                binding.buttonChangePass.height = (constraintHeight * 0.07).toInt()

                binding.textViewChangePass.height = (constraintHeight * 0.04).toInt()

                binding.textViewBufer.height = (constraintHeight * 0.5).toInt()

                binding.textViewForgotPass2.height = (constraintHeight * 0.06).toInt()

                binding.textViewSize.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        binding.imageButtonBack.setOnClickListener {
            (activity as? TransitionActivity)?.onBackPressed()
        }
        binding.buttonSaveChanges.setOnClickListener{
            lifecycleScope.launch {
                var id = auth.currentUser?.uid.toString()
                val db = Firebase.firestore
                var currentUser = getUserInfo(id)
                try {
                    val snapshot = db.collection("users").whereEqualTo("authId", id).get().await()
                    var k = 0
                    if (!snapshot.isEmpty) {
                        val document = snapshot.documents[0]
                        val inputName = binding.editTextName2.text.toString()
                        var inputSurname = binding.editTextSurname2.text.toString()
                        var inputAge: Double = 0.0
                        var curName = currentUser?.get("name").toString()
                        var curSurname = currentUser?.get("surname").toString()
                        var curAge = currentUser?.get("age").toString().toDouble()
                        var a = binding.editTextAge2.text.toString()
                        if(a != ""){
                            inputAge = a.toDouble()
                        }
                        if(inputName != curName && inputName != ""){
                            document.reference.update("name", inputName)
                        }
                        else{
                            k += 1
                            binding.editTextName2.setText(curName)
                        }
                        if(inputSurname != curSurname && inputSurname != ""){
                            document.reference.update("surname", inputSurname)
                        }
                        else{
                            k += 1
                            binding.editTextSurname2.setText(curSurname)
                        }
                        if(inputAge != curAge && !(inputAge < 4 || inputAge > 130)){
                            document.reference.update("age", inputAge.toInt())
                        }
                        else{
                            k += 1
                            binding.editTextAge2.setText(curAge.toInt().toString())
                        }

                        if(k == 3){
                            Toast.makeText(MAIN2, "Введите изменения", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(MAIN2, "Изменения применены", Toast.LENGTH_SHORT).show()
                        }
                        Log.d("Firestore", "Field updated successfully.")
                    }
                } catch (e: Exception) {
                    Log.w("Firestore", "Error updating document.", e)
                }
                /*if(binding.editTextName2.text.toString() != currentUser?.get("name").toString()){
                    updateUserField(id, "name", binding.editTextName2.text.toString())
                }
                if(binding.editTextSurname2.text.toString() != currentUser?.get("surname").toString()){
                    updateUserField(id, "surname", binding.editTextSurname2.text.toString())
                }
                if(binding.editTextAge2.text.toString().toInt() != currentUser?.get("age")){
                    updateUserField(id, "age", binding.editTextAge2.text.toString().toInt())
                }*/
            }
        }
        var f = false
        binding.imageButtonChangeTypeOld.setOnClickListener{
            var b = binding.editTextOldPass
            var curStart = b.selectionStart
            var curEnd = b.selectionEnd
            when(f){
                false -> {
                    b.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    b.setSelection(curStart, curEnd)
                    binding.imageButtonChangeTypeOld.setImageResource(R.drawable.opened_eye)
                    f = true
                }
                true -> {
                    b.transformationMethod = PasswordTransformationMethod.getInstance()
                    b.setSelection(curStart, curEnd)
                    binding.imageButtonChangeTypeOld.setImageResource(R.drawable.closed_eye)
                    f = false
                }
            }
            /*val curType = binding.editTextOldPass.inputType
            when(curType){
                129 -> binding.editTextOldPass.inputType = 144
                144 -> binding.editTextOldPass.inputType = 129
            }*/
        }
        binding.imageButtonChangeTypeNew.setOnClickListener{
            var b = binding.editTextNewPass
            var curStart = b.selectionStart
            var curEnd = b.selectionEnd
            when(f){
                false -> {
                    b.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    b.setSelection(curStart, curEnd)
                    binding.imageButtonChangeTypeNew.setImageResource(R.drawable.opened_eye)
                    f = true
                }
                true -> {
                    b.transformationMethod = PasswordTransformationMethod.getInstance()
                    b.setSelection(curStart, curEnd)
                    binding.imageButtonChangeTypeNew.setImageResource(R.drawable.closed_eye)
                    f = false
                }
            }
            /*val curType = binding.editTextOldPass.inputType
            when(curType){
                129 -> binding.editTextOldPass.inputType = 144
                144 -> binding.editTextOldPass.inputType = 129
            }*/
        }

        binding.editTextOldPass.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }
            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

            }
            override fun afterTextChanged(s: Editable?) {
                val b = binding.editTextOldPass
                var t = b.text.toString().replace(" ", "").replace(".", "").replace(",", "")
                var curStart = b.selectionStart - 1
                var curEnd = b.selectionEnd - 1
                if (b.text.toString() != t){
                    b.setText(t)
                    b.setSelection(curStart, curEnd)
                }
                if (b.text.toString() != ""){
                    binding.imageButtonChangeTypeOld.visibility = View.VISIBLE
                }
                else{
                    binding.imageButtonChangeTypeOld.visibility = View.GONE
                }
            }
        })
        binding.editTextNewPass.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }
            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

            }
            override fun afterTextChanged(s: Editable?) {
                val b = binding.editTextNewPass
                var t = b.text.toString().replace(" ", "").replace(".", "").replace(",", "")
                var curStart = b.selectionStart - 1
                var curEnd = b.selectionEnd - 1
                if (b.text.toString() != t){
                    b.setText(t)
                    b.setSelection(curStart, curEnd)
                }
                if (b.text.toString() != ""){
                    binding.imageButtonChangeTypeNew.visibility = View.VISIBLE
                }
                else{
                    binding.imageButtonChangeTypeNew.visibility = View.GONE
                }
            }
        })

        binding.buttonChangePass.setOnClickListener{
            val oldpass = binding.editTextOldPass.text.toString()
            val newpass = binding.editTextNewPass.text.toString()
            if(oldpass == "" || newpass == ""){
                Toast.makeText(MAIN2, "Поля не могут быть пустыми", Toast.LENGTH_SHORT).show()
            }
            else if(oldpass == newpass){
                Toast.makeText(MAIN2, "Старый и новый пароли не могут совпадать", Toast.LENGTH_SHORT).show()
            }
            else if(newpass.length > 40){
                Toast.makeText(MAIN2, "Новый пароль слишком длинный", Toast.LENGTH_SHORT).show()
            }
            else if(!isPassValid(newpass)){
                Toast.makeText(MAIN2, "Новый пароль должен быть не менее 8 символов", Toast.LENGTH_SHORT).show()
            }
            else{
                val credential = EmailAuthProvider.getCredential(user?.email.toString(), oldpass)
                user?.reauthenticate(credential)?.addOnSuccessListener {
                    Log.d(TAG, "User re-authenticated.")
                    user?.updatePassword(newpass)
                    Toast.makeText(MAIN2, "Пароль успешно сменен", Toast.LENGTH_SHORT).show()
                    binding.editTextOldPass.setText("")
                    binding.editTextNewPass.setText("")
                }?.addOnFailureListener{
                    Toast.makeText(MAIN2, "Не удалось сменить пароль", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.textViewForgotPass2.setOnClickListener{
            bundle.putString("Email", binding.editTextEmail2.text.toString())
            (activity as? TransitionActivity)?.goFragment("Map", ForgotPasswordFragment(), bundle)
        }
        lifecycleScope.launch {
            val auth = FirebaseAuth.getInstance()
            var id = auth.currentUser?.uid.toString()
            var currentUser = getUserInfo(id)
            binding.editTextName2.setText(currentUser?.get("name").toString())
            binding.editTextSurname2.setText(currentUser?.get("surname").toString())
            binding.editTextEmail2.setText(currentUser?.get("email").toString())
            binding.editTextAge2.setText(currentUser?.get("age").toString())
        }
    }

    override fun onResume() {
        super.onResume()
        setColors(requireActivity(), "mainGreen")
    }

    }