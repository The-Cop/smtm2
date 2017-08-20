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
import ru.thecop.smtm2.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<StatsActivity.StatsLoaderResult> {

    public static final String TAG = "StatsActivity";
    //todo use intent extra to define view mode: spendings/categories
    public static final String EXTRA_DISPLAY_MODE_CATEGORIES = "DisplayModeCategories";
    public static final String LOADER_ARGUMENT_LOAD_ALL = "LoadAllSpendings";
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

    private SmtmApplication smtmApplication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        smtmApplication = (SmtmApplication) getApplication();

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
        if (newFromDate == null) {
            this.mFromDate = newFromDate;
            return;
        }
        if (newFromDate != null && mToDate != null && newFromDate.isAfter(mToDate)) {
            Toast.makeText(StatsActivity.this, getString(R.string.from_date_after_to_date), Toast.LENGTH_SHORT).show();
            return;
        }
        this.mFromDate = newFromDate;
        String dateString = newFromDate.toString(Constants.DATE_DISPLAY_FORMAT_PATTERN);
        mTextViewDateFrom.setText(dateString);
    }

    private void updateToDate(LocalDate newToDate) {
        if (newToDate == null) {
            this.mToDate = newToDate;
            return;
        }
        if (newToDate != null && mFromDate != null && newToDate.isBefore(mFromDate)) {
            Toast.makeText(StatsActivity.this, getString(R.string.from_date_after_to_date), Toast.LENGTH_SHORT).show();
            return;
        }
        this.mToDate = newToDate;
        String dateString = newToDate.toString(Constants.DATE_DISPLAY_FORMAT_PATTERN);
        mTextViewDateTo.setText(dateString);
    }

    private void updateBothDates(LocalDate newFromDate, LocalDate newToDate) {
        if (newFromDate == null && newToDate == null) {
            mToDate = newToDate;
            mFromDate = newFromDate;
            return;
        }

        if (newFromDate == null || newToDate == null) {
            throw new IllegalArgumentException("Both dates must be null or non-null");
        }
        if (newToDate.isBefore(newFromDate)) {
            throw new IllegalArgumentException("to date can not be before from date");
        }
        mToDate = newToDate;
        mFromDate = newFromDate;

        mTextViewDateTo.setText(newToDate.toString(Constants.DATE_DISPLAY_FORMAT_PATTERN));
        mTextViewDateFrom.setText(newFromDate.toString(Constants.DATE_DISPLAY_FORMAT_PATTERN));

    }

    @Override
    public Loader<StatsLoaderResult> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<StatsLoaderResult>(this) {

            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public StatsLoaderResult loadInBackground() {
                if (mDisplayModeCategories) {
                    long timestampFrom;
                    long timestampTo;
                    //categories display mode
                    if (args != null && args.getBoolean(LOADER_ARGUMENT_LOAD_ALL, false)) {
                        //load all
                        Spending earliest = DbHelper.findEarliestSpending(smtmApplication);
                        Spending latest = DbHelper.findLatestSpending(smtmApplication);
                        timestampFrom = earliest != null ? earliest.getTimestamp() : DateTimeUtils.convert(DateTimeUtils.atStartOfDay(LocalDate.now()));
                        timestampTo = latest != null ? latest.getTimestamp() : DateTimeUtils.convert(DateTimeUtils.atEndOfDay(LocalDate.now()));

                    } else {
                        timestampFrom = DateTimeUtils.convert(DateTimeUtils.atStartOfDay(mFromDate));
                        timestampTo = DateTimeUtils.convert(DateTimeUtils.atEndOfDay(mToDate));
                    }
                    //load stats
                    List<CategoryStatsRequestResult> categories = DbHelper.loadCategoriesStats(smtmApplication, timestampFrom, timestampTo);
                    StatsLoaderResult statsLoaderResult = new StatsLoaderResult();
                    statsLoaderResult.dateFrom = DateTimeUtils.convert(timestampFrom).toLocalDate();
                    statsLoaderResult.dateTo = DateTimeUtils.convert(timestampTo).toLocalDate();

                    //convert to info dtos
                    int periodBetweenDates = DateTimeUtils.getPeriodBetweenDates(timestampFrom, timestampTo);
                    List<StatsCategoryInfo> statsCategoryInfos = new ArrayList<>(categories.size());
                    for (CategoryStatsRequestResult category : categories) {
                        statsCategoryInfos.add(new StatsCategoryInfo(category, periodBetweenDates));
                    }
                    statsLoaderResult.categoryAdapterData = new StatsCategoryAdapter.StatsCategoryAdapterData(statsCategoryInfos);
                    statsLoaderResult.totals = new Totals(periodBetweenDates, categories);
                    return statsLoaderResult;
                } else {
                    //spendings display mode
                    List<Spending> spendings;
                    StatsLoaderResult statsLoaderResult = new StatsLoaderResult();
                    if (args != null && args.getBoolean(LOADER_ARGUMENT_LOAD_ALL, false)) {
                        //load all sendings
                        spendings = DbHelper.findAllConfirmedSpendings(smtmApplication);

                        if (!spendings.isEmpty()) {
                            statsLoaderResult.dateFrom = DateTimeUtils.convert(spendings.get(spendings.size() - 1).getTimestamp()).toLocalDate();
                            statsLoaderResult.dateTo = DateTimeUtils.convert(spendings.get(0).getTimestamp()).toLocalDate();
                        }

                    } else {
                        //load spendings by specified dates
                        spendings = DbHelper.findConfirmedSpendings(smtmApplication,
                                DateTimeUtils.convert(DateTimeUtils.atStartOfDay(mFromDate)),
                                DateTimeUtils.convert(DateTimeUtils.atEndOfDay(mToDate)));
                        statsLoaderResult.dateFrom = mFromDate;
                        statsLoaderResult.dateTo = mToDate;
                    }
                    statsLoaderResult.spendings = spendings;
                    statsLoaderResult.totals = new Totals(spendings, DateTimeUtils.getPeriodBetweenDates(statsLoaderResult.dateFrom, statsLoaderResult.dateTo));
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
        //if loading all spendings
        if (mFromDate == null && mToDate == null) {
            updateFromDate(data.dateFrom);
            updateToDate(data.dateTo);
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

    public void loadToday(View view) {
        updateBothDates(LocalDate.now(), LocalDate.now());
        getSupportLoaderManager().restartLoader(STATS_LOADER_ID, null, StatsActivity.this);
    }

    public void loadYesterday(View view) {
        updateBothDates(LocalDate.now().minusDays(1), LocalDate.now().minusDays(1));
        getSupportLoaderManager().restartLoader(STATS_LOADER_ID, null, StatsActivity.this);
    }

    public void loadWeek(View view) {
        updateBothDates(LocalDate.now().minusDays(6), LocalDate.now());
        getSupportLoaderManager().restartLoader(STATS_LOADER_ID, null, StatsActivity.this);
    }

    public void load30Days(View view) {
        updateBothDates(LocalDate.now().minusDays(29), LocalDate.now());
        getSupportLoaderManager().restartLoader(STATS_LOADER_ID, null, StatsActivity.this);
    }

    public void load365Days(View view) {
        updateFromDate(LocalDate.now().minusDays(364));
        updateToDate(LocalDate.now());
        getSupportLoaderManager().restartLoader(STATS_LOADER_ID, null, StatsActivity.this);
    }

    public void loadAll(View view) {
        updateBothDates(null, null);
        Bundle args = new Bundle();
        args.putBoolean(LOADER_ARGUMENT_LOAD_ALL, true);
        getSupportLoaderManager().restartLoader(STATS_LOADER_ID, args, StatsActivity.this);
    }

    static class StatsLoaderResult {
        private List<Spending> spendings;
        private StatsCategoryAdapter.StatsCategoryAdapterData categoryAdapterData;
        private Totals totals;
        private LocalDate dateFrom = LocalDate.now();
        private LocalDate dateTo = LocalDate.now();
    }


    private static class Totals {
        private int entriesCount;
        private int periodDays;
        private double sum;

        Totals() {
        }

        public Totals(List<Spending> spendings, int periodDays) {
            this.periodDays = periodDays;
            this.entriesCount = spendings.size();
            sum = 0d;
            for (Spending spending : spendings) {
                sum += spending.getAmount();
            }
        }

        Totals(int periodBetweenDates, List<CategoryStatsRequestResult> categories) {
            this.periodDays = periodBetweenDates;
            for (CategoryStatsRequestResult category : categories) {
                this.entriesCount += category.getEntriesCount();
                this.sum += category.getTotalAmount();
            }
        }

        int getEntriesCount() {
            return entriesCount;
        }

        int getPeriodDays() {
            return periodDays;
        }

        double getSum() {
            return sum;
        }

        double getEntriesPerDay() {
            if (entriesCount == 0 || periodDays == 0) {
                return 0;
            }
            return ((double) entriesCount) / periodDays;
        }

        double getPerDay() {
            return sum / periodDays;
        }

        double getPerWeek() {
            return getPerDay() * 7;
        }

        double getPerMonth() {
            return getPerDay() * 30;
        }

        double getPerYear() {
            return getPerDay() * 365;
        }
    }
}
