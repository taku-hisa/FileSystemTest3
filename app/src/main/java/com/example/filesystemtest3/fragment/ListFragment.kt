package com.example.filesystemtest3.fragment

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.filesystemtest3.MainViewModel
import com.example.filesystemtest3.adapter.itemAdapter
import com.example.filesystemtest3.data.entity.item
import com.example.filesystemtest3.databinding.FragmentListBinding
import java.io.ByteArrayOutputStream
import java.io.InputStream

private val category = arrayOf("A","B","C","D","E") //カテゴリを定義
private var CATEGORY_CODE: Int = 42                 //カテゴリの変数

class ListFragment : Fragment() {

    private var _binding : FragmentListBinding? = null
    private val binding get() = _binding!!
    private val args: ListFragmentArgs by navArgs()
    private val viewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Observer設置
        viewModel.getItem.observe( viewLifecycleOwner, Observer {
            setRecyclerView(it)
        })
        binding.fab.setOnClickListener{
            CATEGORY_CODE = 0 //初期選択="A"
            AlertDialog.Builder(requireContext()).apply {
                setTitle("SELECT")
                setSingleChoiceItems(category, 0) { _, i -> //初期選択="A"
                    CATEGORY_CODE = i  // 選択した項目を保持
                }
                setPositiveButton("OK") { _, _ ->
                    if (CATEGORY_CODE < category.size + 1) {
                        intent()
                    }
                }
                setNegativeButton("Cancel", null)
            }.show()
        }
    }

    //RecyclerView表示処理
    private fun setRecyclerView (items:List<item>) {
        //Roomは長時間UIスレッドをロックする可能性がある処理をメインスレッドでは行えないようになっている
        val bitmapList = mutableListOf<Bitmap>()
        for (i in items) {
            val bitmap = BitmapFactory.decodeByteArray(i.image, 0, i.image.size)
            bitmapList.add(bitmap)
        }
        binding.recyclerView.apply {
            layoutManager =
                when {
                    resources.configuration.orientation
                            == Configuration.ORIENTATION_PORTRAIT
                    -> GridLayoutManager(requireContext(), 2)
                    else
                    -> GridLayoutManager(requireContext(), 4)
                }
            adapter = itemAdapter(context, bitmapList).apply {
                //画面遷移
                setOnItemClickListener { position: Int ->
                    val action = items[position]?.let {
                        ListFragmentDirections.actionListFragmentToDetailFragment(it.id)
                    }
                    if (action != null) findNavController().navigate(action)
                }
            }
        }
    }

    //DB登録処理
    private fun intent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        this.startActivityForResult(intent, CATEGORY_CODE)
    }

    //DB登録処理
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode != RESULT_OK) {
            return
        }
        when (requestCode) {
            CATEGORY_CODE -> {
                val uri = resultData?.data
                if (uri != null) {
                    //一枚選択時の動作
                    val inputStream = getActivity()?.getContentResolver()?.openInputStream(uri)
                    if (inputStream != null) saveImage( inputStream, 999)
                } else {
                    //複数枚選択時の動作
                    val clipData = resultData?.clipData
                    val clipItemCount = clipData?.itemCount?.minus(1) //エラーになるので、数字を１減らす。
                    for (i in 0..clipItemCount!!) {
                        val item = clipData.getItemAt(i).uri
                        val inputStream = getActivity()?.getContentResolver()?.openInputStream(item)
                        if (inputStream != null) saveImage( inputStream, i)
                    }
                }
                Toast.makeText(requireContext(), "保存完了", Toast.LENGTH_LONG).show()
            }
        }
    }

    //DB登録処理
    fun saveImage(inputStream: InputStream, int:Int) {
        try {
            ByteArrayOutputStream().use { byteArrOutputStream ->
                val image = BitmapFactory.decodeStream(inputStream)
                image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrOutputStream)
                val byte = byteArrOutputStream.toByteArray()
                insertItem(byte)
                inputStream.close() //明示的に閉じる
            }
        }catch(e:Exception){
            println("エラー発生")
        }
    }

    //DB登録処理
    fun insertItem(byteArray: ByteArray){
        //エラー　行が大きすぎてCursorWindowに収まらない　→　BLOB
        val item = item(0,CATEGORY_CODE.toString(),byteArray,"")
        viewModel.insertItem(item)
    }

    override fun onDestroy(){
        super.onDestroy()
        _binding = null
    }
}