package com.dovoo.memesnetwork.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dovoo.memesnetwork.R;
import com.dovoo.memesnetwork.components.TextViewFaSolid;
import com.dovoo.memesnetwork.adapter.items.DirectLinkItemTest;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<Map<String, Object>> itemList;
    private DirectLinkItemTest videoItem;
    private Context context;
    public SimpleExoPlayer player;
    float finalWidth;
    float finalHeight;
    float maxHeightVideo;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int[] funnyimgs = new int[] {R.drawable.funny_user1,R.drawable.funny_user2,R.drawable.funny_user3};


    public CommentRecyclerViewAdapter(Context context, List<Map<String, Object>> itemList,DirectLinkItemTest videoItem) {
        this.context = context;
        this.itemList = itemList;
        this.videoItem = videoItem;
        finalWidth = context.getResources().getDisplayMetrics().widthPixels;  // default phone width
        finalHeight = context.getResources().getDisplayMetrics().heightPixels; // default phone heights
        maxHeightVideo = (float) context.getResources().getDisplayMetrics().heightPixels * 0.8f; // set default maximum video size in phone
        player = ExoPlayerFactory.newSimpleInstance(context);
    }

    class MyViewHolderHeader extends RecyclerView.ViewHolder {
        public final RelativeLayout relativeLayout;
        public final PlayerView playerView;
        public final ImageView mCover;
        public final TextViewFaSolid tvIconSound;
        public final TextView tvTitle,tvLabelNoAudio;
        public MyViewHolderHeader(View view) {
            super(view);
            relativeLayout = view.findViewById(R.id.relativeLayout);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvLabelNoAudio = view.findViewById(R.id.tvLabelNoAudio);
            playerView = view.findViewById(R.id.playerView);
            mCover = view.findViewById(R.id.cover);
            tvIconSound = view.findViewById(R.id.tvIconSound);
            playerView.setPlayer(player);

        }
    }

    class MyViewHolderItem extends RecyclerView.ViewHolder {
        TextView tvUsername,tvComment,tvCreatedDate;
        ImageView ivPicture;
        public MyViewHolderItem(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvCreatedDate = itemView.findViewById(R.id.tvCreatedDate);
            ivPicture = itemView.findViewById(R.id.ivPicture);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_header_comment, parent, false);
            return new MyViewHolderHeader(view);
        } else if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comment, parent, false);
            return new MyViewHolderItem(view);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolderHeader) {
            MyViewHolderHeader vhHeader = (MyViewHolderHeader) holder;
            if (videoItem.getmHeight() > videoItem.getmWidth()) {
                // if video is potrait
                float ratio = (float) videoItem.getmHeight() / videoItem.getmWidth();

                finalHeight = finalWidth * ratio;
                // if final height higher or same with phone height, we have to decrease it to make the video fit in phone. It will show around 3/4 phone screen
                if (finalHeight >= maxHeightVideo) {
                    finalHeight = maxHeightVideo * 0.7f;
                }

            } else if (videoItem.getmHeight() < videoItem.getmWidth()) {
                // if video is landscape
                float ratio = (float) videoItem.getmWidth() / videoItem.getmHeight();

                finalHeight = finalWidth / ratio;
            } else {
                // if video is square
                finalHeight = finalWidth;
            }

            ViewGroup.LayoutParams layoutParams = vhHeader.relativeLayout.getLayoutParams();
            layoutParams.width = (int) finalWidth;
            layoutParams.height = (int) finalHeight;
            vhHeader.relativeLayout.setLayoutParams(layoutParams);


            vhHeader.tvTitle.setText(videoItem.getmTitle());
//            vhHeader.mCover.setVisibility(View.VISIBLE);
//
//            Picasso.get().load(directLinkVideoItem.getmCoverUrl())
//                    .memoryPolicy(MemoryPolicy.NO_CACHE)
//                    .networkPolicy(NetworkPolicy.NO_CACHE)
//                    .resize(0, vhHeader.mCover.getHeight())
//                    .into(vhHeader.mCover);
// Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, "yourApplicationName"));
// This is the MediaSource representing the media to be played.
            if(videoItem.getmDirectUrl()!=null) {

                MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(videoItem.getmDirectUrl()));

// Prepare the player with the source.
                player.prepare(videoSource);
                player.setPlayWhenReady(true);
                vhHeader.mCover.setVisibility(View.GONE);

            }else{

                vhHeader.playerView.setVisibility(View.GONE);
                vhHeader.mCover.setVisibility(View.VISIBLE);
                videoItem.getmImageLoader().load(videoItem.getmCoverUrl()).into(vhHeader.mCover);
            }
        } else if (holder instanceof MyViewHolderItem) {
            final Map<String, Object> obj = itemList.get(position-1);
            MyViewHolderItem vhItem = (MyViewHolderItem) holder;
            //vhItem.tvUsername.setText((String)obj.get("created_by"));
            vhItem.tvUsername.setText((String)obj.get("created_by"));
            vhItem.tvComment.setText((String)obj.get("messages"));
            Picasso.get().load(funnyimgs[new Random().nextInt(3)]).into(vhItem.ivPicture);

            try {
                if(!(obj.get("created_at")+"").equals("null")) {
                    Date createdAtDate = sdf.parse((String) obj.get("created_at"));
                    Date currentDate = sdf.parse((String) obj.get("current_datetime"));
                    long createAtMiliseconds = createdAtDate.getTime();
                    long currentTimeMiliseconds = currentDate.getTime();

                    CharSequence thedate = DateUtils.getRelativeTimeSpanString(createAtMiliseconds, currentTimeMiliseconds, DateUtils.MINUTE_IN_MILLIS);
                    vhItem.tvCreatedDate.setText(thedate);
                }else{
                    vhItem.tvCreatedDate.setText("null");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    private boolean isPositionHeader(int position) {
        return position == TYPE_HEADER;
    }

    @Override
    public int getItemCount() {
        return itemList.size() + 1;
    }

}