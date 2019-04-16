package com.dovoo.memesnetwork.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.dovoo.memesnetwork.LoginActivity;
import com.dovoo.memesnetwork.R;
import com.dovoo.memesnetwork.activities.ChooseUsernameActivity;
import com.dovoo.memesnetwork.activities.CommentActivity;
import com.dovoo.memesnetwork.adapter.holders.VideoViewHolder;
import com.dovoo.memesnetwork.adapter.items.BaseVideoItem;
import com.dovoo.memesnetwork.adapter.items.DirectLinkVideoItem;
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils;
import com.dovoo.memesnetwork.utils.Utils;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by danylo.volokh on 9/20/2015.
 */
public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoViewHolder> {

    private final VideoPlayerManager mVideoPlayerManager;
    private final List<BaseVideoItem> mList;
    private final Context mContext;
    float finalWidth;
    float finalHeight;
    public static BaseVideoItem currentVideoItem;
    float maxHeightVideo;
    private Integer userId;
    private FrameLayout mLoadingBar;

    public VideoRecyclerViewAdapter(VideoPlayerManager videoPlayerManager, Context context, FrameLayout loadingBar, List<BaseVideoItem> list) {
        mVideoPlayerManager = videoPlayerManager;
        mContext = context;
        mLoadingBar = loadingBar;
        mList = list;
        finalWidth = mContext.getResources().getDisplayMetrics().widthPixels;  // default phone width
        finalHeight = mContext.getResources().getDisplayMetrics().heightPixels; // default phone heights
        maxHeightVideo = (float) mContext.getResources().getDisplayMetrics().heightPixels * 0.8f; // set default maximum video size in phone
        userId = SharedPreferenceUtils.getPrefs(context).getInt(SharedPreferenceUtils.PREFERENCES_USER_ID, 0);
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        BaseVideoItem videoItem = mList.get(position);
        View resultView = videoItem.createView(viewGroup, mContext.getResources().getDisplayMetrics().widthPixels, mContext.getResources().getDisplayMetrics().heightPixels);
        return new VideoViewHolder(resultView);
    }

    @Override
    public void onBindViewHolder(final VideoViewHolder viewHolder, int position) {
        final BaseVideoItem videoItem = mList.get(position);
        if (videoItem.getContentHeight() > videoItem.getContentWidth()) {
            // if video is potrait
            float ratio = (float) videoItem.getContentHeight() / videoItem.getContentWidth();

            finalHeight = finalWidth * ratio;
            // if final height higher or same with phone height, we have to decrease it to make the video fit in phone. It will show around 3/4 phone screen
            if (finalHeight >= maxHeightVideo) {
                finalHeight = maxHeightVideo * 0.7f;
            }

        } else if (videoItem.getContentHeight() < videoItem.getContentWidth()) {
            // if video is landscape
            float ratio = (float) videoItem.getContentWidth() / videoItem.getContentHeight();

            finalHeight = finalWidth / ratio;
        } else {
            // if video is square
            finalHeight = finalWidth;
        }

        ViewGroup.LayoutParams layoutParams = viewHolder.relativeLayout.getLayoutParams();
        layoutParams.width = (int) finalWidth;
        layoutParams.height = (int) finalHeight;
        viewHolder.relativeLayout.setLayoutParams(layoutParams);

        final DirectLinkVideoItem directLinkVideoItem = (DirectLinkVideoItem) videoItem;

        viewHolder.tvTitle.setText(directLinkVideoItem.getmTitle());
        viewHolder.tvCategory.setText(directLinkVideoItem.getmCategory());
        viewHolder.mCover.setVisibility(View.VISIBLE);
        directLinkVideoItem.getmImageLoader().load(directLinkVideoItem.getmCoverUrl()).into(viewHolder.mCover);

        Integer totalLike = (Integer) directLinkVideoItem.getData().get("total_like");
        Integer totalDislike = (Integer) directLinkVideoItem.getData().get("total_dislike");
        Integer totalComment = (Integer) directLinkVideoItem.getData().get("total_comment");

        viewHolder.tvTotalLike.setText(totalLike+"");
        viewHolder.tvTotalDislike.setText(totalDislike+"");
        viewHolder.tvTotalComment.setText(totalComment+"");
        if(directLinkVideoItem.getData().get("is_liked") instanceof Integer){
            Integer isLiked = (Integer) directLinkVideoItem.getData().get("is_liked");
            if(isLiked==1){
                viewHolder.tvBtnLike.setTextColor(ContextCompat.getColor(mContext, R.color.pink700));
                viewHolder.tvTotalLike.setTextColor(ContextCompat.getColor(mContext, R.color.pink700));
                viewHolder.tvBtnDislike.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                viewHolder.tvTotalDislike.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            }else{
                viewHolder.tvBtnLike.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                viewHolder.tvTotalLike.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                viewHolder.tvBtnDislike.setTextColor(ContextCompat.getColor(mContext, R.color.pink700));
                viewHolder.tvTotalDislike.setTextColor(ContextCompat.getColor(mContext, R.color.pink700));
            }
        }else{
            viewHolder.tvBtnLike.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            viewHolder.tvTotalLike.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            viewHolder.tvBtnDislike.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            viewHolder.tvTotalDislike.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }

        viewHolder.linBtnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentVideoItem = videoItem;
                Intent i = new Intent(mContext, CommentActivity.class);
                i.putExtra("meme_id",directLinkVideoItem.getId());
                mContext.startActivity(i);
            }
        });
        viewHolder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentVideoItem = videoItem;
                Intent i = new Intent(mContext, CommentActivity.class);
                mContext.startActivity(i);
            }
        });

        viewHolder.linBtnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SharedPreferenceUtils.getPrefs(mContext).getBoolean(SharedPreferenceUtils.PREFERENCES_USER_IS_LOGIN, false)) {
                    Intent i = new Intent(mContext, LoginActivity.class);
                    mContext.startActivity(i);
                } else {
                    doLike(viewHolder, directLinkVideoItem.getId());
                }
            }
        });

        viewHolder.linBtnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SharedPreferenceUtils.getPrefs(mContext).getBoolean(SharedPreferenceUtils.PREFERENCES_USER_IS_LOGIN, false)) {
                    Intent i = new Intent(mContext, LoginActivity.class);
                    mContext.startActivity(i);
                } else {
                    doDislike(viewHolder, directLinkVideoItem.getId());
                }
            }
        });
        videoItem.update(position, viewHolder, mVideoPlayerManager);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void doLike(final VideoViewHolder viewHolder, Integer memeId) {
        mLoadingBar.setVisibility(View.VISIBLE);
        viewHolder.linBtnLike.setEnabled(false);
        viewHolder.linBtnDislike.setEnabled(false);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("meme_id", memeId);
            jsonObject.put("user_id", userId);
            jsonObject.put("like", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(Utils.API_URL + "insertlike")
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            Integer totalLike = response.getInt("total_like");
                            Integer totalDislike = response.getInt("total_dislike");

                            viewHolder.tvBtnLike.setTextColor(ContextCompat.getColor(mContext, R.color.pink700));
                            viewHolder.tvBtnDislike.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                            viewHolder.tvTotalLike.setText(totalLike + "");
                            viewHolder.tvTotalLike.setTextColor(ContextCompat.getColor(mContext, R.color.pink700));
                            viewHolder.tvTotalDislike.setText(totalDislike + "");
                            viewHolder.tvTotalDislike.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                            viewHolder.linBtnLike.setEnabled(false);
                            viewHolder.linBtnDislike.setEnabled(true);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mLoadingBar.setVisibility(View.GONE);


                    }

                    @Override
                    public void onError(ANError anError) {
                        // handle error
                        System.out.print("error");
                        mLoadingBar.setVisibility(View.GONE);
                        viewHolder.linBtnLike.setEnabled(true);
                        viewHolder.linBtnDislike.setEnabled(true);

                    }
                });
    }

    private void doDislike(final VideoViewHolder viewHolder, Integer memeId) {
        mLoadingBar.setVisibility(View.VISIBLE);
        viewHolder.linBtnLike.setEnabled(false);
        viewHolder.linBtnDislike.setEnabled(false);

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("meme_id", memeId);
            jsonObject.put("user_id", userId);
            jsonObject.put("like", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(Utils.API_URL + "insertlike")
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            Integer totalLike = response.getInt("total_like");
                            Integer totalDislike = response.getInt("total_dislike");

                            viewHolder.tvBtnLike.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                            viewHolder.tvBtnDislike.setTextColor(ContextCompat.getColor(mContext, R.color.pink700));
                            viewHolder.tvTotalLike.setText(totalLike + "");
                            viewHolder.tvTotalLike.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                            viewHolder.tvTotalDislike.setText(totalDislike + "");
                            viewHolder.tvTotalDislike.setTextColor(ContextCompat.getColor(mContext, R.color.pink700));
                            viewHolder.linBtnLike.setEnabled(true);
                            viewHolder.linBtnDislike.setEnabled(false);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mLoadingBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(ANError anError) {
                        // handle error
                        System.out.print("error");
                        mLoadingBar.setVisibility(View.GONE);
                        viewHolder.linBtnLike.setEnabled(true);
                        viewHolder.linBtnDislike.setEnabled(true);

                    }
                });
    }
}
