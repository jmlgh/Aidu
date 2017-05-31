package jjv.uem.com.aidu.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import jjv.uem.com.aidu.Model.Community;
import jjv.uem.com.aidu.R;

/**
 * Created by javi_ on 24/05/2017.
 */

public class CommunitiesCardAdapter extends RecyclerView.Adapter<CommunitiesCardAdapter.MyViewHolder> {
    private Context mContext;
    private List<Community> communities;
    private OnItemClickListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, author;
        public ImageView thumbnnail, dots;

        public MyViewHolder(View v){
            super(v);
            title = (TextView)v.findViewById(R.id.tv_communitytitle);
            thumbnnail = (ImageView)v.findViewById(R.id.imv_community_thumbnail);
        }

        public void bind(final Community item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public CommunitiesCardAdapter(Context mContext, List<Community> communities, OnItemClickListener listener){
        this.mContext = mContext;
        this.communities = communities;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_communities, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Community communityCard = communities.get(position);
        holder.title.setText(communityCard.getName());
        holder.bind(communities.get(position),listener);

        // loading album cover using Glide library
        Glide.with(mContext).load(communityCard.getImage()).into(holder.thumbnnail);


    }



    /**
     * Click listener for popup menu items
     */


    @Override
    public int getItemCount() {
        return communities.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Community item);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
