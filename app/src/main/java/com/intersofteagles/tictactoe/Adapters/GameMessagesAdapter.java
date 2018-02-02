package com.intersofteagles.tictactoe.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.intersofteagles.tictactoe.POJOs.Message;
import com.intersofteagles.tictactoe.POJOs.Move;
import com.intersofteagles.tictactoe.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Monroe on 4/25/2017.
 */
public class GameMessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    Context context;
    List<Message> messages = new ArrayList<>();
    LayoutInflater inflater;
    private final int ME = 0,THEM=1;

    public GameMessagesAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);



    }


    public void addMessage(Message message){
        messages.add(0,message);
        notifyItemInserted(0);
    }



    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Message msg = messages.get(position);
        DefaultHolder h = (DefaultHolder)holder;
        h.text.setText(msg.getText());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case ME:
                return new DefaultHolder(inflater.inflate(R.layout.row_game_message,parent,false));
            case THEM:
                return new DefaultHolder(inflater.inflate(R.layout.row_game_message_them,parent,false));
            default:
                return new DefaultHolder(inflater.inflate(R.layout.row_game_message,parent,false));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getPlayer_id().equals("me")?ME:THEM;//TODO change
    }


    class DefaultHolder extends RecyclerView.ViewHolder{
        TextView text;
        public DefaultHolder(View v) {
            super(v);
            //TODO add time
            text = (TextView)v.findViewById(R.id.text);
        }
    }















    public interface GameListener{
        void onMoveMade(Move move, View box);
    }


}
