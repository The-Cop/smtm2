package ru.thecop.smtm2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import ru.thecop.smtm2.R;
import ru.thecop.smtm2.SmtmApplication;
import ru.thecop.smtm2.activity.adapter.NonConfirmedAdapter;
import ru.thecop.smtm2.db.DbHelper;
import ru.thecop.smtm2.model.Spending;

import java.util.List;

//todo refactor sample android:text to tools:text in layouts
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Spending>>{

    private static final int NON_CONFIRMED_SPENDINGS_LOADER_ID = 1;
    public static final String TAG = "MainActivity";

    private RecyclerView mRecyclerView;
    private NonConfirmedAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Click listener for FAB button
        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an category choose activity
                Intent selectCategoryIntent = new Intent(MainActivity.this, CategoryActivity.class);
                startActivity(selectCategoryIntent);
            }
        });

        //Bind adapter to recyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewNonConfirmed);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        //TODO swipe to delete
        //Bind adapter to recyclerView
        mAdapter = new NonConfirmedAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        //start the loader
        getSupportLoaderManager().initLoader(
                NON_CONFIRMED_SPENDINGS_LOADER_ID,
                null,
                this
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(NON_CONFIRMED_SPENDINGS_LOADER_ID, null, this);
    }

    @Override
    public Loader<List<Spending>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<Spending>>(this) {

            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public List<Spending> loadInBackground() {
                return DbHelper.findAllNonConfirmedSpendings((SmtmApplication) getApplication());//FakeDb.findSpendingsNonConfirmed();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Spending>> loader, List<Spending> data) {
        mAdapter.setData(data);
        if (data == null) {
            Log.e(TAG, "Failed to retrieve non-confirmed spendings");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Spending>> loader) {

    }

    public void showStatsActivitySpendings(View view) {
        Intent statsActiviryIntent = new Intent(MainActivity.this, StatsActivity.class);
        startActivity(statsActiviryIntent);
    }
}
