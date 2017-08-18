package ru.thecop.smtm2.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import ru.thecop.smtm2.R;
import ru.thecop.smtm2.SmtmApplication;
import ru.thecop.smtm2.activity.adapter.StatsCategoryAdapter;
import ru.thecop.smtm2.activity.adapter.StatsCategoryInfo;
import ru.thecop.smtm2.activity.adapter.StatsSpendingAdapter;
import ru.thecop.smtm2.db.CategoryStatsRequestResult;
import ru.thecop.smtm2.db.DbHelper;
import ru.thecop.smtm2.dialog.DatePickerDialogFragment;
import ru.thecop.smtm2.model.Spending;
import ru.thecop.smtm2.util.AmountFormatter;
import ru.thecop.smtm2.util.Constants;
import ru.thecop.smtm2.util.DateTimeConverter;

import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<StatsActivity.StatsLoaderResult> {

    public static final String TAG = "StatsActivity";
    //todo use intent extra to define view mode: spendings/categories
    public static final String EXTRA_DISPLAY_MODE_CATEGORIES = "DisplayModeCategories";
    //todo spinner progressbars everywhere where loaders present
    private static final int STATS_LOADER_ID = 2;//todo - later - ensure uniqueness of loader ids
    private static final String FROM_DATE_PIRCKER_DIALOG_TAG = "FromDatePickerDialogTag";
    private static final String TO_DATE_PIRCKER_DIALOG_TAG = "ToDatePickerDialogTag";

    private RecyclerView mRecyclerView;
    private StatsSpendingAdapter mSpendingsAdapter;
    private StatsCategoryAdapter mCategoriesAdapter;

    private TextView mTextViewDateFrom;
    private TextView mTextViewDateTo;

    private LocalDate mFromDate;
    private LocalDate mToDate;

    private TextView mTextViewTotal;
    private TextView mTextViewEntries;
    private TextView mTextViewEntriesPerDay;
    private TextView mTextViewPeriod;
    private TextView mTextViewPerDay;
    private TextView mTextViewPerWeek;
    private TextView mTextViewPerMonth;
    private TextView mTextViewPerYear;

    private boolean mDisplayModeCategories = false;

    private DatePickerDialog.OnDateSetListener onFromDateSetListener;
    private DatePickerDialog.OnDateSetListener onToDateSetListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        //set proper display mode flag
        if (getIntent().hasExtra(EXTRA_DISPLAY_MODE_CATEGORIES)) {
            mDisplayModeCategories = getIntent().getBooleanExtra(EXTRA_DISPLAY_MODE_CATEGORIES, false);
        }

        //Deal with recyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        //TODO swipe to delete in spendings mode?

        //Bind proper adapter to recyclerView
        if (mDisplayModeCategories) {
            mCategoriesAdapter = new StatsCategoryAdapter(this, null);//todo click listener
            mRecyclerView.setAdapter(mCategoriesAdapter);
        } else {
            mSpendingsAdapter = new StatsSpendingAdapter(this, null);//todo click listener
            mRecyclerView.setAdapter(mSpendingsAdapter);
        }

        //date textViews
        mTextViewDateFrom = (TextView) findViewById(R.id.textViewDateFrom);
        mTextViewDateTo = (TextView) findViewById(R.id.textViewDateTo);

        //totals textviews
        mTextViewTotal = (TextView) findViewById(R.id.textViewTotal);
        mTextViewEntries = (TextView) findViewById(R.id.textViewEntries);
        mTextViewEntriesPerDay = (TextView) findViewById(R.id.textViewEntriesPerDay);
        mTextViewPeriod = (TextView) findViewById(R.id.textViewPeriod);
        mTextViewPerDay = (TextView) findViewById(R.id.textViewPerDay);
        mTextViewPerWeek = (TextView) findViewById(R.id.textViewPerWeek);
        mTextViewPerMonth = (TextView) findViewById(R.id.textViewPerMonth);
        mTextViewPerYear = (TextView) findViewById(R.id.textViewPerYear);

        //set totals values to zeroes
        updateTotalsTextViews(new Totals());

        //set default dates to today
        LocalDate initialDate = LocalDate.now();
        updateFromDate(initialDate);
        updateToDate(initialDate);

        //set date dialogs listeners
        onFromDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                updateFromDate(new LocalDate(year, month + 1, dayOfMonth));
                getSupportLoaderManager().restartLoader(STATS_LOADER_ID, null, StatsActivity.this);
            }
        };
        onToDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                updateToDate(new LocalDate(year, month + 1, dayOfMonth));
                getSupportLoaderManager().restartLoader(STATS_LOADER_ID, null, StatsActivity.this);
            }
        };


        //start the loader
        getSupportLoaderManager().initLoader(
                STATS_LOADER_ID,
                null,
                this
        );
    }

    private void updateTotalsTextViews(Totals totals) {
        mTextViewTotal.setText(AmountFormatter.format(totals.getSum()));
        mTextViewEntries.setText(Integer.toString(totals.getEntriesCount()));
        mTextViewEntriesPerDay.setText(AmountFormatter.format(totals.getEntriesPerDay()));
        mTextViewPeriod.setText(Integer.toString(totals.getPeriodDays()));

        mTextViewPerDay.setText(AmountFormatter.format(totals.getPerDay()));
        mTextViewPerWeek.setText(AmountFormatter.format(totals.getPerWeek()));
        mTextViewPerMonth.setText(AmountFormatter.format(totals.getPerMonth()));
        mTextViewPerYear.setText(AmountFormatter.format(totals.getPerYear()));
    }

    private void updateFromDate(LocalDate newFromDate) {
        if (newFromDate != null && mToDate != null && newFromDate.isAfter(mToDate)) {
            Toast.makeText(StatsActivity.this, getString(R.string.from_date_after_to_date), Toast.LENGTH_SHORT).show();
            return;
        }
        this.mFromDate = newFromDate;
        String dateString = newFromDate.toString(Constants.DATE_DISPLAY_FORMAT_PATTERN);
        mTextViewDateFrom.setText(dateString);
    }

    private void updateToDate(LocalDate newToDate) {
        if (newToDate != null && mFromDate != null && newToDate.isBefore(mFromDate)) {
            Toast.makeText(StatsActivity.this, getString(R.string.from_date_after_to_date), Toast.LENGTH_SHORT).show();
            return;
        }
        this.mToDate = newToDate;
        String dateString = newToDate.toString(Constants.DATE_DISPLAY_FORMAT_PATTERN);
        mTextViewDateTo.setText(dateString);
    }

    private int getPeriodBetweenDates() {
        if (mFromDate == null || mToDate == null) {
            return 0;
        }
        return Days.daysBetween(mFromDate, mToDate).getDays() + 1;
    }

    @Override
    public Loader<StatsLoaderResult> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<StatsLoaderResult>(this) {

            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public StatsLoaderResult loadInBackground() {
                //todo load spendings by specified dates
                if (mDisplayModeCategories) {
                    long timestampFrom = DateTimeConverter.convert(DateTimeConverter.atStartOfDay(mFromDate));
                    long timestampTo = DateTimeConverter.convert(DateTimeConverter.atEndOfDay(mToDate));
                    //load stats
                    List<CategoryStatsRequestResult> categories = DbHelper.loadCategoriesStats(
                            (SmtmApplication) getApplication(), timestampFrom, timestampTo);
                    StatsLoaderResult statsLoaderResult = new StatsLoaderResult();

                    //convert to info dtos
                    int periodBetweenDates = getPeriodBetweenDates();
                    List<StatsCategoryInfo> statsCategoryInfos = new ArrayList<>(categories.size());
                    for (CategoryStatsRequestResult category : categories) {
                        statsCategoryInfos.add(new StatsCategoryInfo(category, periodBetweenDates));
                    }
                    statsLoaderResult.categoryAdapterData = new StatsCategoryAdapter.StatsCategoryAdapterData(statsCategoryInfos);
                    statsLoaderResult.totals = new Totals(getPeriodBetweenDates(), categories);
                    return statsLoaderResult;
                } else {
                    List<Spending> spendings = DbHelper.findAllConfirmedSpendings((SmtmApplication) getApplication());
                    StatsLoaderResult statsLoaderResult = new StatsLoaderResult();
                    statsLoaderResult.spendings = spendings;
                    statsLoaderResult.totals = new Totals(spendings, getPeriodBetweenDates());
                    return statsLoaderResult;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<StatsLoaderResult> loader, StatsLoaderResult data) {
        if (data == null) {
            Log.e(TAG, "Failed to retrieve stats");
            return;
        }

        if (mDisplayModeCategories) {
            mCategoriesAdapter.setData(data.categoryAdapterData);
        } else {
            mSpendingsAdapter.setData(data.spendings);
        }
        updateTotalsTextViews(data.totals);
    }

    @Override
    public void onLoaderReset(Loader<StatsLoaderResult> loader) {

    }

    public void showFromDatePickerDialog(View view) {
        DatePickerDialogFragment datePickerDialogFragment = DatePickerDialogFragment.newInstance(mFromDate, onFromDateSetListener);
        datePickerDialogFragment.show(getSupportFragmentManager(), FROM_DATE_PIRCKER_DIALOG_TAG);
    }

    public void showToDatePickerDialog(View view) {
        DatePickerDialogFragment datePickerDialogFragment = DatePickerDialogFragment.newInstance(mToDate, onToDateSetListener);
        datePickerDialogFragment.show(getSupportFragmentManager(), TO_DATE_PIRCKER_DIALOG_TAG);
    }

    static class StatsLoaderResult {
        private List<Spending> spendings;
        private StatsCategoryAdapter.StatsCategoryAdapterData categoryAdapterData;
        private Totals totals;
    }


    private static class Totals {
        private int entriesCount;
        private int periodDays;
        private double sum;

        public Totals() {
        }

        public Totals(List<Spending> spendings, int periodDays) {
            this.periodDays = periodDays;
            this.entriesCount = spendings.size();
            sum = 0d;
            for (Spending spending : spendings) {
                sum += spending.getAmount();
            }
        }

        public Totals(int periodBetweenDates, List<CategoryStatsRequestResult> categories) {
            this.periodDays = periodBetweenDates;
            for (CategoryStatsRequestResult category : categories) {
                this.entriesCount += category.getEntriesCount();
                this.sum += category.getTotalAmount();
            }
        }

        public int getEntriesCount() {
            return entriesCount;
        }

        public int getPeriodDays() {
            return periodDays;
        }

        public double getSum() {
            return sum;
        }

        public double getEntriesPerDay() {
            if (entriesCount == 0 || periodDays == 0) {
                return 0;
            }
            return ((double) entriesCount) / periodDays;
        }

        public double getPerDay() {
            return sum / periodDays;
        }

        public double getPerWeek() {
            return getPerDay() * 7;
        }

        public double getPerMonth() {
            return getPerDay() * 30;
        }

        public double getPerYear() {
            return getPerDay() * 365;
        }
    }
}
