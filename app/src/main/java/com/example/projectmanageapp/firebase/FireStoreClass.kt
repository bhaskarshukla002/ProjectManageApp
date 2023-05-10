package com.example.projectmanageapp.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.projectmanageapp.activities.CreateBoardActivity
import com.example.projectmanageapp.activities.MainActivity
import com.example.projectmanageapp.activities.MembersActivity
import com.example.projectmanageapp.activities.MyProfileActivity
import com.example.projectmanageapp.activities.SignInActivity
import com.example.projectmanageapp.activities.SignUpActivity
import com.example.projectmanageapp.activities.TaskListActivity
import com.example.projectmanageapp.models.Board
import com.example.projectmanageapp.models.User
import com.example.projectmanageapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject

class FireStoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun getAssignedMembersListDetails(activity: MembersActivity, assignedTo: ArrayList<String>) {
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.ID,assignedTo)
            .get()
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,it.documents.toString())
                val userList:ArrayList<User> =ArrayList<User>()
                for(i in it.documents){
                    val user=i.toObject<User>()
                    userList.add(user!!)
                }
                activity.setUpMemberList(userList)
            }
            .addOnFailureListener {
                Log.e(activity.javaClass.simpleName,it.message.toString())
            }
    }

    fun addUpdateTaskList(activity:TaskListActivity,board: Board){
        val taskListHM=HashMap<String,Any>()
        taskListHM[Constants.TASK_LIST]=board.taskList
        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHM)
            .addOnSuccessListener {
                Toast.makeText(activity,"Board Updated successfully!",Toast.LENGTH_SHORT).show()

                activity.addUpdateTaskListSuccess()
            }.addOnFailureListener{
                Toast.makeText(activity,"error while creating board!!"+it.message,Toast.LENGTH_SHORT).show()
                activity.hideProgressDialog()
            }
    }

    fun getBoardDetails(activity: TaskListActivity,documentId:String){
        mFireStore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, it.toString())
                val board=it.toObject<Board>()
                board?.documentId=it.id
                activity.boardDetails(board!!)
            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,it.message.toString())
            }
    }

    fun getBoardList(activity: MainActivity){
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, it.documents.toString())
                val boardList : ArrayList<Board> = ArrayList()
                for(i in it.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentId=i.id
                    boardList.add(board)
                }
                activity.populateBoardListToUI(boardList)
            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,it.message.toString())
            }
    }

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

    fun loadUserData(activity: Activity, readBoardList:Boolean=false){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser=document.toObject<User>()!!
                Log.e("data :",loggedInUser.toString())
                when(activity){
                    is SignInActivity    -> activity.signInSuccess(loggedInUser)
                    is MainActivity      -> activity.updateNavigationUserDetails(loggedInUser , readBoardList)
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