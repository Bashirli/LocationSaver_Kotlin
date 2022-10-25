package com.bashirli.kotlinlocationbook.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bashirli.kotlinlocationbook.databinding.RecyclerBinding
import com.bashirli.kotlinlocationbook.model.Data
import com.bashirli.kotlinlocationbook.view.MainActivity
import com.bashirli.kotlinlocationbook.view.MapsActivity

class Adapter(val list: List<Data>) : RecyclerView.Adapter<Adapter.AdapterHolder>() {
    class AdapterHolder(val recyclerBinding: RecyclerBinding) : RecyclerView.ViewHolder(recyclerBinding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHolder {
    val recyclerBinding=RecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AdapterHolder(recyclerBinding)
    }

    override fun onBindViewHolder(holder: AdapterHolder, position: Int) {
    holder.recyclerBinding.recyclerText.setText(list.get(position).name)
    holder.recyclerBinding.recyclerText.setOnClickListener(){
        var intent=Intent(it.context,MapsActivity::class.java)
        intent.putExtra("data",list.get(position))
        intent.putExtra("info","old")

        it.context.startActivity(intent)
    }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}