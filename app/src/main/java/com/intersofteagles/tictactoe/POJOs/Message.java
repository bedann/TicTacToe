package com.intersofteagles.tictactoe.POJOs;

/**
 * Created by Monroe on 4/25/2017.
 */
public class Message {

    private String message_id,player,player_id,text;
    private int type;
    private long time;

    public Message() {
    }

    public Message(String message_id,String player, String player_id, String text, int type, long time) {
        this.message_id = message_id;
        this.player_id = player_id;
        this.player = player;
        this.text = text;
        this.type = type;
        this.time = time;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getPlayer_id() {
        return player_id;
    }

    public void setPlayer_id(String player_id) {
        this.player_id = player_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return String.format("%s: %s",player.toUpperCase(),text.toLowerCase());
    }
}
