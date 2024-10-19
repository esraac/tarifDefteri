package com.esrac.tarifdefteri.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.esrac.tarifdefteri.adapter.tarifAdapter
import com.esrac.tarifdefteri.databinding.FragmentListBinding
import com.esrac.tarifdefteri.model.tarif
import com.esrac.tarifdefteri.roomdb.tarifDAO
import com.esrac.tarifdefteri.roomdb.tarifDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var db : tarifDatabase
    private lateinit var tarifDao : tarifDAO
    private val mDisposable =CompositeDisposable()
    private lateinit var adapter: tarifAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db= Room.databaseBuilder(requireContext(),tarifDatabase::class.java,"Tarifler").build()
        tarifDao = db.tarifDAO()
    }
    private fun handleResponse(tarifler: List<tarif>){
        val adapter=tarifAdapter(tarifler)
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerView.adapter=adapter

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener{(newItem(it))}
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        verileriAl()

    }


    private fun verileriAl(){
        mDisposable.add(tarifDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )
    }

    fun newItem(view: View){
        val action= ListFragmentDirections.actionListFragmentToRecipeFragment(bilgi = "yeni", id = -1)
        Navigation.findNavController(view).navigate(action)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }
}