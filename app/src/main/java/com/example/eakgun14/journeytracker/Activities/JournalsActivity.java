package com.example.eakgun14.journeytracker.Activities;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
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
import com.example.eakgun14.journeytracker.LocalDatabase.AppDatabase;
import com.example.eakgun14.journeytracker.R;

public class JournalsActivity extends AppCompatActivity {

    AppDatabase db;

    private List<Journal> journals;

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

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .allowMainThreadQueries()
                .build();

        journals = db.journalDao().getAllJournals();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutMgr = new LinearLayoutManager(this);
        adapter = new JournalAdapter(journals, this, db);
        recyclerView.setLayoutManager(layoutMgr);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addJournalButton = findViewById(R.id.add_journal_button);
        addJournalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.add(adapter.getItemCount(), new Journal("saqurula"));
            }
        });

        TextView allJourneyField = findViewById(R.id.all_journals_text);
        allJourneyField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAllJourniesActivity();
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

    public void startJourniesActivity(Journal j) {
        int journalID = j.getId();
        String journalName = j.getName();
        Intent intent = new Intent(JournalsActivity.this, JourniesActivity.class);
        intent.putExtra("Journal", journalID);
        intent.putExtra("Name", journalName);
        startActivity(intent);
    }

    public void startAllJourniesActivity() {
        Intent intent = new Intent(JournalsActivity.this, JourniesActivity.class);
        intent.putExtra("Special", -1);
        startActivity(intent);
    }
}
