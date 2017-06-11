package jjv.uem.com.aidu.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jjv.uem.com.aidu.R;

public class Images_Adapter extends BaseAdapter {

    Context contexto;
    ArrayList<String> photos;
    boolean guardando;


    public Images_Adapter(Context contexto, ArrayList<String> photos,boolean guardando) {
        this.contexto = contexto;
        this.photos = photos;
        this.guardando = guardando;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int i) {
        return photos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView==null){
            convertView= LayoutInflater.from(contexto).inflate(R.layout.imagelist_item,null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.img_photo);
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        if(this.guardando){
            Picasso.with(contexto).load(photos.get(position)).into(holder.image);
        }else{
            Picasso.with(contexto).load(photos.get(position)).into(holder.image);
        }




        return convertView;
    }



    private class ViewHolder{
        ImageView image;

    }
}
