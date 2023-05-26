package edu.msu.moranti1.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.checkerframework.checker.units.qual.C;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        TextView text = findViewById(R.id.ResultText);
        Intent intent = getIntent();
        String winner = intent.getStringExtra("winner")+" ";
        text.setText(winner+getString(R.string.winner));
    }
    public void onNewGame(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        String gamename = getIntent().getStringExtra("gamename");
        deleteGame(gamename);
        startActivity(intent);
    }
    private void deleteGame(String gamename) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cloud c = new Cloud();
                c.deleteGame(gamename);
            }
        }).start();
    }

}