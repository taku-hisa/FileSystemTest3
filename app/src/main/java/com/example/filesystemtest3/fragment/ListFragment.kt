package com.example.filesystemtest3.fragment

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.filesystemtest3.MainViewModel
import com.example.filesystemtest3.adapter.itemAdapter
import com.example.filesystemtest3.databinding.FragmentListBinding


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
        //最初の読み込み
        setRecyclerView(args.category)
        //追加後の読み込み
        viewModel.getLiveData.observe( viewLifecycleOwner, Observer {

        })
    }

    private fun setRecyclerView (category:String) {
        //　↓　ここでエラー　Roomは長時間UIスレッドをロックする可能性がある処理をメインスレッドでは行えないようになっている
        val items = viewModel.getItem(args.category)
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

    override fun onDestroy(){
        super.onDestroy()
        _binding = null
    }
}