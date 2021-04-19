package com.dovoo.memesnetwork.adapter

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.dovoo.memesnetwork.BuildConfig
import com.dovoo.memesnetwork.LoginActivity
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.activities.CommentActivity
import com.dovoo.memesnetwork.adapter.holders.MemesViewHolder
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils.getPrefs
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import im.ene.toro.widget.PressablePlayerSelector
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class MemesRecyclerViewAdapter(
    private val mContext: Context, private val selector: PressablePlayerSelector?, //
    private val directLinkItemTestList: List<DirectLinkItemTest>, loadingBar: FrameLayout
) : RecyclerView.Adapter<MemesViewHolder>() {
    var finalWidth // default phone width
            : Float
    var finalHeight: Float
    var maxHeightVideo: Float
    private val mLoadingBar: FrameLayout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(MemesViewHolder.LAYOUT_RES, parent, false)
        val viewHolder = MemesViewHolder(view, selector)
        if (selector != null) viewHolder.itemView.setOnLongClickListener(selector)
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: MemesViewHolder, position: Int) {

        // public static BaseVideoItem currentVideoItem;
        val directLinkVideoItem = directLinkItemTestList[position]
        if (directLinkVideoItem.getmHeight() > directLinkVideoItem.getmWidth()) {
            // if video is potrait
            val ratio = directLinkVideoItem.getmHeight().toFloat() / directLinkVideoItem.getmWidth()
            finalHeight = finalWidth * ratio
            // if final height higher or same with phone height, we have to decrease it to make the video fit in phone. It will show around 3/4 phone screen
            if (finalHeight >= maxHeightVideo) {
                finalHeight = maxHeightVideo * 0.6f
            }
        } else if (directLinkVideoItem.getmHeight() < directLinkVideoItem.getmWidth()) {
            // if video is landscape
            val ratio = directLinkVideoItem.getmWidth().toFloat() / directLinkVideoItem.getmHeight()
            finalHeight = finalWidth / ratio
        } else {
            // if video is square
            finalHeight = finalWidth
        }
        val layoutParams = viewHolder.relativeLayout.layoutParams
        layoutParams.width = finalWidth.toInt()
        layoutParams.height = finalHeight.toInt()
        viewHolder.relativeLayout.layoutParams = layoutParams
        viewHolder.tvTitle.text = Html.fromHtml(directLinkVideoItem.getmTitle())
        viewHolder.tvCategory.text = directLinkVideoItem.getmCategory()
        viewHolder.mCover.visibility = View.VISIBLE
        directLinkVideoItem.getmImageLoader().load(directLinkVideoItem.getmCoverUrl())
            .into(viewHolder.mCover)
        val totalLike = directLinkVideoItem.data!!["total_like"] as Int?
        val totalDislike = directLinkVideoItem.data!!["total_dislike"] as Int?
        val totalComment = directLinkVideoItem.data!!["total_comment"] as Int?
        if (directLinkVideoItem.isVideo) {
            //video
            if (directLinkVideoItem.isHasAudio) {
                viewHolder.ivIconSound.visibility = View.VISIBLE
                viewHolder.tvLabelNoAudio.visibility = View.GONE
            } else {
                viewHolder.tvLabelNoAudio.visibility = View.VISIBLE
                viewHolder.ivIconSound.visibility = View.GONE
            }
        } else {
            //photo
            viewHolder.tvLabelNoAudio.visibility = View.GONE
            viewHolder.ivIconSound.visibility = View.GONE
        }
        viewHolder.tvTotalLike.text = totalLike.toString()
        viewHolder.tvTotalComment.text = totalComment.toString()
        if (directLinkVideoItem.data!!["is_liked"] is Int) {
            val isLiked = directLinkVideoItem.data!!["is_liked"] as Int?
            if (isLiked == 1) {
                viewHolder.linBtnLike.isEnabled = false
                viewHolder.ivBtnLike.setImageResource(R.drawable.ic_thumbs_up_active)
                viewHolder.tvTotalLike.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.pink700
                    )
                )
            } else {
                viewHolder.linBtnLike.isEnabled = true
                viewHolder.ivBtnLike.setImageResource(R.drawable.ic_thumbs_up)
                viewHolder.tvTotalLike.setTextColor(ContextCompat.getColor(mContext, R.color.white))
            }
        } else {
            viewHolder.linBtnLike.isEnabled = true
            viewHolder.ivBtnLike.setImageResource(R.drawable.ic_thumbs_up)
            viewHolder.tvTotalLike.setTextColor(ContextCompat.getColor(mContext, R.color.white))
        }
        viewHolder.ivBtnShare.setOnClickListener(View.OnClickListener {
            if (ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(mContext, "Please allow storage permission", Toast.LENGTH_LONG)
                    .show()
                return@OnClickListener
            }
            viewHolder.ivBtnShare.isEnabled = false
            mLoadingBar.visibility = View.VISIBLE
            val isVideo = directLinkVideoItem.getmDirectUrl() != null
            var theUrl: String? = directLinkVideoItem.getmCoverUrl()
            if (directLinkVideoItem.getmDirectUrl() != null) {
                theUrl = directLinkVideoItem.getmDirectUrl()
            }
            FileLoader.with(mContext)
                .load(theUrl) //2nd parameter is optioal, pass true to force load from network
                .fromDirectory("memesnetwork", FileLoader.DIR_EXTERNAL_PUBLIC)
                .asFile(object : FileRequestListener<File?> {
                    override fun onLoad(request: FileLoadRequest, response: FileResponse<File?>) {
                        val loadedFile = response.body
                        val share = Intent(Intent.ACTION_SEND)
                        if (!isVideo) {
                            share.type = "image/*"
                        } else {
                            share.type = "video/*"
                        }
                        val uri: Uri
                        uri = if (Build.VERSION.SDK_INT <= 24) {
                            Uri.fromFile(loadedFile)
                        } else {
                            FileProvider.getUriForFile(
                                mContext,
                                BuildConfig.APPLICATION_ID + ".provider",
                                loadedFile!!
                            )
                        }
                        var shareMessage =
                            "\nWith MemesNetwork everything is laughable, download here\n\n"
                        shareMessage = """
                            ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                            
                            
                            """.trimIndent()
                        share.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        share.putExtra(Intent.EXTRA_STREAM, uri)
                        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        mContext.startActivity(Intent.createChooser(share, "Share :"))
                        mLoadingBar.visibility = View.GONE
                        viewHolder.ivBtnShare.isEnabled = true
                    }

                    override fun onError(request: FileLoadRequest, t: Throwable) {
                        mLoadingBar.visibility = View.GONE
                        viewHolder.ivBtnShare.isEnabled = true
                        Toast.makeText(mContext, "Cannot sharing file", Toast.LENGTH_LONG).show()
                    }
                })
        })
        viewHolder.linBtnComment.setOnClickListener {
            GlobalFunc.currentVideoItem = directLinkVideoItem
            val i = Intent(mContext, CommentActivity::class.java)
            i.putExtra("meme_id", directLinkVideoItem.id)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext.startActivity(i)
        }
        viewHolder.tvTitle.setOnClickListener {
            GlobalFunc.currentVideoItem = directLinkVideoItem
            val i = Intent(mContext, CommentActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext.startActivity(i)
        }
        viewHolder.linBtnLike.setOnClickListener {
            if (!getPrefs(mContext).getBoolean(
                    SharedPreferenceUtils.PREFERENCES_USER_IS_LOGIN,
                    false
                )
            ) {
                val i = Intent(mContext, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                mContext.startActivity(i)
            } else {
                doLike(viewHolder, directLinkVideoItem)
            }
        }

        viewHolder.bind(directLinkVideoItem)
    }

    override fun getItemCount(): Int {
        return directLinkItemTestList.size
    }

    private fun doLike(viewHolder: MemesViewHolder, directLinkVideoItem: DirectLinkItemTest) {
        mLoadingBar.visibility = View.VISIBLE
        viewHolder.linBtnLike.isEnabled = false
        val userId = getPrefs(mContext).getInt(SharedPreferenceUtils.PREFERENCES_USER_ID, 0)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("meme_id", directLinkVideoItem.id)
            jsonObject.put("user_id", userId)
            jsonObject.put("like", 1)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        //        AndroidNetworking.post(BuildConfig.API_URL + "insertlike")
//                .addJSONObjectBody(jsonObject)
//                .setPriority(Priority.HIGH)
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // do anything with response
//                        try {
//                            Integer totalLike = response.getInt("total_like");
//                            Integer totalDislike = response.getInt("total_dislike");
//
//                            viewHolder.tvBtnLike.setTextColor(ContextCompat.getColor(mContext, R.color.pink700));
//                            viewHolder.tvBtnDislike.setTextColor(ContextCompat.getColor(mContext, R.color.white));
//                            viewHolder.tvTotalLike.setText(totalLike + "");
//                            viewHolder.tvTotalLike.setTextColor(ContextCompat.getColor(mContext, R.color.pink700));
//                            viewHolder.linBtnLike.setEnabled(false);
//                            viewHolder.linBtnDislike.setEnabled(true);
//                            directLinkVideoItem.getData().put("is_liked", 1);
//                            String totLike = (String) directLinkVideoItem.getData().get("total_like");
//                            directLinkVideoItem.getData().put("total_like", (Integer.parseInt(totLike)+1)+"");
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        mLoadingBar.setVisibility(View.GONE);
//
//
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        // handle error
//                        System.out.print("error");
//                        mLoadingBar.setVisibility(View.GONE);
//                        viewHolder.linBtnLike.setEnabled(true);
//                        viewHolder.linBtnDislike.setEnabled(true);
//                        Toast.makeText(mContext, "Fail", Toast.LENGTH_LONG).show();
//                    }
//                });
    }

    private fun doDislike(viewHolder: MemesViewHolder, directLinkVideoItem: DirectLinkItemTest) {
        mLoadingBar.visibility = View.VISIBLE
        viewHolder.linBtnLike.isEnabled = false
        val userId = getPrefs(mContext).getInt(SharedPreferenceUtils.PREFERENCES_USER_ID, 0)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("meme_id", directLinkVideoItem.id)
            jsonObject.put("user_id", userId)
            jsonObject.put("like", 0)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        //        AndroidNetworking.post(BuildConfig.API_URL + "insertlike")
//                .addJSONObjectBody(jsonObject)
//                .setPriority(Priority.HIGH)
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // do anything with response
//                        try {
//                            Integer totalLike = response.getInt("total_like");
//                            Integer totalDislike = response.getInt("total_dislike");
//
//                            viewHolder.tvBtnLike.setTextColor(ContextCompat.getColor(mContext, R.color.white));
//                            viewHolder.tvBtnDislike.setTextColor(ContextCompat.getColor(mContext, R.color.pink700));
//                            viewHolder.tvTotalLike.setText(totalLike + "");
//                            viewHolder.tvTotalLike.setTextColor(ContextCompat.getColor(mContext, R.color.white));
//                            viewHolder.linBtnLike.setEnabled(true);
//                            viewHolder.linBtnDislike.setEnabled(false);
//                            directLinkVideoItem.getData().put("is_liked", 0);
//                            String totDislike = (String) directLinkVideoItem.getData().get("total_dislike");
//                            directLinkVideoItem.getData().put("total_dislike", (Integer.parseInt(totDislike)+1)+"");
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        mLoadingBar.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        // handle error
//                        System.out.print("error");
//                        mLoadingBar.setVisibility(View.GONE);
//                        viewHolder.linBtnLike.setEnabled(true);
//                        viewHolder.linBtnDislike.setEnabled(true);
//                        Toast.makeText(mContext, "Fail", Toast.LENGTH_LONG).show();
//
//                    }
//                });
    }

    init {
        finalWidth = mContext.resources.displayMetrics.widthPixels.toFloat() // default phone width
        finalHeight = mContext.resources.displayMetrics.heightPixels.toFloat()
        maxHeightVideo =
            mContext.resources.displayMetrics.heightPixels.toFloat() * 0.8f // set default maximum video size in phone
        mLoadingBar = loadingBar
    }
}