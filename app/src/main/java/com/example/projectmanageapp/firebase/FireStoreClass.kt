package com.example.projectmanageapp.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.projectmanageapp.activities.CreateBoardActivity
import com.example.projectmanageapp.activities.MainActivity
import com.example.projectmanageapp.activities.MyProfileActivity
import com.example.projectmanageapp.activities.SignInActivity
import com.example.projectmanageapp.activities.SignUpActivity
import com.example.projectmanageapp.models.Board
import com.example.projectmanageapp.models.User
import com.example.projectmanageapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject

class FireStoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun createBoard(activity: CreateBoardActivity,board:Board){
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener{
                Toast.makeText(activity,"Board created successfully",Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener{
                Toast.makeText(activity,"Error: "+it.message.toString(),Toast.LENGTH_SHORT).show()
                activity.hideProgressDialog()
            }
    }

    fun registerUser(activity: SignUpActivity,userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener{
                activity.userRegisteredSuccess()
            }.addOnFailureListener{
//                Log.e(activity.javaClass.simpleName,it.message.toString())
                activity.hideProgressDialog()
                FirebaseAuth.getInstance().signOut()
                activity.finish()
            }
    }

    fun loadUserData(activity: Activity){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser=document.toObject<User>()!!
                Log.e("data :",loggedInUser.toString())
                when(activity){
                    is SignInActivity    -> activity.signInSuccess(loggedInUser)
                    is MainActivity      -> activity.updateNavigationUserDetails(loggedInUser)
                    is MyProfileActivity -> activity.setUserDataInUI(loggedInUser)
                }
            }
            .addOnFailureListener{ e ->
                when(activity){
                    is SignInActivity -> activity.hideProgressDialog()
                    is MainActivity   -> activity.hideProgressDialog()
                }
            }
    }

    fun getCurrentUserId() : String{
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID=""
        if( currentUser!=null){
            currentUserID= currentUser.uid
        }
        return currentUserID
    }

    fun updateUserProfileData(activity: MyProfileActivity,
                              userHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
//                Log.e(a)
                Toast.makeText(activity,"Profile update successfully!",Toast.LENGTH_SHORT).show()

                activity.profileUpdateSuccess()
            }.addOnFailureListener{
                Toast.makeText(activity,"Profile update error!!"+it.message,Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }

    }
}