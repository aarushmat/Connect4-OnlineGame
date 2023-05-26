package edu.msu.moranti1.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class JoinGameActivity extends AppCompatActivity {
    TextView textView;
    Cloud.CatalogAdapter adapter;
    Context context;
    String user,pass;
    EditText edit;

    private FirebaseService FCMService = new FirebaseService();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        textView = findViewById(R.id.textv);
        edit = findViewById(R.id.edit);

        ListView list = findViewById(R.id.list);
        context = this;
        //todo
        SharedPreferences shared = getSharedPreferences(getString(R.string.mypref), MODE_PRIVATE);
        user = shared.getString(getString(R.string.saveuser), "");
        pass = shared.getString(getString(R.string.savepass), "");
        adapter = new Cloud.CatalogAdapter(context, list,user,pass);
        list.setAdapter(adapter);


        SharedPreferences preferences = getSharedPreferences(getString(R.string.mypref), MODE_PRIVATE);
        FCMService.setUser(preferences.getString(getString(R.string.currUser), ""));
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = "Token: "+token;
                        Log.d("FCM", msg);
//                        Toast.makeText(JoinGameActivity.this, msg, Toast.LENGTH_SHORT).show();
                        FCMService.onNewToken(token);
                    }
                });
    }
    public void onbtnClick(View v){
        if(TextUtils.isEmpty(edit.getText().toString()))
        {
            Toast.makeText(context, R.string.entername, Toast.LENGTH_SHORT).show();
        }
        else {
            Intent i = new Intent(this, GameActivity.class);
            i.putExtra("name",edit.getText().toString());
            i.putExtra("hostid", user);
            savingGameState(edit.getText().toString(), user, "0", "");
            startActivity(i);
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
    public void listsize(Context context,int size){
        textView = ((Activity)context).findViewById(R.id.textv);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (size == 0){
                    textView.setText(R.string.no_avail_games);
                }
                else{
                    textView.setText(R.string.avail_games);
                }

            }
        });

    }
}
