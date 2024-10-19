package com.esrac.tarifdefteri.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.room.Room
import com.esrac.tarifdefteri.databinding.FragmentRecipeBinding
import com.esrac.tarifdefteri.model.tarif
import com.esrac.tarifdefteri.roomdb.tarifDAO
import com.esrac.tarifdefteri.roomdb.tarifDatabase
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.IOException

private var secilenBitmap : Bitmap? = null
private var secilenGorsel : Uri? = null
private val mDisposable= CompositeDisposable()
private var tarifFromListe : tarif? = null

private lateinit var permissionLauncher: ActivityResultLauncher<String>
private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
private lateinit var db : tarifDatabase
private lateinit var tarifDao : tarifDAO

class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
        db= Room.databaseBuilder(requireContext(),tarifDatabase::class.java,"Tarifler").build()
        tarifDao=db.tarifDAO()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView.setOnClickListener { imageSelect(it) }
        binding.save.setOnClickListener { save(it) }
        binding.delete.setOnClickListener { delete(it) }

        arguments?.let {
            val bilgi= RecipeFragmentArgs.fromBundle(it).bilgi
            println(bilgi)
            if (bilgi=="yeni"){
                binding.delete.isEnabled=false
                binding.save.isEnabled=true
                binding.nameText.setText("")
                binding.ingredientText.setText("")
                binding.recipeText.setText("")
            }else{
                binding.save.isEnabled=false
                binding.delete.isEnabled=true
                val id= RecipeFragmentArgs.fromBundle(it).id
                mDisposable.add(
                    tarifDao.findById(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponse))
            }
        }

    }
    private fun handleResponse(tarif: tarif){
        tarifFromListe=tarif
        binding.nameText.setText(tarif.isim)
        binding.ingredientText.setText(tarif.malzeme)
        binding.recipeText.setText(tarif.tarif)
        val byteArray=tarif.gorsel
        val bitmap=BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
        binding.imageView.setImageBitmap(bitmap)
    }

    fun save(view: View){
        val isim= binding.nameText.toString()
        val malzeme=binding.ingredientText.toString()
        val tarif=binding.recipeText.toString()

        if (secilenBitmap != null){
            val kucukBitmap=bitMapKucult(secilenBitmap!!,300)
            val outputStream= ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray=outputStream.toByteArray()

            val tarif=tarif(isim=isim,malzeme=malzeme, gorsel = byteArray, tarif = tarif)

            mDisposable.add(
                tarifDao.insert(tarif)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponseForInsert)
            )
        }
    }

    private fun handleResponseForInsert(){
        val action= RecipeFragmentDirections.actionRecipeFragmentToListFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }

    fun delete(view: View){
        tarifFromListe?.let {
            mDisposable.add(
                tarifDao.delete(tarifFromListe!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponseForInsert)
            )
        }

    }

    fun imageSelect(view: View){
        activity?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        requireActivity().applicationContext,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.READ_MEDIA_IMAGES
                        )
                    ) {
                        Snackbar.make(
                            view,
                            "Permission needed for gallery",
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction("Give Permission",
                            View.OnClickListener {
                                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            }).show()
                    } else {
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                } else {
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)

                }
            } else {
                if (ContextCompat.checkSelfPermission(
                        requireActivity().applicationContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    ) {
                        Snackbar.make(
                            view,
                            "Permission needed for gallery",
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction("Give Permission",
                            View.OnClickListener {
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }).show()
                    } else {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                } else {
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)

                }
            }
        }
    }


    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    secilenGorsel = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(
                                requireActivity().contentResolver,
                                secilenGorsel!!
                            )
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        } else {
                            secilenBitmap = MediaStore.Images.Media.getBitmap(
                                requireActivity().contentResolver,
                                secilenGorsel
                            )
                            binding.imageView.setImageBitmap(secilenBitmap)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                //izin verildi
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //izin reddedildi
                Toast.makeText(requireContext(), "Permisson needed!", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun bitMapKucult(kullaniciBitMap:Bitmap, maximumBoyut:Int):Bitmap{
        var width=kullaniciBitMap.width
        var height=kullaniciBitMap.height

        var bitmapOran : Double= width.toDouble() / height.toDouble()
        if(bitmapOran > 1){
            width=maximumBoyut
            val kucukHeight= width/bitmapOran
            height=kucukHeight.toInt()
        }else{
            height=maximumBoyut
            val kucukWidth= height*bitmapOran
            width=kucukWidth.toInt()
        }
        return Bitmap.createScaledBitmap(kullaniciBitMap,width,height,true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }

}

