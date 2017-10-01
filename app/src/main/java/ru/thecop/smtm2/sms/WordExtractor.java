package ru.thecop.smtm2.sms;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class WordExtractor {

    private WordExtractor() {
    }

    public static Set<String> extractWords(String smsBody) {
        Pattern replaceNonWordCharsPattern;
        //todo dont replace all non-word characters. dashes '-' are needed, as many other symbols too.
        //replace only dots and commas?
        replaceNonWordCharsPattern = Pattern.compile("[^\\p{L}\\p{Nd}]");

        Set<String> result = new HashSet<>();
        List<String> bodyWords = Arrays.asList(smsBody.split("\\s"));
        for (String bodyWord : bodyWords) {
            Matcher m = replaceNonWordCharsPattern.matcher(bodyWord);
            result.add(m.replaceAll(""));
        }
        return result;
    }
}
