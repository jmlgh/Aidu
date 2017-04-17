package jjv.uem.com.aidu.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import jjv.uem.com.aidu.Model.Service;
import jjv.uem.com.aidu.util.TextViewCustom;
import jjv.uem.com.aidu.R;

/**
 * Created by Victor on 16/04/2017.
 */

public class Service_Adapter_RV extends RecyclerView.Adapter<Service_Adapter_RV.ServiceViewHolder> {

    private ArrayList<Service>services;
    private OnItemClickListener listener;


    public Service_Adapter_RV(ArrayList<Service> services, OnItemClickListener listener) {
        this.services = services;
        this.listener = listener;
    }

    @Override
    public ServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_service,parent,false);
        ServiceViewHolder svh = new ServiceViewHolder(v);
        return svh;
    }

    @Override
    public void onBindViewHolder(final ServiceViewHolder holder, int position) {
        holder.bind(services.get(position),listener);
        holder.title.setText(services.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ServiceViewHolder extends  RecyclerView.ViewHolder{
        TextViewCustom title;

         //TODO resto de elementos el icono

        public ServiceViewHolder(View itemView) {
            super(itemView);

            title = (TextViewCustom) itemView.findViewById(R.id.title_service_card);


        }

        public void bind(final Service item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Service item);
    }


}
