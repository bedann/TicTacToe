package com.intersofteagles.tictactoe.POJOs;

/**
 * Created by Monroe on 4/25/2017.
 */
public class Move {

    private int index,symbol =-1;
    private String player_id;
    private long time;

    public Move() {
    }

    public Move(int index) {
        this.index = index;
    }

    public Move(int index, int symbol) {
        this.index = index;
        this.symbol = symbol;
    }

    public Move(int index, int symbol, String player_id, long time) {
        this.index = index;
        this.symbol = symbol;
        this.player_id = player_id;
        this.time = time;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSymbol() {
        return symbol;
    }

    public void setSymbol(int symbol) {
        this.symbol = symbol;
    }

    public String getPlayer_id() {
        return player_id;
    }

    public void setPlayer_id(String player_id) {
        this.player_id = player_id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Move{" +
                "index=" + index +
                ", symbol=" + symbol +
                ", player_id='" + player_id + '\'' +
                ", time=" + time +
                '}';
    }
}
