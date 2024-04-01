package com.example.foodmania.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.foodmania.databinding.FragmentProfileBinding
import com.example.foodmania.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        binding.apply {

            profileEditTextName.isEnabled = false
            profileEditTextAddress.isEnabled = false
            profileEditTextEmail.isEnabled = false
            profileEditTextPhone.isEnabled = false

            // Inflate the layout for this fragment
            binding.editBtn.setOnClickListener {


                profileEditTextName.isEnabled = !profileEditTextName.isEnabled
                profileEditTextEmail.isEnabled = !profileEditTextEmail.isEnabled
                profileEditTextAddress.isEnabled = !profileEditTextAddress.isEnabled
                profileEditTextPhone.isEnabled = !profileEditTextPhone.isEnabled

            }
        }

        setUserData()

        binding.saveInfoBtn.setOnClickListener {
            val name = binding.profileEditTextName.text.toString()
            val address = binding.profileEditTextAddress.text.toString()
            val email = binding.profileEditTextEmail.text.toString()
            val phone = binding.profileEditTextPhone.text.toString()

            updateUserData(name, address, email, phone)
        }


        return binding.root
    }

    private fun updateUserData(name: String, address: String, email: String, phone: String) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val userReference = database.getReference("user").child(userId)

            val userData = hashMapOf(
                "name" to name,
                "address" to address,
                "email" to email,
                "phone" to phone,
            )

            userReference.setValue(userData).addOnSuccessListener {
                binding.apply {
                    profileEditTextName.isEnabled = false
                    profileEditTextAddress.isEnabled = false
                    profileEditTextEmail.isEnabled = false
                    profileEditTextPhone.isEnabled = false
                }


                Toast.makeText(requireContext(), "Profile is Updated", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Profile is not updated try again !!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userReference = database.getReference("user").child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userProfile = snapshot.getValue(UserModel::class.java)

                        if (userProfile != null) {
                            //binding . (id from xml) . setText ((variableto get data from UserModel.kt activity) . (variable name from UserModel.kt activity))
                            binding.profileEditTextName.setText(userProfile.name)
                            binding.profileEditTextAddress.setText(userProfile.address)
                            binding.profileEditTextEmail.setText(userProfile.email)
                            binding.profileEditTextPhone.setText(userProfile.phone)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }


}