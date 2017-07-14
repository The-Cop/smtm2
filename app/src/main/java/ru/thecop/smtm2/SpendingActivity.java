package ru.thecop.smtm2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SpendingActivity extends AppCompatActivity {

    public static final String EXTRA_SPENDING_ID = "SpendingId";
    public static final String EXTRA_CATEGORY_ID = "CategoryId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spending);
    }
}
