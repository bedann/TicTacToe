package com.intersofteagles.tictactoe.Commoners;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.intersofteagles.tictactoe.Adapters.MovesAdapter;
import com.intersofteagles.tictactoe.POJOs.Move;
import com.intersofteagles.tictactoe.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Monroe on 4/26/2017.
 */
public class Bot implements PlayerProperties {

    private String uid;
    private String[] usernames = {"Tee-Nine","Bot 2.0","G-Bot"};
    private long think_duration[] = {500,1000,1500,700,600,800,900,1200,1300};
    private Random r = new Random();
    private MovesAdapter adapter;
    private  List<Move> moves = new ArrayList<>();
    private int symbol = R.drawable.bender;
    private BotListener listener;

    public Bot(BotListener listener) {
        this.listener = listener;
        uid = String.valueOf(System.currentTimeMillis());
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public String getUsername() {
        return usernames[r.nextInt(usernames.length-1)];
    }

    public int getSymbol() {
        return symbol;
    }

    public void attachAdapter(MovesAdapter adapter){
        this.adapter = adapter;
    }


    public void play(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                executeMove();
            }
        }, think_duration[r.nextInt(think_duration.length - 1)]);
    }

    private void executeMove(){
        moves = adapter.getMoves();
        if (playRows()){
            return;
        }else if (playDiagss()){
            return;
        }else if (playCols()){
            return;
        }else {
            playAny();
        }
    }

    public boolean playRows(){
        if (defend(0, 1, 2)){
            return true;
        }else if (defend(3,4,5)){
            return true;
        }else if (defend(6,7,8)){
            return true;
        }
        return false;
    }

    public boolean playCols(){
        if (defend(0,3,6)){
            return true;
        }else if (defend(1,4,7)){
            return true;
        }else if (defend(2,5,8)){
            return true;
        }
        return false;
    }


    public boolean playDiagss(){
        if (defend(0,4,8)){
            return true;
        }else if (defend(2,4,6)){
            return true;
        }
        return false;
    }


    private void playAny(){
        Log.e("PLAYING","<<<<ANY>>>>");
        List<Integer> empties = new ArrayList<>();
        for (Move m:moves){
            if (m.getSymbol()==-1)empties.add(m.getIndex());
        }
        try {
            listener.onBotDecided(new Move(empties.get(r.nextInt(empties.size()-1))));
        }catch (Exception e){
            if (!empties.isEmpty())listener.onBotDecided(new Move(empties.get(0)));
        }
    }


    private boolean defend(int ... indices){
        int count_them = 0, index = -1,index2 = -1,count_me = 0;
        for (int i:indices){
            if (symbol == moves.get(i).getSymbol()){//Mine
                index = i;
                count_me++;
            }else if (moves.get(i).getSymbol() != -1){
                count_them++;
                index2 = i;
            }
        }
        if (count_them == 2 && index != -1){
            listener.onBotDecided(new Move(index));
            return true;
        }
        if (count_me == 2 && index != -1){//offense
            listener.onBotDecided(new Move(index2));
            return true;
        }
        return false;
    }


    private int validate2Rows(int start){
        int him = -1;
        int me = -1;
        int indexh = -1,indexm = -1;
        for (int i = start;i<=start+2;i++){
            if (moves.get(i).getSymbol() == -1)continue;
            if (moves.get(i).getSymbol() != this.symbol){
                him++;
                indexh = i;
            }else {
                me++;
                indexm = i;
            }
        }
        if (him==2){//BLOCK
            listener.onBotDecided(new Move(indexm));
            return -2;
        }else if (me ==2){//FILL SPACE
            listener.onBotDecided(new Move(indexh));
            return -2;
        }
        if (start<6){
            return validate2Rows(start+3);
        }
        return -1;
    }

    private int validate2Columns(int start){
        int him = -1;
        int me = -1;
        int indexh = -1,indexm = -1;
        for (int i = start;i<=start+6;i+=3){
            if (moves.get(i).getSymbol() == -1)continue;
            if (moves.get(i).getSymbol()!=symbol){
                him++;
                indexh=1;
            }else {
                me++;
                indexm = i;
            }
        }
        if (him==2){//BLOCK
            listener.onBotDecided(new Move(indexm));
            return -2;
        }else if (me ==2){//FILL SPACE
            listener.onBotDecided(new Move(indexh));
            return -2;
        }
        if (start<2){
            return validate2Columns(start + 1);
        }
        return -1;
    }


    public int validate2Diagonal(){
        int indexh = 0,indexm = 0,him=-1,me=-1;
        for (int i = 0;i<9;i+=4){
            if (moves.get(i).getSymbol() == -1)continue;
            if (moves.get(i).getSymbol()!=symbol){
                him++;
                indexh=i;
            }else {
                me++;
                indexm = i;
            }
        }
        if (him==2){//BLOCK
            listener.onBotDecided(new Move(indexm));
            return -2;
        }else if (me ==2){//FILL SPACE
            listener.onBotDecided(new Move(indexh));
            return -2;
        }
        him = 0;
        me = 0;
        for (int i = 2;i<7;i+=2){
            if (moves.get(i).getSymbol() == -1)continue;
            if (moves.get(i).getSymbol()!=symbol){
                him++;
                indexh = i;
            }else {
                me++;
                indexm = i;
            }
        }
        if (him==2){//BLOCK
            listener.onBotDecided(new Move(indexm));
            return -2;
        }else if (me ==2){//FILL SPACE
            listener.onBotDecided(new Move(indexh));
            return -2;
        }
        return -1;
    }



    public interface BotListener{
        void onBotDecided(Move move);
    }

}
