package com.intersofteagles.tictactoe.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intersofteagles.tictactoe.Commoners.MDate;
import com.intersofteagles.tictactoe.POJOs.Player;
import com.intersofteagles.tictactoe.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Monroe on 4/28/2017.
 */
public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.Holder>{


    Context context;
    LayoutInflater inflater;
    List<Player> players = new ArrayList<>();

    public PlayersAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void add(Player p){
        players.add(p);
        notifyItemInserted(players.size()-1);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.row_player,parent,false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Player player = players.get(position);
        holder.last_seen.setText(new MDate(player.getLast_seen()+"").getRelativeTime());
        holder.name.setText(player.getUsername());
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    class Holder extends RecyclerView.ViewHolder{
        TextView name;
        TextView last_seen;
        public Holder(View v) {
            super(v);
            name = (TextView)v.findViewById(R.id.name);
            last_seen = (TextView)v.findViewById(R.id.last_seen);
        }
    }

}
