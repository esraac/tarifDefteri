package com.esrac.tarifdefteri.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.esrac.tarifdefteri.databinding.FragmentListBinding
import com.esrac.tarifdefteri.databinding.RecyclerRowBinding
import com.esrac.tarifdefteri.model.tarif
import com.esrac.tarifdefteri.view.ListFragmentDirections

class tarifAdapter(val tarifListesi : List<tarif>) : RecyclerView.Adapter<tarifAdapter.tarifHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): tarifHolder {
        val recyclerRowBinding: RecyclerRowBinding= RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return tarifHolder(recyclerRowBinding)
    }

    override fun getItemCount(): Int {
        return tarifListesi.size
    }

    override fun onBindViewHolder(holder: tarifHolder, position: Int) {
        holder.recyclerRowBinding.recyclerViewTextView.text = tarifListesi[position].isim
        holder.itemView.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToRecipeFragment(bilgi = "eski", id = tarifListesi[position].id)
            Navigation.findNavController(it).navigate(action)
        }
    }

    class tarifHolder(val recyclerRowBinding: RecyclerRowBinding) : RecyclerView.ViewHolder(recyclerRowBinding.root){

    }

}