package com.example.eakgun14.journeytracker.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.example.eakgun14.journeytracker.Adapters.JournalAdapter;
import com.example.eakgun14.journeytracker.DataTypes.Journal;
import com.example.eakgun14.journeytracker.DataTypes.Journey;
import com.example.eakgun14.journeytracker.R;

public class JournalsActivity extends AppCompatActivity {

    private List<Journal> journals;
    private Journal allJourneys;
    private Journal defaultJourneys;

    private RecyclerView recyclerView;
    private JournalAdapter adapter;
    private RecyclerView.LayoutManager layoutMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journals);

        android.support.v7.widget.Toolbar bar = findViewById(R.id.journal_toolbar);
        setSupportActionBar(bar);
        ActionBar actBar = getSupportActionBar();
        actBar.setDisplayHomeAsUpEnabled(true);

        journals = new ArrayList<Journal>();
        allJourneys = new Journal("All Journeys");
        defaultJourneys = new Journal("Default Journeys");

        for (int i = 0; i < 3; i++)
            allJourneys.addJourney(new Journey("Journey no: " + i, "dummy desc"));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        layoutMgr = new LinearLayoutManager(this);
        adapter = new JournalAdapter(journals, this.getApplicationContext());
        recyclerView.setLayoutManager(layoutMgr);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addJourneyButton = findViewById(R.id.add_journal_button);
        addJourneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.add(adapter.getItemCount(), new Journal("saqurula"));
            }
        });

        TextView allJourneyField = findViewById(R.id.all_journals_text);
        allJourneyField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startJourniesActivity(allJourneys);
            }
        });

        TextView defaultJourneyField = findViewById(R.id.default_journals_text);
        defaultJourneyField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startJourniesActivity(defaultJourneys);
            }
        });

        ImageButton deleteButton = findViewById(R.id.journal_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.removeSelected();
            }
        });
    }

    public void startJourniesActivity(Journal toInspect) {
        Intent intent = new Intent(JournalsActivity.this, JourniesActivity.class);
        intent.putExtra("Journal", toInspect);
        startActivity(intent);
    }
}
