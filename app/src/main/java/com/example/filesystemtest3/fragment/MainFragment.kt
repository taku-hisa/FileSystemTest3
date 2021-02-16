package com.example.filesystemtest3.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.filesystemtest3.MainViewModel
import com.example.filesystemtest3.databinding.FragmentMainBinding

private val category = arrayOf("A","B","C","D","E")

class MainFragment : Fragment() {

    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, category)
        binding.listView.adapter = adapter
        binding.listView.setOnItemClickListener{parent,view,position,id->
            val item = (view.findViewById<TextView>(android.R.id.text1)).text.toString()
            val action = MainFragmentDirections.actionMainFragmentToListFragment(item)
            findNavController().navigate(action)
        }
    }

    override fun onDestroy(){
        super.onDestroy()
        _binding = null
    }
}