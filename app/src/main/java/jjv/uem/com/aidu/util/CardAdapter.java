package jjv.uem.com.aidu.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.util.List;

import jjv.uem.com.aidu.Model.Service;
import jjv.uem.com.aidu.R;

/**
 * Created by javi_ on 24/05/2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {
    private Context mContext;
    private List<Service> serviceList;
    private OnItemClickListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, author;
        public ImageView thumbnnail, dots;

        public MyViewHolder(View v){
            super(v);
            title = (TextView)v.findViewById(R.id.card_title);
            author = (TextView)v.findViewById(R.id.card_author);
            thumbnnail = (ImageView)v.findViewById(R.id.card_thumbnail);
            dots = (ImageView)v.findViewById(R.id.card_dots);
        }

        public void bind(final Service item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public CardAdapter(Context mContext, List<Service> serviceList, OnItemClickListener listener){
        this.mContext = mContext;
        this.serviceList = serviceList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_service_alt, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Service serviceCard = serviceList.get(position);
        holder.title.setText(serviceCard.getTitle());
        holder.author.setText(serviceCard.getUserName());
        holder.bind(serviceList.get(position),listener);

        // loading album cover using Glide library
        Glide.with(mContext).load(serviceCard.getPhotos().get(0)).into(holder.thumbnnail);

        holder.dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.dots);
            }
        });
    }

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.card_options, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_more_info:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_subscribe:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Service item);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
