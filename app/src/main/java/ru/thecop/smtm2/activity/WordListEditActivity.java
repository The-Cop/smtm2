package ru.thecop.smtm2.activity;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import ru.thecop.smtm2.R;
import ru.thecop.smtm2.SmtmApplication;
import ru.thecop.smtm2.activity.adapter.WordsAdapter;
import ru.thecop.smtm2.db.DbHelper;
import ru.thecop.smtm2.model.CategoryKeyword;
import ru.thecop.smtm2.preferences.PreferenceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordListEditActivity extends AppCompatActivity
        implements WordsAdapter.WordDeleteOnClickHandler,
        LoaderManager.LoaderCallbacks<List<String>> {

    public static final String EXTRA_MODE = "Mode";
    public static final String EXTRA_CATEGORY_ID = "CategoryId";
    public static final String EXTRA_CATEGORY_NAME = "CategoryName";
    private static final String TAG = "WordListEditActivity";

    private static final int WORDS_LOADER_ID = 3;

    private RecyclerView mRecyclerView;
    private WordsAdapter mAdapter;
    private EditText mEditTextNewWord;
    private Long categoryId;
    private String categoryName;
    private Mode mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list_edit);
        initRecyclerAndAdapter();
        initMode();
        initTitle();
        mEditTextNewWord = (EditText) findViewById(R.id.editTextNewWord);

        //start the loader
        getSupportLoaderManager().initLoader(
                WORDS_LOADER_ID,
                null,
                this
        );
    }

    private void initTitle() {
        //set category name in action bar
        ActionBar actionBar = getSupportActionBar();
        switch (mode) {
            case CATEGORY_KEYWORDS:
                actionBar.setTitle(getString(R.string.words_edit_caption_category_keywords) + " " + categoryName);
                break;
            case SMS_GO_WORDS:
                actionBar.setTitle(getString(R.string.words_edit_caption_sms_go));
                break;
            case SMS_STOP_WORDS:
                actionBar.setTitle(getString(R.string.words_edit_caption_sms_stop));
                break;
        }
    }

    private void initMode() {
        if (getIntent().hasExtra(EXTRA_MODE)) {
            mode = (Mode) getIntent().getSerializableExtra(EXTRA_MODE);
        } else {
            throw new IllegalStateException("No mode specified");
        }

        if (mode == Mode.CATEGORY_KEYWORDS) {
            if (getIntent().hasExtra(EXTRA_CATEGORY_ID) && getIntent().hasExtra(EXTRA_CATEGORY_NAME)) {
                categoryId = getIntent().getLongExtra(EXTRA_CATEGORY_ID, -1);
                categoryName = getIntent().getStringExtra(EXTRA_CATEGORY_NAME);
            } else {
                throw new IllegalStateException("No category id or name specified for category keywords mode");
            }
        }
    }

    private void initRecyclerAndAdapter() {
        //Bind adapter to recyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewWords);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        //Bind adapter to recyclerView
        mAdapter = new WordsAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onWordDeleteClick(String word) {
        Log.d(TAG, "Delete click on word '" + word + "'");
    }

    @Override
    public Loader<List<String>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<String>>(this) {

            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public List<String> loadInBackground() {
                switch (mode) {
                    case CATEGORY_KEYWORDS:
                        List<CategoryKeyword> categoryKeywords =
                                DbHelper.findCategoryKeywords((SmtmApplication) getApplication(), categoryId);
                        List<String> result = new ArrayList<>(categoryKeywords.size());
                        for (CategoryKeyword categoryKeyword : categoryKeywords) {
                            result.add(categoryKeyword.getKeyword());
                        }
                        return result;
                    case SMS_GO_WORDS:
                        return new ArrayList<>(PreferenceUtils.getSmsParseGoWords(WordListEditActivity.this));
                    case SMS_STOP_WORDS:
                        return new ArrayList<>(PreferenceUtils.getSmsParseStopWords(WordListEditActivity.this));
                    default:
                        throw new IllegalStateException("Unknown mode");
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
        Collections.sort(data);
        mAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<String>> loader) {
    }

    public enum Mode {
        CATEGORY_KEYWORDS, SMS_GO_WORDS, SMS_STOP_WORDS
    }
}
