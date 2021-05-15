package com.dovoo.memesnetwork.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.databinding.FragmentSectionPickerBinding
import com.dovoo.memesnetwork.model.Section
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel

class SectionPickerFragment : Fragment() {
    private var _binding: FragmentSectionPickerBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()
    val listSections: ArrayList<Section> = ArrayList()
    val selectedListSections: ArrayList<Section> = ArrayList()
    lateinit var adapter: SectionRecyclerViewAdapter

    private val textChangeListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            filter(s.toString())
        }
    }

    private val sectionOnClickListener = View.OnClickListener {
        val data = (it.tag as SectionRecyclerViewAdapter.MyViewHolder).data
        binding.etSection.setText(data.name)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = SectionRecyclerViewAdapter(
            requireContext(),
            selectedListSections,
            sectionOnClickListener
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSectionPickerBinding.inflate(inflater, container, false)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        binding.etSection.removeTextChangedListener(textChangeListener)
        binding.etSection.addTextChangedListener(textChangeListener)
        binding.linBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.linBtnAdd.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set("selectedSection", binding.etSection.text.toString())
            findNavController().popBackStack()
        }
        fetchSection()
        return binding.root
    }

    private fun filter(str: String) {
        selectedListSections.clear()
        listSections.forEach {
            if (it.name.contains(str)) selectedListSections.add(it)
        }
        adapter.notifyDataSetChanged()
    }

    private fun fetchSection() {
        generalViewModel.fetchTopSections(binding.etSection.text.toString())
            .observe(viewLifecycleOwner, {
                when (it.status) {
                    Status.SUCCESS -> {
                        listSections.clear()
                        listSections.addAll(it.data!!.sections)
                        selectedListSections.addAll(it.data.sections)
                        adapter.notifyDataSetChanged()
                    }
                    Status.ERROR -> {
                        System.out.println("BBBB")
                    }
                }
            })
    }

    class SectionRecyclerViewAdapter(
        private val context: Context,
        private val itemList: List<Section>,
        private val onClickListener: View.OnClickListener
    ) : RecyclerView.Adapter<SectionRecyclerViewAdapter.MyViewHolder>() {

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var linBtnSection: LinearLayout
            var tvSection: TextView
            lateinit var data: Section

            init {
                linBtnSection = itemView.findViewById(R.id.linBtnFilter)
                tvSection = itemView.findViewById(R.id.tvSection)
                itemView.tag = this
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
            val view =
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.row_filter, viewGroup, false)
            view.setOnClickListener(onClickListener)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: MyViewHolder, i: Int) {
            val obj = itemList[i]
            viewHolder.data = obj
            viewHolder.tvSection.text = obj.name
        }

        override fun getItemCount(): Int {
            return itemList.size
        }
    }
}