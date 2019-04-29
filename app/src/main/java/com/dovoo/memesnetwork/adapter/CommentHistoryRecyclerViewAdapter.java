package com.dovoo.memesnetwork.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dovoo.memesnetwork.R;
import com.dovoo.memesnetwork.activities.CommentActivity;
import com.dovoo.memesnetwork.adapter.items.BaseVideoItem;
import com.dovoo.memesnetwork.adapter.items.DirectLinkVideoItem;
import com.dovoo.memesnetwork.components.TextViewFaSolid;
import com.dovoo.memesnetwork.utils.GlobalFunc;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CommentHistoryRecyclerViewAdapter extends RecyclerView.Adapter<CommentHistoryRecyclerViewAdapter.MyViewHolderItem> {

    private List<Map<String, Object>> itemList;
    private Context context;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int[] funnyimgs = new int[] {R.drawable.funny_user1,R.drawable.funny_user2,R.drawable.funny_user3};


    public CommentHistoryRecyclerViewAdapter(Context context, List<Map<String, Object>> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    class MyViewHolderItem extends RecyclerView.ViewHolder {
        TextView tvUsername,tvComment,tvCreatedDate;
        ImageView ivPicture;
        ConstraintLayout clBtn;
        public MyViewHolderItem(View itemView) {
            super(itemView);
            clBtn = itemView.findViewById(R.id.clBtn);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvCreatedDate = itemView.findViewById(R.id.tvCreatedDate);
            ivPicture = itemView.findViewById(R.id.ivPicture);
        }
    }

    @NonNull
    @Override
    public CommentHistoryRecyclerViewAdapter.MyViewHolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comment_history, parent, false);
            return new CommentHistoryRecyclerViewAdapter.MyViewHolderItem(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final CommentHistoryRecyclerViewAdapter.MyViewHolderItem vhItem, int position) {

            final Map<String, Object> obj = itemList.get(position);
            vhItem.tvUsername.setText((String)obj.get("created_by"));
            vhItem.tvComment.setText((String)obj.get("messages"));
            Picasso.get().load(funnyimgs[new Random().nextInt(3)]).into(vhItem.ivPicture);

            try {
                if(!(obj.get("created_at")+"").equals("null")) {
                    Date mDate = sdf.parse((String) obj.get("created_at"));
                    long your_time_in_milliseconds = mDate.getTime();
                    long current_time_in_millisecinds = System.currentTimeMillis();

                    CharSequence thedate = DateUtils.getRelativeTimeSpanString(your_time_in_milliseconds, current_time_in_millisecinds, DateUtils.MINUTE_IN_MILLIS);
                    vhItem.tvCreatedDate.setText(thedate);
                }else{
                    vhItem.tvCreatedDate.setText("null");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            vhItem.clBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        Integer id = (int)obj.get("meme_id");
                        String title = (String)obj.get("title");
                        String type = (String)obj.get("type");
                        JSONObject imagesObject = null;
                        imagesObject = new JSONObject((String)obj.get("images"));
                        String coverUrl = imagesObject.getJSONObject("image700").getString("url");
                        String category = (String)obj.get("post_section");
                        String videoUrl = null;
                        boolean isVideo = false;
                        boolean hasAudio = false;
                        if (type.equalsIgnoreCase("animated")) {
                            isVideo = true;
                            videoUrl = imagesObject.getJSONObject("image460sv").getString("url");
                            hasAudio = (imagesObject.getJSONObject("image460sv").getInt("hasAudio") == 1 ? true : false);
                        }

                        int width = imagesObject.getJSONObject("image700").getInt("width");
                        int height = imagesObject.getJSONObject("image700").getInt("height");
                        GlobalFunc.currentVideoItem = new DirectLinkVideoItem(id, category, title, videoUrl, null, null, Picasso.get(), coverUrl, width, height, hasAudio, isVideo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent i = new Intent(context, CommentActivity.class);
                    i.putExtra("meme_id",(int)obj.get("meme_id"));
                    context.startActivity(i);
                }
            });


    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

}