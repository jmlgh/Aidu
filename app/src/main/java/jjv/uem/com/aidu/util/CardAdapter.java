package jjv.uem.com.aidu.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import jjv.uem.com.aidu.Model.Service;
import jjv.uem.com.aidu.R;
import jjv.uem.com.aidu.UI.MainActivity;
import jjv.uem.com.aidu.UI.MyServices;
import jjv.uem.com.aidu.UI.ServiceView;

/**
 * Created by javi_ on 24/05/2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {
    private static final int LONGITUD_CARD = 14;
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
        View itemView;
        if(mContext.getClass().equals(MyServices.class)){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_service_dots, parent, false);
        }else{
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_service_alt, parent, false);
        }


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Service serviceCard = serviceList.get(position);
        String title = serviceCard.getTitle();
        String author = serviceCard.getUserName();

        if(title.length()>LONGITUD_CARD){
            title = serviceCard.getTitle().substring(0,LONGITUD_CARD)+"...";
        }


        if(author.length()>LONGITUD_CARD){
            author = serviceCard.getTitle().substring(0,LONGITUD_CARD)+"...";
        }

        holder.title.setText(title);
        holder.author.setText(author);
        holder.bind(serviceList.get(position),listener);

        // loading album cover using Glide library
        Glide.with(mContext).load(serviceCard.getPhotos().get(0)).into(holder.thumbnnail);
        if(mContext.getClass().equals(MyServices.class)){
            holder.dots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(holder.dots,serviceList.get(position).getServiceKey());
                }
            });

            // no funciona... por que?
            /*holder.thumbnnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(holder.thumbnnail.getContext(),
                            ServiceView.class);
                    i.putExtra(MainActivity.KEY_SERVICE, serviceList.get(position).getServiceKey());
                    mContext.startActivity(i);
                }
            });*/
        }

    }

     class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private String key ;

         public MyMenuItemClickListener(String serviceKey) {
             this.key = serviceKey;
         }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.action_more_info:
                    Intent i = new Intent(mContext,ServiceView.class);
                    i.putExtra(MyServices.KEY_SERVICE,key);
                    mContext.startActivity(i);
                    return true;
                case R.id.action_delete:

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setCancelable(true);
                    builder.setTitle(mContext.getString(R.string.atention));
                    builder.setMessage(mContext.getString(R.string.message_atention));
                    builder.setPositiveButton(mContext.getString(R.string.action_delete),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                   // Toast.makeText(mContext, "delete: "+key, Toast.LENGTH_SHORT).show();
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                    Query query = ref.child("services").orderByChild("serviceKey").equalTo(key);
                                    Query queryTwo = ref.child("user-services/"
                                            + FirebaseAuth.getInstance().getCurrentUser().getUid()
                                    ).orderByChild("serviceKey").equalTo(key);


                                    ValueEventListener vee = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                                        Service s = ds.getValue(Service.class);
                                                        if(s!=null){
                                                            if(s.getUserkey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                                ds.getRef().removeValue();
                                                            }else{
                                                                s.setUserkeyInterested("");
                                                                s.setState(Constants.DISPONIBLE);
                                                                ds.getRef().setValue(s);
                                                            }
                                                        }
                                                    }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(mContext, mContext.getString(R.string.error_standar), Toast.LENGTH_SHORT).show();
                                        }
                                    };
                                    Log.i("SERVICE TO DELETE"," primera q");
                                    query.addListenerForSingleValueEvent(vee);
                                    Log.i("SERVICE TO DELETE"," segunda q");
                                    queryTwo.addListenerForSingleValueEvent(vee);



                                }
                            });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                default:
            }
            return false;
        }

     }

    private void showPopupMenu(View view, String serviceKey) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.card_options, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(serviceKey));
        popup.show();
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
