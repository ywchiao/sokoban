package io.tut.sokoban;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnGameStart = (Button) findViewById(R.id.btn_game_start);

        btnGameStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GameLevelActivity.class);

                startActivity(intent);
            }
        });

        Button btnGameIntro = (Button) findViewById(R.id.btn_game_intro);

        btnGameIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GameIntroActivity.class);

                startActivity(intent);
            }
        });

        Button btnGameExit = (Button) findViewById(R.id.btn_game_exit);

        btnGameExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
