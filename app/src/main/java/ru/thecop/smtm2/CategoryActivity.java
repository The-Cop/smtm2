package ru.thecop.smtm2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import ru.thecop.smtm2.db.FakeDb;
import ru.thecop.smtm2.model.Category;

import java.util.List;

public class CategoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Category>>, CategoryAdapter.CategoryAdapterOnClickHandler {

    public static final String TAG = "CategoryActivity";

    private static final int CATEGORIES_LOADER_ID = 0;

    private RecyclerView mRecyclerView;
    private CategoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        //Bind adapter to recyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewCategories);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        //add item divider

        //TODO custom divider thicker
        // https://stackoverflow.com/questions/24618829/how-to-add-dividers-and-spaces-between-items-in-recyclerview

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        //Bind adapter to recyclerView
        mAdapter = new CategoryAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
        //todo implement filtering categories

        //start the loader
        getSupportLoaderManager().initLoader(
                CATEGORIES_LOADER_ID,
                null,
                this
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        //restart loader if any changes to category list were made
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(CATEGORIES_LOADER_ID, null, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Loader<List<Category>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<Category>>(this) {

            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public List<Category> loadInBackground() {
                return FakeDb.findCategories();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Category>> loader, List<Category> data) {
        mAdapter.setData(data);
        if (data == null) {
            Log.e(TAG, "Failed to retrieve categories");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Category>> loader) {
    }

    @Override
    public void onCategoryClick(long categoryId) {
        Intent intent = new Intent(CategoryActivity.this, SpendingActivity.class);
        intent.putExtra(SpendingActivity.EXTRA_CATEGORY_ID,categoryId);
        startActivity(intent);
    }
}
