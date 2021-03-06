package ru.thecop.smtm2.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import ru.thecop.smtm2.R;
import ru.thecop.smtm2.SmtmApplication;
import ru.thecop.smtm2.db.DbHelper;
import ru.thecop.smtm2.dialog.DatePickerDialogFragment;
import ru.thecop.smtm2.model.Category;
import ru.thecop.smtm2.model.Spending;
import ru.thecop.smtm2.util.Constants;
import ru.thecop.smtm2.util.DateTimeUtils;

public class SpendingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    public static final String EXTRA_SPENDING_ID = "SpendingId";
    public static final String EXTRA_CATEGORY_ID = "CategoryId";
    private static final String DATE_PIRCKER_DIALOG_TAG = "DatePickerDialogTag";

    private TextView mDateTextView;
    private EditText mAmountEditText;
    private EditText mCommentEditText;
    private LocalDateTime mDateTime;

    private Category mCategory = null;
    private Spending mEditedSpending = null;//used for editing existing spending


    //TODO format decimals to 2 digits after dot, add spaces
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spending);
        mDateTextView = (TextView) findViewById(R.id.textViewSpendingDate);
        mAmountEditText = (EditText) findViewById(R.id.editTextSpendingAmount);
        mCommentEditText = (EditText) findViewById(R.id.editTextSpendingComment);

        long passedSpendingId = getIntent().getLongExtra(EXTRA_SPENDING_ID, -1);

        //get and load a category
        long categoryId = getIntent().getLongExtra(EXTRA_CATEGORY_ID, -1);
        if (categoryId == -1) {
            throw new IllegalArgumentException("CategoryId must be passed");
        }
        mCategory = DbHelper.findCategoryById(categoryId, (SmtmApplication) getApplication());//FakeDb.findCategoryById(categoryId);

        //set category name in action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mCategory.getName());

        //id passed - edit an existing entry
        if (passedSpendingId >= 0) {
            loadExistingSpendingValues(passedSpendingId);
        }
        //no spending id passed - init empty views
        else {
            updateDate(LocalDateTime.now());
        }
    }

    private void loadExistingSpendingValues(long spendingId) {
        mEditedSpending = DbHelper.findSpendingById(spendingId, (SmtmApplication) getApplication());//FakeDb.findSpendingById(spendingId);
        updateDate(DateTimeUtils.convert(mEditedSpending.getTimestamp()));
        updateAmountAndSelectAll(mEditedSpending.getAmount());
        mCommentEditText.setText(mEditedSpending.getComment());
    }

    public void decreaseDate(View view) {
        updateDate(mDateTime.minusDays(1));
    }

    public void increaseDate(View view) {
        updateDate(mDateTime.plusDays(1));
    }

    public void updateDate(LocalDateTime dateTime) {
        this.mDateTime = dateTime;
        String dateString = dateTime.toString(Constants.DATE_DISPLAY_FORMAT_PATTERN);
        mDateTextView.setText(dateString);
    }

    public void updateAmountAndSelectAll(double amount) {
        mAmountEditText.setText(Double.toString(amount));
        mAmountEditText.selectAll();
    }

    private boolean isEditingExisting() {
        return mEditedSpending != null;
    }

    public void save(View view) {
        //todo write ui-test for saving
        if (!checkAmount()) {
            return;
        }
        Spending s = mEditedSpending != null ? mEditedSpending : new Spending();

        s.setConfirmed(true);
        s.setAmount(Double.parseDouble(mAmountEditText.getText().toString()));
        s.setCategory(mCategory);
        s.setTimestamp(DateTimeUtils.convert(mDateTime));
        s.setComment(mCommentEditText.getText().toString());

        if (isEditingExisting()) {
            DbHelper.update(s, (SmtmApplication) getApplication());
        } else {
            DbHelper.create(s, (SmtmApplication) getApplication());
        }
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    private boolean checkAmount() {
        String amountString = mAmountEditText.getText().toString();
        amountString = amountString.trim();
        if (amountString.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_amount), Toast.LENGTH_SHORT).show();
            return false;
        }
        amountString = amountString.replace(',', '.');
        try {
            double amount = Double.parseDouble(amountString);
            if (amount <= 0) {
                Toast.makeText(this, getString(R.string.negative_or_zero_amount), Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.wrong_amount), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void showDatePickerDialog(View view) {
        DatePickerDialogFragment datePickerDialogFragment = DatePickerDialogFragment.newInstance(mDateTime, this);
        datePickerDialogFragment.show(getSupportFragmentManager(), DATE_PIRCKER_DIALOG_TAG);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        updateDate(new LocalDate(year, month + 1, dayOfMonth).toLocalDateTime(LocalTime.MIDNIGHT));
    }
}
