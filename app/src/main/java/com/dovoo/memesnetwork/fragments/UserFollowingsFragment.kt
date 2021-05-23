package com.dovoo.memesnetwork.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.databinding.FragmentUserFollowingsBinding
import com.dovoo.memesnetwork.model.FollowingData
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel

class UserFollowingsFragment: Fragment() {
    private var _binding: FragmentUserFollowingsBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()
    val isFollowing by lazy {
        arguments?.getBoolean("isFollowing")
    }
    val listData: ArrayList<FollowingData> = ArrayList()
    lateinit var adapter: UserFollowingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = UserFollowingsAdapter(requireContext(), listData)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserFollowingsBinding.inflate(inflater, container, false)
        if(isFollowing==true) binding.tvTitle.text = getString(R.string.followings)
        else binding.tvTitle.text = getString(R.string.followers)

        fetchData()

        return binding.root
    }

    private fun fetchData(){
        val userId = GlobalFunc.getLoggedInUserId(requireContext())
        if(isFollowing==true){
            generalViewModel.fetchFollowings(userId).observe(viewLifecycleOwner, {
                when(it.status){
                    Status.SUCCESS -> {
                        it.data?.followings?.let {
                            listData.addAll(it)
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(requireContext(), it.error?.message, Toast.LENGTH_LONG).show()
                    }
                }
            })
        }else{
            generalViewModel.fetchFollowers(userId).observe(viewLifecycleOwner, {
                when(it.status){
                    Status.SUCCESS -> {
                        it.data?.followers?.let {
                            listData.addAll(it)
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(requireContext(), it.error?.message, Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }

    class UserFollowingsAdapter(val context: Context,val listData: ArrayList<FollowingData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        class UserFollowingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

            lateinit var data: FollowingData
            init {

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return UserFollowingsViewHolder(LayoutInflater.from(context).inflate(R.layout.row_user_followings, parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder as UserFollowingsViewHolder
            val data = listData[position]
            holder.data = data

        }

        override fun getItemCount(): Int {
            return listData.size
        }

    }

    fun newInstance(isFollowing: Boolean): UserFollowingsFragment {
        val args = Bundle()

        val fragment = UserFollowingsFragment()
        args.putBoolean("isFollowing", isFollowing)
        fragment.arguments = args
        return fragment
    }
}