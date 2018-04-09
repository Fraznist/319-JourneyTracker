package com.example.eakgun14.journeytracker;

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

        Intent intent = getIntent();

        Button manageJourneysButton = (Button) findViewById(R.id.manage_journey);
        manageJourneysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, JournalsActivity.class);
                startActivity(intent);
            }
        });

        Button startJourneyButton = (Button) findViewById(R.id.start_journey);
        startJourneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StartJourneyActivity.class);
                startActivity(intent);
            }
        });

        Button viewJourneysButton = (Button) findViewById(R.id.view_journeys);
        viewJourneysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ViewJourneysActivity.class);
                startActivity(intent);
            }
        });
    }
}
