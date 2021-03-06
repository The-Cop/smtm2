package ru.thecop.smtm2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import ru.thecop.smtm2.R;
import ru.thecop.smtm2.SmtmApplication;
import ru.thecop.smtm2.activity.adapter.CategoryAdapter;
import ru.thecop.smtm2.db.DbHelper;
import ru.thecop.smtm2.model.Category;

import java.util.List;

public class CategoryActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<Category>>,
        CategoryAdapter.CategoryAdapterOnClickHandler {

    public enum Mode {
        NEW_SPENDING, EDIT_SPENDING, EDIT_NON_CONFIRMED_SPENDING, EDIT_KEYWORDS
    }

    public static final String TAG = "CategoryActivity";

    public static final String EXTRA_MODE = "Mode";
    //If editing an existing spending - put it's id in extra
    public static final String EXTRA_SPENDING_ID = "SpendingId";

    private static final int CATEGORIES_LOADER_ID = 0;

    private RecyclerView mRecyclerView;
    private CategoryAdapter mAdapter;
    private EditText mEditTextFilter;
    private Mode mode;

    private Long mEditedSpendingId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        initMode();
        initFilterEditText();
        initRecyclerAndAdapter();

        //if chosing category for existing spending (editing it) - save the id
        if (getIntent().hasExtra(EXTRA_SPENDING_ID)) {
            mEditedSpendingId = getIntent().getLongExtra(EXTRA_SPENDING_ID, -1);
        }

        //start the loader
        getSupportLoaderManager().initLoader(
                CATEGORIES_LOADER_ID,
                null,
                this
        );
    }

    private void initMode() {
        if (!getIntent().hasExtra(EXTRA_MODE)) {
            throw new IllegalStateException("No mode specified");
        }
        mode = (Mode) getIntent().getSerializableExtra(EXTRA_MODE);
        switch (mode) {
            case EDIT_SPENDING:
            case EDIT_NON_CONFIRMED_SPENDING:
                if (!getIntent().hasExtra(EXTRA_SPENDING_ID)) {
                    throw new IllegalStateException("No spending id specified");
                }
        }
    }

    private void initFilterEditText() {
        mEditTextFilter = (EditText) findViewById(R.id.editTextCategoriesFilter);
        mEditTextFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "filter onTextChanged, s='" + s.toString() + "'");
                getSupportLoaderManager().restartLoader(CATEGORIES_LOADER_ID, null, CategoryActivity.this);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initRecyclerAndAdapter() {
        //Bind adapter to recyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewCategories);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        //TODO custom divider thicker
        // https://stackoverflow.com/questions/24618829/how-to-add-dividers-and-spaces-between-items-in-recyclerview

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        //Bind adapter to recyclerView
        mAdapter = new CategoryAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
    }


    //TODO remove everywhere unnecessary onstart, ondestroy and other overrides
    @Override
    protected void onStart() {
        super.onStart();
//        getSupportLoaderManager().restartLoader(CATEGORIES_LOADER_ID, null, this);
        //restart loader if any changes to category list were made
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
                Log.d(TAG, "Reloading categories");
                String trimmedFilterText = mEditTextFilter.getText().toString().trim();
                if (trimmedFilterText.isEmpty()) {
                    return DbHelper.findAllCategories((SmtmApplication) getApplication());//FakeDb.findCategories();
                } else {
                    List<Category> categories = DbHelper.findCategoriesNameContains(trimmedFilterText, (SmtmApplication) getApplication());
                    if (!hasExactNameMatch(trimmedFilterText, categories)) {
                        //if a searched category does not exist - add it to list without id
                        //to be able to save it as new category if user selects it
                        Category filterStubCategory = new Category();
                        filterStubCategory.setName(trimmedFilterText);
                        categories.add(filterStubCategory);
                    }
                    return categories;
                }
            }

            private boolean hasExactNameMatch(String filterText, List<Category> categories) {
                if (categories.isEmpty()) {
                    return false;
                }
                String lowerCaseFilter = filterText.trim().toLowerCase();
                for (Category category : categories) {
                    if (category.getLowerCaseName().equals(lowerCaseFilter)) {
                        return true;
                    }
                }
                return false;
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
    public void onCategoryClick(Category category) {
        if (category.getId() == null) {
            //save new category
            DbHelper.create(category, (SmtmApplication) getApplication());
        }
        Intent intent = null;
        switch (mode) {
            case NEW_SPENDING:
                intent = new Intent(CategoryActivity.this, SpendingActivity.class);
                intent.putExtra(SpendingActivity.EXTRA_CATEGORY_ID, category.getId());
                break;
            case EDIT_SPENDING:
                intent = new Intent(CategoryActivity.this, SpendingActivity.class);
                intent.putExtra(SpendingActivity.EXTRA_SPENDING_ID, mEditedSpendingId);
                intent.putExtra(SpendingActivity.EXTRA_CATEGORY_ID, category.getId());
                break;
            case EDIT_NON_CONFIRMED_SPENDING:
                //TODO if spending is not confirmed, open an keyword adding activity for selected category
                intent = new Intent(CategoryActivity.this, SpendingActivity.class);
                intent.putExtra(SpendingActivity.EXTRA_SPENDING_ID, mEditedSpendingId);
                intent.putExtra(SpendingActivity.EXTRA_CATEGORY_ID, category.getId());
                break;
            case EDIT_KEYWORDS:
                intent = new Intent(CategoryActivity.this, WordListEditActivity.class);
                intent.putExtra(WordListEditActivity.EXTRA_MODE, WordListEditActivity.Mode.CATEGORY_KEYWORDS);
                intent.putExtra(WordListEditActivity.EXTRA_CATEGORY_ID, category.getId());
                intent.putExtra(WordListEditActivity.EXTRA_CATEGORY_NAME, category.getName());
                break;
        }
        startActivity(intent);
    }
}
