package jjv.uem.com.aidu.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import jjv.uem.com.aidu.Model.Service;
import jjv.uem.com.aidu.R;
import jjv.uem.com.aidu.UI.Chats;

/**
 * Created by javi_ on 10/06/2017.
 */

public class ChatServiceAdapter extends RecyclerView.Adapter<ChatServiceAdapter.ViewHolder> {
    private static final int LONGITUD_CARD = 14;
    private Context context;
    private ArrayList<Service> serviceList;
    private OnItemClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvServiceTitle, tvServiceAuthor;
        private ImageView ivServiceImage;

        public ViewHolder(View view){
            super(view);

            tvServiceTitle = (TextView)view.findViewById(R.id.tv_servicechat_name);
            tvServiceAuthor = (TextView)view.findViewById(R.id.tv_servicechat_author);
            ivServiceImage = (ImageView)view.findViewById(R.id.iv_service_pic);
        }

        public void bind(final Service item, final OnItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    // constructor
    public ChatServiceAdapter(Context context, ArrayList<Service> services, OnItemClickListener listener){
        this.context = context;
        this.serviceList = services;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Service item);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_chat_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Service currentService = serviceList.get(position);
        String title = currentService.getTitle();
        String author = currentService.getUserName();

        if(title.length()>LONGITUD_CARD){
            title = currentService.getTitle().substring(0,LONGITUD_CARD)+"...";
        }
        if(author.length()>LONGITUD_CARD){
            author = currentService.getTitle().substring(0,LONGITUD_CARD)+"...";
        }

        holder.tvServiceTitle.setText(holder.tvServiceTitle.getContext().getString(R.string.tv_chat_service_name, title));
        holder.tvServiceAuthor.setText(holder.tvServiceAuthor.getContext().getString(R.string.tv_chat_service_author, author));
        holder.bind(serviceList.get(position), listener);

        // carga la imagen
        Glide.with(context).load(currentService.getPhotos().get(0)).into(holder.ivServiceImage);

    }


    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
