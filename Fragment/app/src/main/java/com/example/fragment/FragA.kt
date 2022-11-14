package com.example.fragment

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
//import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.fragment.databinding.FragmentFragABinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragA.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragA : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding:FragmentFragABinding?=null
    private val binding get()= _binding!!

    var mainActivity:MainActivity?=null
    private var flag = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFragABinding.inflate(inflater, container, false)
        binding.textView.text=arguments?.getString("key")?:"This is Frag A"
//        return inflater.inflate(R.layout.fragment_frag_a, container, false)

        binding.btnShowFragmentC.setOnClickListener{
            if (flag==0) {
                mainActivity!!.openFragmentOnFrameLayoutB(1)
                flag=1
            }
            else {
                mainActivity!!.openFragmentOnFrameLayoutB(2)
                flag=0
            }
        }


        binding.btnChangeTextFragmentC.setOnClickListener{
            val items=arrayOf("Hi from Frag A", "what's going on?")
            var selectedItem = "default"
            var builder=AlertDialog.Builder(mainActivity!!)
                .setTitle("Select Sentence")
                .setSingleChoiceItems(items,-1){ dialogInterface: DialogInterface, i:Int ->
                    selectedItem = items[i]
                }
                .setPositiveButton("Select"){dialogInterface:DialogInterface, i:Int ->
                    mainActivity!!.changeReqFromFragA(selectedItem)
                }
            builder.show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // companion object {
    //     /**
    //      * Use this factory method to create a new instance of
    //      * this fragment using the provided parameters.
    //      *
    //      * @param param1 Parameter 1.
    //      * @param param2 Parameter 2.
    //      * @return A new instance of fragment FragA.
    //      */
    //     // TODO: Rename and change types and number of parameters
    //     @JvmStatic
    //     fun newInstance(param1: String, param2: String) =
    //         FragA().apply {
    //             arguments = Bundle().apply {
    //                 putString(ARG_PARAM1, param1)
    //                 putString(ARG_PARAM2, param2)
    //             }
    //         }
    // }
}