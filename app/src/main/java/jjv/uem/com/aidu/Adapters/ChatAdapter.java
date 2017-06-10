package jjv.uem.com.aidu.Adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import jjv.uem.com.aidu.Model.MensajeChat;
import jjv.uem.com.aidu.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{

    private static final String TAG = "ChatAdapter";
    ArrayList<MensajeChat> mensajes;

    public ChatAdapter(ArrayList<MensajeChat> mensajes){
        this.mensajes = mensajes;
    }


    @Override
    public ChatAdapter.ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        ChatViewHolder cvh = new ChatViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(ChatAdapter.ChatViewHolder holder, int position) {
        holder.bind(mensajes.get(position));

    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{
        TextView tvNombre, tvHora, tvMensaje;


        public ChatViewHolder(View itemView) {
            super(itemView);

            tvNombre = (TextView) itemView.findViewById(R.id.tv_chat_nombre);
            tvHora = (TextView) itemView.findViewById(R.id.tv_chat_hora);
            tvMensaje = (TextView) itemView.findViewById(R.id.tv_chat_msg);
        }
        // bind
        public void bind(MensajeChat msgChat){
            tvNombre.setText(String.format("%s: ", msgChat.getMsgEmisor()));
            tvMensaje.setText(msgChat.getMsgTexto());
            tvHora.setText(String.format("(%s)", msgChat.getMsgHora()));
        }
    }
}
