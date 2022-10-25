package com.bashirli.kotlinlocationbook.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.bashirli.kotlinlocationbook.R
import com.bashirli.kotlinlocationbook.adapter.Adapter
import com.bashirli.kotlinlocationbook.databinding.ActivityMainBinding
import com.bashirli.kotlinlocationbook.model.Data
import com.bashirli.kotlinlocationbook.roomdb.RoomDAO
import com.bashirli.kotlinlocationbook.roomdb.RoomDB
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
   lateinit var binding: ActivityMainBinding
   var compositeDisposable=CompositeDisposable()
    lateinit var roomDB: RoomDB
    lateinit var roomDAO: RoomDAO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        var view=binding.root
        setContentView(view)
             roomDB= Room.databaseBuilder(applicationContext,RoomDB::class.java,"Book").build()
        roomDAO=roomDB.getDAO()

        compositeDisposable.add(roomDAO.getAll().subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribe(this::handler))

    }
fun handler(list: List<Data>){
    binding.recycler.layoutManager=LinearLayoutManager(this)
    val adapter=Adapter(list)
    binding.recycler.adapter=adapter
}



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(R.id.elaveEt ==item.itemId){
            var intent=Intent(this@MainActivity, MapsActivity::class.java)
            startActivity(intent)
            intent.putExtra("info","new")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    var menuInflater=MenuInflater(this)
        menuInflater.inflate(R.menu.menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

}