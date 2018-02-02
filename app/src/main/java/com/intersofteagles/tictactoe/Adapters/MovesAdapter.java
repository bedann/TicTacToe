package com.intersofteagles.tictactoe.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.intersofteagles.tictactoe.Commoners.MyBounceInter;
import com.intersofteagles.tictactoe.POJOs.Move;
import com.intersofteagles.tictactoe.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Monroe on 4/25/2017.
 */
public class MovesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    Context context;
    List<Move> moves = new ArrayList<>();
    LayoutInflater inflater;
    GameListener listener;

    public MovesAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        listener = (GameListener)context;

        for (int i=0;i<9;i++)moves.add(new Move(i));
        notifyDataSetChanged();
    }


    public void commitMove(Move move){
        moves.set(move.getIndex(), move);
        notifyItemChanged(move.getIndex());

        int row = validateRows(0);
        int col = validateColumns(0);
        int diag = validateDiagonal();
    }


    public int validateRows(int start){
        int symbol = moves.get(start).getSymbol();
        int count = 0;
        for (int i = start;i<=start+2;i++){
            if (moves.get(i).getSymbol()==symbol)count++;
        }
        if (count==3 && symbol != -1){
            listener.onGameWon(symbol,start,start+1,start+2);
            return count;
        }
        if (start<6){
            return validateRows(start+3);
        }
        return count;
    }

    public int validateColumns(int start){
        int symbol = moves.get(start).getSymbol();
        int count = 0;
        for (int i = start;i<=start+6;i+=3){
            if (moves.get(i).getSymbol()==symbol)count++;
        }
        if (count==3 && symbol != -1){
            listener.onGameWon(symbol,start,start+3,start+6);
            return count;
        }
        if (start<2){
            return validateColumns(start + 1);
        }
        return count;
    }

    public int validateDiagonal(){
        int symbol = moves.get(0).getSymbol();
        int count = 0;
        for (int i = 0;i<9;i+=4){
            if (moves.get(i).getSymbol()==symbol)count++;
        }
        if (count==3 && symbol != -1){
            listener.onGameWon(symbol,0,4,8);
            return count;
        }
        symbol = moves.get(2).getSymbol();
        count = 0;
        for (int i = 2;i<7;i+=2){
            if (moves.get(i).getSymbol()==symbol)count++;
        }
        if (count==3 && symbol != -1){
            listener.onGameWon(symbol,2,4,6);
            return count;
        }
        return count;
    }


    public void resetGame(){
        for (int i=0;i<9;i++)moves.get(i).setSymbol(-1);
        notifyDataSetChanged();
    }

    public List<Move> getMoves() {
        return moves;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Move move = moves.get(position);
        switch (holder.getItemViewType()){

            default:
                DefaultHolder h = (DefaultHolder)holder;
                h.image.setImageBitmap(null);
                if (move.getSymbol() != -1){
                    h.image.setImageResource(move.getSymbol());
                }
        }
        if (move.getSymbol()==-1)holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMoveMade(move);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){

            default:
                return new DefaultHolder(inflater.inflate(R.layout.row_move_default,parent,false));
        }
    }

    @Override
    public int getItemCount() {
        return moves.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    class DefaultHolder extends RecyclerView.ViewHolder{
        ImageView image;
        public DefaultHolder(View v) {
            super(v);
            image = (ImageView)v.findViewById(R.id.image);
        }
    }















    public interface GameListener{
        void onMoveMade(Move move);
        void onGameWon(int symbol,int ... indices);
    }


}
