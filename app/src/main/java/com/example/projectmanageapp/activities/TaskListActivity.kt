package com.example.projectmanageapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmanageapp.R
import com.example.projectmanageapp.adapter.TaskListItemsAdapter
import com.example.projectmanageapp.databinding.ActivityTaskListBinding
import com.example.projectmanageapp.firebase.FireStoreClass
import com.example.projectmanageapp.models.Board
import com.example.projectmanageapp.models.Card
import com.example.projectmanageapp.models.Task
import com.example.projectmanageapp.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var mBoardDocumentId:String
    private lateinit var mBoardDetails:Board
    private var binding:ActivityTaskListBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            mBoardDocumentId= intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this,mBoardDocumentId)
    }

    fun addCardToTaskList(position: Int,cardName:String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        val cardAssignedUsersList:ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(FireStoreClass().getCurrentUserId())
        val card = Card(cardName,FireStoreClass().getCurrentUserId(),cardAssignedUsersList)
        val cardList=mBoardDetails.taskList[position].cards
        cardList.add(card)
        val task=Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardList
        )
        mBoardDetails.taskList[position]=task
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }


    fun createTaskList(taskListName : String){
        val task=Task(taskListName,FireStoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(mBoardDetails.taskList.size-1,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun updateTaskList(position: Int,listName:String,model:Task){
        val task=Task(listName, model.createdBy)
        mBoardDetails.taskList[position]=task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun boardDetails(board: Board){
        mBoardDetails=board
        hideProgressDialog()
        setUpActionBar()
        val addTaskList= Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)
        binding?.rvTaskList?.layoutManager=LinearLayoutManager(
            this,LinearLayoutManager.HORIZONTAL,false)
        binding?.rvTaskList?.setHasFixedSize(true)
        val adapter=TaskListItemsAdapter(this,board.taskList)
        binding?.rvTaskList?.adapter=adapter
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this,mBoardDetails.documentId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members->{
                val intent=Intent(this,MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAILS,mBoardDetails)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding?.toolbarTaskListActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        supportActionBar?.title=mBoardDetails.name
        binding?.toolbarTaskListActivity?.setNavigationOnClickListener {
            finish()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }



}