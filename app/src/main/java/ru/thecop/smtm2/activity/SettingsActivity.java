package ru.thecop.smtm2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import ru.thecop.smtm2.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void editSmsGoWords(View view) {
        Intent intent = new Intent(SettingsActivity.this, WordListEditActivity.class);
        intent.putExtra(WordListEditActivity.EXTRA_MODE, WordListEditActivity.Mode.SMS_GO_WORDS);
        startActivity(intent);
    }

    public void editSmsStopWords(View view) {
        Intent intent = new Intent(SettingsActivity.this, WordListEditActivity.class);
        intent.putExtra(WordListEditActivity.EXTRA_MODE, WordListEditActivity.Mode.SMS_STOP_WORDS);
        startActivity(intent);
    }

    public void editCategoriesKeywords(View view) {
        Intent intent = new Intent(SettingsActivity.this, CategoryActivity.class);
        intent.putExtra(CategoryActivity.EXTRA_MODE, CategoryActivity.Mode.EDIT_KEYWORDS);
        startActivity(intent);
    }
}
