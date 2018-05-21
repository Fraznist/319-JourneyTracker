package com.example.eakgun14.journeytracker.Activities;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eakgun14.journeytracker.Adapters.JournableAdapter;
import com.example.eakgun14.journeytracker.DataTypes.Journey;
import com.example.eakgun14.journeytracker.Adapters.ViewAdapterListener;
import com.example.eakgun14.journeytracker.Dialogs.NoticeDialogListener2;
import com.example.eakgun14.journeytracker.Dialogs.ViewJourneyDialogFragment;
import com.example.eakgun14.journeytracker.LocalDatabase.AppDatabase;
import com.example.eakgun14.journeytracker.R;

import java.util.List;

public class JourniesActivity extends AppCompatActivity implements ViewAdapterListener<Journey>,
        NoticeDialogListener2 {

    AppDatabase db;

    List<Journey> journies;
    String journalName;

    private JournableAdapter<Journey> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journies);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .allowMainThreadQueries()
                .build();

        Intent intent = getIntent();

        // Special case requires to display all journies, regardless of journal
        if (intent.getIntExtra("Special", 0) == -1) {
            journalName = "All Journeys";
            journies = db.journeyDao().getAllJourneys();
        }
        else {
            int journalID = intent.getIntExtra("Journal", 0);
            journalName = intent.getStringExtra("Name");
            journies = db.journeyDao().getAllJourneysInJournal(journalID);
        }

        android.support.v7.widget.Toolbar bar = findViewById(R.id.toolbar);
        setSupportActionBar(bar);
        ActionBar actBar = getSupportActionBar();
        assert actBar != null;
        actBar.setDisplayHomeAsUpEnabled(true);
        actBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actBar.setTitle(journalName);

        RecyclerView recyclerView = findViewById(R.id.journies_recycler_view);
        recyclerView.setHasFixedSize(true);

        adapter = new JournableAdapter<>(journies, this);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutMgr = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutMgr);

        ImageButton viewButton = findViewById(R.id.journey_view_button);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Journey> selected = adapter.getSelectedJournables();
                startViewJournesActivity(extractRouteArray(selected));
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
        getMenuInflater().inflate(R.menu.move_menu, menu);
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
                return true;
            case R.id.action_delete:
                adapter.removeSelected();
                return true;
            case R.id.action_move:
                Toast.makeText(this, "MOVE", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Store changes to database only when the activity stops.
        updateJourniesDatabase();
    }

    @Override
    public void onViewItemClicked(Journey j) {
        // Set up a dialog box to show a specific journeys details.
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment frag =  new ViewJourneyDialogFragment();

        // The dialog fragment needs to display the name and the description
        // It also stores the route of the journey since the route will be viewed
        // by pressing a button from the dialog fragment
        Bundle args = new Bundle();
        args.putString("name", j.getName());
        args.putString("description", j.getDescription());
        args.putString("route", j.getRoute());
        args.putString("photos", j.getCoordinate_photos());
        frag.setArguments(args);

        frag.show(fm, "fragment_view_journey_info");
    }

    public void updateJourniesDatabase() {
        List<Journey> toAdd = adapter.getJournablesToAdd();
        Log.d("db", toAdd.toString());
        db.journeyDao().insertAll(toAdd);

        List<Journey> toDelete = adapter.getJournablesToDelete();
        Log.d("db", toDelete.toString());
        db.journeyDao().deleteAll(toDelete);
    }

    private void startViewJournesActivity(String ...routes) {
        // routes is an array of JSON objects
        // each JSON object represents a list of coordiantes when deserialized
        Intent intent = new Intent(JourniesActivity.this, ViewJourniesActivity.class);
        intent.putExtra("routes", routes);

        startActivity(intent);
    }

    private String[] extractRouteArray(List<Journey> journies) {
        // Only need the list of coordinates that represent a route,
        // Can't pass Journey object via intents anyways, they aren't parcelable
        String[] routes = new String[journies.size()];

        for (int i = 0; i < routes.length; i++)
            routes[i] = journies.get(i).getRoute();

        return routes;
    }

    @Override
    public void onDialogClick(DialogFragment dialog) {
        // NoticeDialogListener callback,
        // display the route that is stored in the dialogFragment
        startViewJournesActivity( ((ViewJourneyDialogFragment) dialog).getRoute() );
    }

    @Override
    public void onSecondaryDialogClick(DialogFragment dialog) {
        ViewJourneyDialogFragment dial = (ViewJourneyDialogFragment) dialog;
        Intent intent = new Intent(JourniesActivity.this, PhotoViewActivity.class);
        intent.putExtra("Journey Name", dial.getName());
        intent.putExtra("URI JSON", dial.getPhotos());
        startActivity(intent);
    }
}
