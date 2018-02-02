package com.intersofteagles.tictactoe.POJOs;

import java.util.List;

/**
 * Created by Monroe on 4/25/2017.
 */
public class Game {


    private String game_id,winner;
    private Player player_1,player_2;
    private long start_time,end_time;
    private List<Move> moves;
    private List<Message> messages;

    public Game() {
    }

    public Game(String game_id, String winner, Player player_1, Player player_2, long start_time, long end_time, List<Move> moves, List<Message> messages) {
        this.game_id = game_id;
        this.winner = winner;
        this.player_1 = player_1;
        this.player_2 = player_2;
        this.start_time = start_time;
        this.end_time = end_time;
        this.moves = moves;
        this.messages = messages;
    }

    public void setPlayer_1(Player player_1) {
        this.player_1 = player_1;
    }

    public void setPlayer_2(Player player_2) {
        this.player_2 = player_2;
    }

    public Player getPlayer_1() {
        return player_1;
    }

    public Player getPlayer_2() {
        return player_2;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }


    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
