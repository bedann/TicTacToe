package com.intersofteagles.tictactoe.POJOs;

/**
 * Created by Monroe on 4/27/2017.
 */
public class Symbol {

    private String title;
    private int symbol;
    private int image = -1;
    private String raw;

    public Symbol() {
    }

    public Symbol(String title, int symbol, int image, String raw) {
        this.title = title;
        this.symbol = symbol;
        this.image = image;
        this.raw = raw;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSymbol() {
        return symbol;
    }

    public void setSymbol(int symbol) {
        this.symbol = symbol;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }
}
