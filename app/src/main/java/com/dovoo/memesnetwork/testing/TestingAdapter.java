package com.dovoo.memesnetwork.testing;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.dovoo.memesnetwork.LoginActivity;
import com.dovoo.memesnetwork.R;
import com.dovoo.memesnetwork.activities.CommentActivity;
import com.dovoo.memesnetwork.utils.GlobalFunc;
import com.dovoo.memesnetwork.utils.SharedPreferenceUtils;
import com.dovoo.memesnetwork.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import im.ene.toro.widget.PressablePlayerSelector;

public class TestingAdapter extends RecyclerView.Adapter<TestingViewHolder> {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") //
    private List<DirectLinkItemTest> directLinkItemTestList;
    private Context mContext;
    @Nullable private final PressablePlayerSelector selector;
    float finalWidth;// default phone width
    float finalHeight;
    float maxHeightVideo;
    private FrameLayout mLoadingBar;

    public TestingAdapter(Context mContext, @Nullable PressablePlayerSelector selector, List<DirectLinkItemTest> directLinkItemTestList, FrameLayout loadingBar) {
        this.selector = selector;
        this.mContext = mContext;
        this.directLinkItemTestList = directLinkItemTestList;
        this.finalWidth =(float) mContext.getResources().getDisplayMetrics().widthPixels;  // default phone width
        this.finalHeight = (float)mContext.getResources().getDisplayMetrics().heightPixels;
        this.maxHeightVideo = (float) mContext.getResources().getDisplayMetrics().heightPixels * 0.8f; // set default maximum video size in phone
        this.mLoadingBar = loadingBar;
    }

    @NonNull @Override
    public TestingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(TestingViewHolder.LAYOUT_RES, parent, false);
        TestingViewHolder viewHolder = new TestingViewHolder(view, this.selector);
        if (this.selector != null) viewHolder.itemView.setOnLongClickListener(this.selector);
        return viewHolder;
    }

    @Override public void onBindViewHolder(@NonNull final TestingViewHolder viewHolder, int position) {

        // public static BaseVideoItem currentVideoItem;
        final DirectLinkItemTest directLinkVideoItem = directLinkItemTestList.get(position);

        if (directLinkVideoItem.getmHeight() > directLinkVideoItem.getmWidth()) {
            // if video is potrait
            float ratio = (float) directLinkVideoItem.getmHeight() / directLinkVideoItem.getmWidth();

            finalHeight = finalWidth * ratio;
            // if final height higher or same with phone height, we have to decrease it to make the video fit in phone. It will show around 3/4 phone screen
            if (finalHeight >= maxHeightVideo) {
                finalHeight = maxHeightVideo * 0.5f;
            }

        } else if (directLinkVideoItem.getmHeight() < directLinkVideoItem.getmWidth()) {
            // if video is landscape
            float ratio = (float) directLinkVideoItem.getmWidth() / directLinkVideoItem.getmHeight();

            finalHeight = finalWidth / ratio;
        } else {
            // if video is square
            finalHeight = finalWidth;
        }

        ViewGroup.LayoutParams layoutParams = viewHolder.relativeLayout.getLayoutParams();
        layoutParams.width = (int) finalWidth;
        layoutParams.height = (int) finalHeight;
        viewHolder.relativeLayout.setLayoutParams(layoutParams);
        viewHolder.tvTitle.setText(directLinkVideoItem.getmTitle());
        viewHolder.tvCategory.setText(directLinkVideoItem.getmCategory());
        viewHolder.mCover.setVisibility(View.VISIBLE);
        directLinkVideoItem.getmImageLoader().load(directLinkVideoItem.getmCoverUrl()).into(viewHolder.mCover);

        Integer totalLike = (Integer) directLinkVideoItem.getData().get("total_like");
        Integer totalDislike = (Integer) directLinkVideoItem.getData().get("total_dislike");
        Integer totalComment = (Integer) directLinkVideoItem.getData().get("total_comment");

        if(directLinkVideoItem.isVideo()){
            //video
            if(directLinkVideoItem.isHasAudio()){
                viewHolder.tvIconSound.setVisibility(View.VISIBLE);
                viewHolder.tvLabelNoAudio.setVisibility(View.GONE);

            }else{
                viewHolder.tvLabelNoAudio.setVisibility(View.VISIBLE);
                viewHolder.tvIconSound.setVisibility(View.GONE);
            }

        }else{
            //photo
            viewHolder.tvLabelNoAudio.setVisibility(View.GONE);
            viewHolder.tvIconSound.setVisibility(View.GONE);
        }

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
                GlobalFunc.currentVideoItem = directLinkVideoItem;
                Intent i = new Intent(mContext, CommentActivity.class);
                i.putExtra("meme_id",directLinkVideoItem.getId());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        });
        viewHolder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalFunc.currentVideoItem = directLinkVideoItem;
                Intent i = new Intent(mContext, CommentActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        });

        viewHolder.linBtnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SharedPreferenceUtils.getPrefs(mContext).getBoolean(SharedPreferenceUtils.PREFERENCES_USER_IS_LOGIN, false)) {
                    Intent i = new Intent(mContext, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);
                } else {
                    doDislike(viewHolder, directLinkVideoItem.getId());
                }
            }
        });
        viewHolder.bind(directLinkVideoItem);

    }

    @Override public int getItemCount() {
        return directLinkItemTestList.size();
    }

    private void doLike(final TestingViewHolder viewHolder, Integer memeId) {
        mLoadingBar.setVisibility(View.VISIBLE);
        viewHolder.linBtnLike.setEnabled(false);
        viewHolder.linBtnDislike.setEnabled(false);
        Integer userId = SharedPreferenceUtils.getPrefs(mContext).getInt(SharedPreferenceUtils.PREFERENCES_USER_ID, 0);

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

    private void doDislike(final TestingViewHolder viewHolder, Integer memeId) {
        mLoadingBar.setVisibility(View.VISIBLE);
        viewHolder.linBtnLike.setEnabled(false);
        viewHolder.linBtnDislike.setEnabled(false);
        Integer userId = SharedPreferenceUtils.getPrefs(mContext).getInt(SharedPreferenceUtils.PREFERENCES_USER_ID, 0);

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