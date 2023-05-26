package edu.msu.moranti1.project1;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

public class GameView extends View {

    private Board board;

    private float mScaleFactor = 1.F;

    private String player1Name;
    private String player2Name;

    public GameView(Context context) {
        super(context);
        init(null, 0);
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(attrs, 0);
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        board = new Board(getContext());
        board.setView(this);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Context context = this.getContext();
        SharedPreferences prefs = context.getSharedPreferences(context.getResources().getString(R.string.mypref), Context.MODE_PRIVATE);
        String player = prefs.getString(context.getString(R.string.saveuser), "");
        if((getBoard().getTurn() == 1 && !(player.equals(player1Name))) || (getBoard().getTurn() == 2 && !(player.equals(player2Name)))) {
            return false;
        }
        return board.onTouchEvent(this, event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        board.draw(canvas);
    }
    public void saveInstanceState(Bundle bundle) {
        board.saveInstanceState(bundle);
    }
    public void loadInstanceState(Bundle bundles) {
        board.loadInstanceState(bundles);
    }

    public Board getBoard() {
        return board;
    }


    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }
}
