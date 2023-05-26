package edu.msu.moranti1.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.Policy;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    String player1;
    String player2;
    String user;
    String pass;
    String gamename, hostid, guestid, state;
    Handler mHandler;
    String gameid;
    Cloud cloud = new Cloud();

//    public int getTurn() {
//        return turn;
//    }
//
//    public void setTurn(int turn) {
//        this.turn = turn;
//        Log.d("TAG", "setTurn:1111 "+turn);
//    }

    int turn;

    TextView textView;
    Board board;

    public ArrayList<GamePiece> getGamePieces() {
        return gamePieces;
    }

    public void setGamePieces(ArrayList<GamePiece> gamePieces) {
        this.gamePieces = gamePieces;
    }

    public ArrayList<GamePiece> gamePieces = new ArrayList<GamePiece>();

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        getGameView().saveInstanceState(bundle);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        textView = findViewById(R.id.PlayerTurn);
        gamename = getIntent().getStringExtra("name");
        hostid = getIntent().getStringExtra("hostid");
        guestid = getIntent().getStringExtra("guestid");
        gameid = getIntent().getStringExtra("gameid");
        state = getIntent().getStringExtra("state");
        SharedPreferences prefs = getSharedPreferences(getString(R.string.mypref), MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getString(R.string.player_1), hostid);
        editor.putString(getString(R.string.player_2), guestid);
        editor.apply();
        player1 = prefs.getString(getString(R.string.player_1), "");
        player2 = prefs.getString(getResources().getString(R.string.player_2), "");
        user = prefs.getString(getString(R.string.saveuser), "");
        pass = prefs.getString(getString(R.string.savepass), "");
        turn = prefs.getInt(getResources().getString(R.string.turn), 1);
        //textView.setText(getString(R.string.player1) +' '+ player1);
//        state = String.valueOf(turn);
        board = new Board(this);
        if (state != "" && state != null) {
            int turn = Integer.parseInt(state.substring(2,3));
            board.setTurn(turn);
        }
        checkForTurn(this, board.getTurn());
        getGameView().setPlayer1Name(player1);
        if (!player2.equals("0")) {
            getGameView().setPlayer2Name(player2);
        }


////        Log.e("TAG", "onCreate: name1111"+a);
//        Toast.makeText(this, a, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, b, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, c, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, d, Toast.LENGTH_SHORT).show();
        if (savedInstanceState != null) {
            getGameView().loadInstanceState(savedInstanceState);
        }

//        getGameView().getBoard().LoadGameStatus("13,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,1,2,2,1,0,0,2,2,1,2,2,1,0,1,1");

        this.mHandler = new Handler();
        this.mHandler.postDelayed(m_Runnable,0);

    }


    public void onSurrender(View view) {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.mypref), MODE_PRIVATE);
        turn = prefs.getInt("turn", 1);
        getGameView().getBoard().resetGame();
        getGameView().invalidate();
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("gamename", gamename);
        if (turn == 1) {
            intent.putExtra("winner", player2);
        } else {
            intent.putExtra("winner", player1);
        }
        this.finish();
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_game, menu);
        return true;
    }

    public void onDone(View view) {
        int winner = getGameView().getBoard().onDone(this);
        getGameView().invalidate();
        savingGameState(gamename, hostid, guestid, getGameView().getBoard().SaveGameStatus());
        if (winner != 0) {
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("gamename", gamename);
            if (winner == 1) {
                intent.putExtra("winner", player1);
            } else if (winner == 2) {
                intent.putExtra("winner", player2);
            }
            getGameView().getBoard().resetGame();
            this.finish();
            startActivity(intent);
        }


    }

    private void savingGameState(String name, String hostid, String guestid, String state) {
        Cloud c = new Cloud();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    String msg = c.saveCatalog(name, hostid, guestid, state);
                    c.saveCatalog(name, hostid, guestid, state);
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            Toast.makeText(GameActivity.this, "msg", Toast.LENGTH_SHORT).show();
//                        }
//                    });
                } catch (Exception e) {
                    Log.e("TAG", "run" + e);
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String id = c.getToken(user);
                    if (id != null) {
                        Log.e("FCM", "Success");
                    }
                } catch (Exception e) {
                    Log.e("TAG", "run" + e);
                }
            }
        }).start();

    }

    public GameView getGameView() {
        return this.findViewById(R.id.boardView);
    }

    public void onBackPressed() {
        super.onBackPressed();
        gamePieces.clear();
        saveturn(this,1);
    }

    /**
     * Undo button handler
     */
    public void onUndo(View view) {
        getGameView().getBoard().onUndo();
        getGameView().invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                getGameView().getBoard().resetGame();
                getGameView().invalidate();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void checkForTurn(Context context, int turn) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.mypref), MODE_PRIVATE);
        player1 = prefs.getString(context.getResources().getString(R.string.player_1), "");
        player2 = prefs.getString(context.getResources().getString(R.string.player_2), "");
        textView = ((Activity) context).findViewById(R.id.PlayerTurn);
        if (turn == 1) {
            textView.setText(context.getString(R.string.player1)+' ' + player1);
        } else {

            textView.setText(context.getString(R.string.player2)+' ' + player2);
        }

    }

    public void saveturn(Context context, int turn) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.mypref), MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(context.getString(R.string.turn), turn);
        editor.apply();
    }

    public int put_turn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.mypref), MODE_PRIVATE);
        if (state == null) {
            return 1;
        }
        return prefs.getInt(context.getResources().getString(R.string.turn), 1);
    }


    private final Runnable m_Runnable = new Runnable() {
        public void run() {
//            Toast.makeText(GameActivity.this, "in runnable", Toast.LENGTH_SHORT).show();
            GameActivity.this.mHandler.postDelayed(m_Runnable, 1000);
            try {
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try  {
                            state = cloud.loadCatalog(user,pass,gamename);
                            getGameView().getBoard().LoadGameStatus(state);
                            checkForTurn(GameActivity.this, getGameView().getBoard().getTurn());
                            int winner = getGameView().getBoard().getWinner();
                            if(winner != 0) {
                                Intent intent = new Intent(GameActivity.this, ResultActivity.class);
                                intent.putExtra("gamename", gamename);
                                if (winner == 1) {
                                    intent.putExtra("winner", player1);
                                } else if (winner == 2) {
                                    intent.putExtra("winner", player2);
                                }
                                getGameView().getBoard().resetGame();
                                GameActivity.this.finish();
                                startActivity(intent);
                            }

                        } catch (Exception e) {
                            Log.e("error", "game won");
                        }
                    }
                });

                thread.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
            //getGameView().getBoard().LoadGameStatus(state);
        }

    };//runnable

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(m_Runnable);
        finish();

    }
}
