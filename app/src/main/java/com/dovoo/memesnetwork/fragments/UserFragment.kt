package com.dovoo.memesnetwork.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.dovoo.memesnetwork.components.EndlessRecyclerViewScrollListener
import com.dovoo.memesnetwork.databinding.FragmentUserBinding
import com.dovoo.memesnetwork.model.Status
import com.dovoo.memesnetwork.model.UserOtherDetails
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils
import com.dovoo.memesnetwork.viewmodel.GeneralViewModel
import org.json.JSONException
import java.util.ArrayList

class UserFragment : Fragment() {
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    val generalViewModel: GeneralViewModel by viewModels()
    val userId by lazy {
        arguments?.getInt("user_id")!!
    }
    private val memesList: ArrayList<DirectLinkItemTest> = ArrayList()
    lateinit var adapter: MyMemesFragment.MyMemesAdapter

    val memesOnClickListener = View.OnClickListener {
        val memesViewHolder = it.tag as MyMemesFragment.MyMemesAdapter.MyMemesViewHolder
    }

    var currentUser: UserOtherDetails? = null

    var isFollowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = MyMemesFragment.MyMemesAdapter(requireContext(), memesList, memesOnClickListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        binding.linBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        val layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)

        val endlessSrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                fetchData(totalItemsCount)
            }
        }
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.removeOnScrollListener(endlessSrollListener)
        binding.recyclerView.addOnScrollListener(endlessSrollListener)

        binding.linBtnFollow.setOnClickListener {
            isFollowing = !isFollowing
            updateUI()
        }

        generalViewModel.userMemes.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {

                    // do anything with response
                    try {
                        it.data?.memes?.forEach { meme ->
                            memesList.add(DirectLinkItemTest(meme))
                        }
                        adapter.notifyDataSetChanged()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
                Status.ERROR -> {
                    println("BBBBB: " + it.error?.message)
                }
                else -> {
                }
            }
        })
        getUser()
        if (memesList.isEmpty()) fetchData(0)

        return binding.root
    }

    private fun fetchData(offset: Int) {
        if (offset == 0) {
            memesList.clear()
        }
        generalViewModel.fetchUserMemes(offset, userId, null)
    }

    private fun getUser() {

        generalViewModel.getUser(userId).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    currentUser = it.data!!.user
                    val loggedInUserId = GlobalFunc.getLoggedInUserId(requireContext())
                    it.data.user.follower_user.forEach lit@{ usr ->
                        if(usr.id == loggedInUserId){
                            isFollowing = true
                            return@lit
                        }
                    }
                    updateUI()
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error?.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun updateUI(){
        currentUser?.let { user ->
            binding.tvUsername.text = user.username
            Glide.with(requireContext())
                .load(user.photo_url)
                .into(binding.ivProfile)
            binding.tvTotalMemes.text = user.memes.size.toString()
            binding.tvFollowers.text = user.following_user.size.toString()
            binding.tvFollowing.text = user.follower_user.size.toString()
        }

        if(isFollowing){
            binding.linBtnFollow.setBackgroundResource(R.drawable.rounded_border_white_bold)
            binding.tvFollow.setText(R.string.followed)
            binding.tvFollow.typeface = Typeface.DEFAULT_BOLD;
        }else{
            binding.linBtnFollow.setBackgroundResource(R.drawable.rounded_border_white)
            binding.tvFollow.setText(R.string.follow)
            binding.tvFollow.typeface = Typeface.DEFAULT;
        }


    }
}