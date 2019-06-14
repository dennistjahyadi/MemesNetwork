package com.dovoo.memesnetwork.testing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dovoo.memesnetwork.R;

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

    public TestingAdapter(Context mContext, @Nullable PressablePlayerSelector selector, List<DirectLinkItemTest> directLinkItemTestList) {
        this.selector = selector;
        this.mContext = mContext;
        this.directLinkItemTestList = directLinkItemTestList;
        this.finalWidth =(float) mContext.getResources().getDisplayMetrics().widthPixels;  // default phone width
        this.finalHeight = (float)mContext.getResources().getDisplayMetrics().heightPixels;
        this.maxHeightVideo = (float) mContext.getResources().getDisplayMetrics().heightPixels * 0.8f; // set default maximum video size in phone

    }

    @NonNull @Override
    public TestingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(TestingViewHolder.LAYOUT_RES, parent, false);
        TestingViewHolder viewHolder = new TestingViewHolder(view, this.selector);
        if (this.selector != null) viewHolder.itemView.setOnLongClickListener(this.selector);
        return viewHolder;
    }

    @Override public void onBindViewHolder(@NonNull TestingViewHolder viewHolder, int position) {

        // public static BaseVideoItem currentVideoItem;
        DirectLinkItemTest directLinkVideoItem = directLinkItemTestList.get(position);

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
             /*   GlobalFunc.currentVideoItem = videoItem;
                Intent i = new Intent(mContext, CommentActivity.class);
                i.putExtra("meme_id",directLinkVideoItem.getId());
                mContext.startActivity(i);*/
            }
        });
        viewHolder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  GlobalFunc.currentVideoItem = videoItem;
                Intent i = new Intent(mContext, CommentActivity.class);
                mContext.startActivity(i);*/
            }
        });

        viewHolder.linBtnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (!SharedPreferenceUtils.getPrefs(mContext).getBoolean(SharedPreferenceUtils.PREFERENCES_USER_IS_LOGIN, false)) {
                    Intent i = new Intent(mContext, LoginActivity.class);
                    mContext.startActivity(i);
                } else {
                    doLike(viewHolder, directLinkVideoItem.getId());
                }*/
            }
        });

        viewHolder.linBtnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  if (!SharedPreferenceUtils.getPrefs(mContext).getBoolean(SharedPreferenceUtils.PREFERENCES_USER_IS_LOGIN, false)) {
                    Intent i = new Intent(mContext, LoginActivity.class);
                    mContext.startActivity(i);
                } else {
                    doDislike(viewHolder, directLinkVideoItem.getId());
                }*/
            }
        });
        viewHolder.bind(directLinkVideoItem);

    }

    @Override public int getItemCount() {
        return directLinkItemTestList.size();
    }

}