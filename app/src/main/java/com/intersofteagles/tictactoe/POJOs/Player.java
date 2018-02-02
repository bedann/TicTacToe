package com.intersofteagles.tictactoe.POJOs;

import com.intersofteagles.tictactoe.Commoners.PlayerProperties;

/**
 * Created by Monroe on 4/25/2017.
 */
public class Player implements PlayerProperties {

    private String uid,username,email;
    private int symbol = -1,games_won,games_lost;
    private long last_seen;

    public Player() {
    }

    public Player(String uid, String username, String email) {
        this.uid = uid;
        this.username = username;
        this.email = email;
    }

    public Player(String uid, String username, String email, int symbol, int games_won, int games_lost, long last_seen) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.symbol = symbol;
        this.games_won = games_won;
        this.games_lost = games_lost;
        this.last_seen = last_seen;
    }

    public int getSymbol() {
        return symbol;
    }

    public void setSymbol(int symbol) {
        this.symbol = symbol;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGames_won() {
        return games_won;
    }

    public void setGames_won(int games_won) {
        this.games_won = games_won;
    }

    public int getGames_lost() {
        return games_lost;
    }

    public void setGames_lost(int games_lost) {
        this.games_lost = games_lost;
    }

    public long getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(long last_seen) {
        this.last_seen = last_seen;
    }
}
