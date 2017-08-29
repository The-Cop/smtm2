package ru.thecop.smtm2.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import org.joda.time.LocalDateTime;
import ru.thecop.smtm2.SmtmApplication;
import ru.thecop.smtm2.db.DbHelper;
import ru.thecop.smtm2.model.Category;
import ru.thecop.smtm2.model.CategoryKeyword;
import ru.thecop.smtm2.model.Spending;
import ru.thecop.smtm2.preferences.PreferenceUtils;
import ru.thecop.smtm2.util.DateTimeUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SmsBroadcastReceiver extends BroadcastReceiver {


    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SmsBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(SMS_RECEIVED)) {
            Log.d(TAG, "action is not " + SMS_RECEIVED);
            return;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            Log.d(TAG, "bundle is null");
            return;
        }
        String format = bundle.getString("format");
        Object[] pdus = (Object[]) bundle.get("pdus");
        Log.d(TAG, "pdus count = " + pdus.length);
        for (Object pdu : pdus) {
            SmsMessage smsMessage;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                smsMessage = SmsMessage.createFromPdu((byte[]) pdu, format);
            } else {
                smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
            }
            String body = smsMessage.getMessageBody();
            String address = smsMessage.getOriginatingAddress();
            Log.d(TAG, "Message from " + address + "; text = " + body);

            parseAndSaveSpending(body, address, context);
        }
    }

    //todo test this
    private void parseAndSaveSpending(String body, String address, Context context) {
        String bodyLowercase = body.toLowerCase();

        List<String> bodyWords = Arrays.asList(body.split(" "));
        if (containsStopWords(bodyWords, context)) {
            return;
        }

        if (!containsGoWord(bodyWords, context)) {
            return;
        }

        AmountParseResult amountParseResult = AmountExtractor.extractSum(body);
        if (amountParseResult == null) {
            return;
        }

        Category category = null;
        SmtmApplication smtmApplication = ((SmtmApplication) context.getApplicationContext());
        List<CategoryKeyword> categoryKeywords = DbHelper.findAllCategoryKeywords(smtmApplication);
        for (CategoryKeyword categoryKeyword : categoryKeywords) {
            if (bodyLowercase.contains(categoryKeyword.getKeyword().toLowerCase())) {
                category = categoryKeyword.getCategory();
                break;
            }
        }

        Spending spending = new Spending();
        spending.setAmount(amountParseResult.getBestFit().doubleValue());
        spending.setCategory(category);
        spending.setTimestamp(DateTimeUtils.convert(LocalDateTime.now()));
        spending.setConfirmed(false);
        spending.setSmsFrom(address);
        spending.setSmsText(body);
        DbHelper.create(spending, smtmApplication);
    }

    //todo test this
    private boolean containsStopWords(List<String> bodyWords, Context context) {
        Set<String> stopWords = PreferenceUtils.getSmsParseStopWords(context);
        for (String stopWord : stopWords) {
            if (bodyWords.contains(stopWord.toLowerCase())) {
                Log.d(TAG, "Sms contains stopword: " + stopWord);
                return true;
            }
        }
        return false;
    }

    //todo test this
    private boolean containsGoWord(List<String> bodyWords, Context context) {
        Set<String> goWords = PreferenceUtils.getSmsParseStopWords(context);
        for (String goWord : goWords) {
            if (bodyWords.contains(goWord.toLowerCase())) {
                return true;
            }
        }
        return false;
    }


}
