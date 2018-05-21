package com.example.eakgun14.journeytracker.Activities;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.example.eakgun14.journeytracker.Adapters.JournableAdapter;
import com.example.eakgun14.journeytracker.DataTypes.Journal;
import com.example.eakgun14.journeytracker.Adapters.ViewAdapterListener;
import com.example.eakgun14.journeytracker.Dialogs.CreateJournalDialogFragment;
import com.example.eakgun14.journeytracker.Dialogs.NoticeDialogListener;
import com.example.eakgun14.journeytracker.LocalDatabase.AppDatabase;
import com.example.eakgun14.journeytracker.R;

public class JournalsActivity extends AppCompatActivity implements ViewAdapterListener<Journal>,
        NoticeDialogListener {

    AppDatabase db;

    private JournableAdapter<Journal> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journals);

        android.support.v7.widget.Toolbar bar = findViewById(R.id.toolbar);
        setSupportActionBar(bar);
        ActionBar actBar = getSupportActionBar();
        assert actBar != null;
        actBar.setDisplayHomeAsUpEnabled(true);
        actBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actBar.setTitle("All Journals");

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .allowMainThreadQueries()//.fallbackToDestructiveMigration()
                .build();

        List<Journal> journals = db.journalDao().getAllJournals();

        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutMgr = new LinearLayoutManager(this);
        adapter = new JournableAdapter<>(journals, this);
        recyclerView.setLayoutManager(layoutMgr);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addJournalButton = findViewById(R.id.add_journal_button);
        addJournalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                DialogFragment frag =  new CreateJournalDialogFragment();

                frag.show(fm, "fragment_create_journal");
            }
        });

        TextView allJourneyField = findViewById(R.id.all_journals_text);
        allJourneyField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAllJourniesActivity();
            }
        });
    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
        super.onBackPressed();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete:
                adapter.removeSelected();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Database is updated only when the activity stops
        updateJournalDatabase();
    }

    public void updateJournalDatabase() {
        List<Journal> toAdd = adapter.getJournablesToAdd();
        Log.d("db", "Add: " + toAdd);
        db.journalDao().insertAll(toAdd);

        List<Journal> toDelete = adapter.getJournablesToDelete();
        Log.d("db", "Delete: " + toDelete.toString());
        db.journalDao().deleteAll(toDelete);
    }

    // Start JourniesActivity with a special intent, in order to display every single journey
    public void startAllJourniesActivity() {
        Intent intent = new Intent(JournalsActivity.this, JourniesActivity.class);
        intent.putExtra("Special", -1);
        startActivity(intent);
    }

    @Override
    public void onViewItemClicked(Journal j) {
        // ViewAdapterListener callback
        // Start a JourniesActivity to display the contents of the selcted journal.
        Integer journalID = j.getId();
        String journalName = j.getName();
        Intent intent = new Intent(JournalsActivity.this, JourniesActivity.class);
        intent.putExtra("Journal", journalID);
        intent.putExtra("Name", journalName);
        startActivity(intent);
    }

    @Override
    public void onDialogClick(DialogFragment dialog) {
        // NoticeDialogListener callback
        // Create a new journal with the specified details
        // For now it exists only in the JournableAdapter instance,
        // it will be stored in the db when this activity stops.
        try {
            CreateJournalDialogFragment dial = (CreateJournalDialogFragment) dialog;

            String name = dial.getNameText().getText().toString();

            adapter.add(adapter.getItemCount(), new Journal(name, ""));
        }
        catch (ClassCastException e) {
            throw new ClassCastException(dialog.toString()
                    + " must extend CreateJournalDialogFragment");
        }
    }
}
