package ru.thecop.smtm2;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.joda.time.LocalDateTime;
import ru.thecop.smtm2.db.FakeDb;
import ru.thecop.smtm2.model.Category;
import ru.thecop.smtm2.model.Spending;
import ru.thecop.smtm2.util.Constants;
import ru.thecop.smtm2.util.DateTimeConverter;

import java.util.UUID;

public class SpendingActivity extends AppCompatActivity {


    public static final String EXTRA_SPENDING_ID = "SpendingId";
    public static final String EXTRA_CATEGORY_ID = "CategoryId";

    private TextView mDateTextView;
    private EditText mAmountEditText;
    private EditText mCommentEditText;
    private LocalDateTime mDateTime;

    private Category mCategory = null;
    private Spending mEditedSpending = null;//used for editing existing spending


    //TODO format decimals to 2 digits after dot
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
        mCategory = FakeDb.findCategoryById(categoryId);

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
        mEditedSpending = FakeDb.findSpendingById(spendingId);
        updateDate(DateTimeConverter.convert(mEditedSpending.getTimestamp()));
        updateAmount(mEditedSpending.getAmount());
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

    public void updateAmount(double amount){
        mAmountEditText.setText(Double.toString(amount));
    }

    private boolean isEditingExisting() {
        return mEditedSpending != null;
    }

    public void save(View view) {
        if(!checkAmount()){
            return;
        }
        Spending s = mEditedSpending != null ? mEditedSpending : new Spending();
        if (!isEditingExisting()) {
            s.setUid(UUID.randomUUID().toString());
        }
        s.setConfirmed(true);
        s.setAmount(Double.parseDouble(mAmountEditText.getText().toString()));
        s.setCategoryId(mCategory.getId());
        s.setTimestamp(DateTimeConverter.convert(mDateTime));
        s.setComment(mCommentEditText.getText().toString());

        FakeDb.saveSpending(s);
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    private boolean checkAmount(){
        String amountString = mAmountEditText.getText().toString();
        amountString = amountString.trim();
        if(amountString.isEmpty()){
            Toast.makeText(this, getString(R.string.no_amount), Toast.LENGTH_SHORT).show();
            return false;
        }
        amountString = amountString.replace(',','.');
        try{
            double amount = Double.parseDouble(amountString);
            if(amount<=0){
                Toast.makeText(this, getString(R.string.negative_or_zero_amount), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        catch (NumberFormatException e){
            Toast.makeText(this, getString(R.string.wrong_amount), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
