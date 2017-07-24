package ru.thecop.smtm2;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import ru.thecop.smtm2.db.FakeDb;
import ru.thecop.smtm2.model.Category;
import ru.thecop.smtm2.model.Spending;
import ru.thecop.smtm2.model.dto.SpendingDto;

import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<SpendingDto>> {

    //todo use intent extra to define view mode: spendings/categories
    //todo spinner progressbars everywhere where loaders present
    private static final int STATS_LOADER_ID = 2;//todo - later - ensure uniqueness of loader ids
    public static final String TAG = "StatsActivity";

    private RecyclerView mRecyclerView;
    private StatsSpendingAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        //Bind adapter to recyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        //TODO swipe to delete?
        //Bind adapter to recyclerView
        mAdapter = new StatsSpendingAdapter(this, null);//todo click listener
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(STATS_LOADER_ID, null, this);
    }

    @Override
    public Loader<List<SpendingDto>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<SpendingDto>>(this) {

            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public List<SpendingDto> loadInBackground() {
                //todo gather and show totals
                List<Spending> spendings = FakeDb.findAllSpendings();//findSpendingsConfirmed();//todo only confirmed spendings
                List<SpendingDto> dtos = new ArrayList<>(spendings.size());
                for (Spending spending : spendings) {
                    Category category = FakeDb.findCategoryById(spending.getCategoryId());
                    dtos.add(new SpendingDto(category, spending));
                }
                return dtos;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<SpendingDto>> loader, List<SpendingDto> data) {
        mAdapter.setData(data);
        if (data == null) {
            Log.e(TAG, "Failed to retrieve confirmed spendings");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<SpendingDto>> loader) {

    }
}
