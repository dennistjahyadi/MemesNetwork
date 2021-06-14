package com.dovoo.memesnetwork.fragments

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener
import com.dovoo.memesnetwork.databinding.FragmentSearchUserBinding
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.model.User
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel

class SearchUserFragment : Fragment() {
    private var _binding: FragmentSearchUserBinding? = null
    private val binding get() = _binding!!
    private val userList: ArrayList<User> = ArrayList()
    lateinit var adapter: SearchUserAdapter
    val generalViewModel: GeneralViewModel by viewModels()

    val itemOnClickListener = View.OnClickListener {
        val data = (it.tag as SearchUserAdapter.SearchUserViewHolder).data
        val bundle = bundleOf("user_id" to data.id)
        findNavController().navigate(R.id.action_searchUserFragment_to_userFragment, bundle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = SearchUserAdapter(requireContext(), userList, itemOnClickListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchUserBinding.inflate(inflater, container, false)
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.addOnScrollListener(object :
            EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                fetchUser(totalItemsCount)
            }
        })
        binding.linBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.ivSearch.setOnClickListener {
            fetchUser(0)
        }
        binding.etSearch.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //Perform Code
                fetchUser(0)
                return@OnKeyListener true
            }
            false
        })
        return binding.root
    }

    private fun fetchUser(offset: Int) {
        if(offset==0)userList.clear()
        binding.progressBar.loadingBar.visibility = View.VISIBLE
        generalViewModel.fetchUser(offset, binding.etSearch.text.toString())
            .observe(viewLifecycleOwner, {
                when (it.status) {
                    Status.SUCCESS -> {
                        binding.progressBar.loadingBar.visibility = View.GONE
                        it.data?.users?.let {
                            userList.addAll(it)
                        }
                        adapter.notifyDataSetChanged()
                    }
                    Status.ERROR -> {
                        binding.progressBar.loadingBar.visibility = View.GONE
                        Toast.makeText(requireContext(), it.error?.message, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            })
    }

    class SearchUserAdapter(
        val context: Context,
        val userList: ArrayList<User>,
        val itemOnClickListener: View.OnClickListener
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        class SearchUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val ivProfilePic: ImageView
            val tvUsername: TextView
            val linBtnFollow: LinearLayout
            lateinit var data: User

            init {
                ivProfilePic = itemView.findViewById(R.id.ivProfilePic)
                tvUsername = itemView.findViewById(R.id.tvUsername)
                linBtnFollow = itemView.findViewById(R.id.linBtnFollow)
                itemView.tag = this
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return SearchUserViewHolder(
                LayoutInflater.from(context).inflate(R.layout.row_search_user, parent, false)
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder as SearchUserViewHolder
            val data = userList[position]
            holder.data = data
            holder.tvUsername.text = data.username
            Glide.with(context).load(data.photo_url).placeholder(R.drawable.funny_user2).into(holder.ivProfilePic)
            holder.itemView.setOnClickListener(itemOnClickListener)
        }

        override fun getItemCount(): Int {
            return userList.size
        }

    }

}