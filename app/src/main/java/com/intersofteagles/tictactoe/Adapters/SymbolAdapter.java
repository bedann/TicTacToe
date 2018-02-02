package com.intersofteagles.tictactoe.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.intersofteagles.tictactoe.POJOs.Symbol;
import com.intersofteagles.tictactoe.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Monroe on 4/27/2017.
 */
public class SymbolAdapter extends RecyclerView.Adapter<SymbolAdapter.Holder> {

    Context context;
    List<Symbol> symbols = new ArrayList<>();
    LayoutInflater inlfater;
    Listener listener;
    private  int ex = -1,index  = -1;

    public SymbolAdapter(Context context,int ex) {
        this.ex = ex;
        this.context = context;
        inlfater = LayoutInflater.from(context);
        listener = (Listener)context;

        symbols.add(new Symbol("Rick",R.drawable.rick,R.drawable.rick_big,"rick_phrase"));
        symbols.add(new Symbol("Morty",R.drawable.face,R.drawable.morty_hip,"oh_man"));
        symbols.add(new Symbol("Terry",R.drawable.scary_terry,R.drawable.scary_terry_big,/*"aww_bitch"*/"oh_man"));
        symbols.add(new Symbol("Mr Meeseeks",R.drawable.meeseeks,R.drawable.meeseeks_big,"mr_meeseeks"));
        symbols.add(new Symbol("Mr Goldenfold",R.drawable.goldenfold,R.drawable.goldenfold_big,"my_man"));
        notifyDataSetChanged();

        if (ex != -1){
            for (Symbol s:symbols){
                if (s.getSymbol() == ex){
                    symbols.remove(s);
                    break;
                }
            }
            notifyDataSetChanged();
        }
    }

    public void disable(int index){
        this.index = index;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(inlfater.inflate(R.layout.row_symbol,parent,false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position)
    {
        final int a = position;
        final Symbol symbol = symbols.get(position);
        holder.title.setText(symbol.getTitle());
        if (symbol.getImage() != -1){
            holder.image.setImageResource(symbol.getImage());
        }else{
            holder.image.setImageResource(symbol.getSymbol());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(symbol,a);
            }
        });
        if (index != -1)holder.image.setAlpha(index!=position?0.2f:1f);
    }

    @Override
    public int getItemCount() {
        return symbols.size();
    }

    class Holder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView image;
        public Holder(View v) {
            super(v);
            title = (TextView)v.findViewById(R.id.title);
            image = (ImageView)v.findViewById(R.id.image);
        }
    }

    public interface Listener{
        void onClick(Symbol symbol,int i);
    }
}
