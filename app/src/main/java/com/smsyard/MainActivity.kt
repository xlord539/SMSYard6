package com.smsyard
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

class MainActivity: AppCompatActivity() {
    private lateinit var recycler:RecyclerView
    private lateinit var db:AppDatabase
    private val smsUri=Uri.parse("content://sms/inbox")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db=AppDatabase.get(this)
        recycler=findViewById(R.id.recyclerView)
        recycler.layoutManager=LinearLayoutManager(this)
        checkPerms()
    }

    private fun checkPerms(){
        val need = listOf(Manifest.permission.READ_SMS).filter{
            ContextCompat.checkSelfPermission(this,it)!=PackageManager.PERMISSION_GRANTED
        }
        if(need.isNotEmpty()) ActivityCompat.requestPermissions(this,need.toTypedArray(),1)
        else loadSms()
    }

    override fun onRequestPermissionsResult(r:Int,p:Array<out String>,g:IntArray){
        if(r==1 && g.all{it==PackageManager.PERMISSION_GRANTED}) loadSms()
    }

    private fun loadSms(){
        GlobalScope.launch(Dispatchers.IO){
            val paid=db.dao().allIds().toSet()
            val projection=arrayOf("_id","address","body","date")
            val map=linkedMapOf<String,MutableList<Message>>()
            contentResolver.query(smsUri,projection,null,null,"date DESC")?.use{cur->
                val idIdx=cur.getColumnIndex("_id")
                val addrIdx=cur.getColumnIndex("address")
                val bodyIdx=cur.getColumnIndex("body")
                val dateIdx=cur.getColumnIndex("date")
                while(cur.moveToNext()){
                    val id=cur.getLong(idIdx)
                    val sender=cur.getString(addrIdx)?: "Unknown"
                    val body=cur.getString(bodyIdx)
                    val date=cur.getLong(dateIdx)
                    map.getOrPut(sender){ mutableListOf() }
                        .add(Message(id,sender,body,date,paid.contains(id)))
                }
            }
            withContext(Dispatchers.Main){ show(map) }
        }
    }

    private fun show(map:LinkedHashMap<String,MutableList<Message>>){
        val adapters=mutableListOf<RecyclerView.Adapter<*>>()
        for((sender,list) in map){
            val headerAdapter=HeaderAdapter()
            headerAdapter.submitList(listOf(sender))
            val msgAdapter=MessageAdapter{ mark(it) }.apply{ submitList(list) }
            adapters.add(headerAdapter)
            adapters.add(msgAdapter)
        }
        recycler.adapter=ConcatAdapter(adapters)
    }

    private fun mark(m:Message){
        GlobalScope.launch(Dispatchers.IO){
            db.dao().insert(PaidMessage(m.id))
            m.paid=true
            withContext(Dispatchers.Main){ recycler.adapter?.notifyDataSetChanged() }
        }
    }
}
