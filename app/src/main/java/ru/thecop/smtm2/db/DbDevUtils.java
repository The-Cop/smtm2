package ru.thecop.smtm2.db;

import org.joda.time.LocalDateTime;
import ru.thecop.smtm2.model.Category;
import ru.thecop.smtm2.model.Spending;
import ru.thecop.smtm2.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DbDevUtils {

    private static final Random r = new Random();
    private static final int SPENDINGS_COUNT = 5000;

    public static void fillDataBase(SessionHolder sessionHolder) {
        List<Category> categories = DbHelper.findAllCategories(sessionHolder);
        List<Spending> spendings = new ArrayList<>(SPENDINGS_COUNT);
        for (int i = 0; i < SPENDINGS_COUNT; i++) {
            Spending s = new Spending();
            s.setConfirmed(true);
            s.setCategory(categories.get(r.nextInt(categories.size())));
            s.setTimestamp(randomTimestamp());
            s.setAmount(1 + r.nextDouble() * 500);
            s.setComment("Hey, comment number " + i);
            spendings.add(s);
        }
        DbHelper.createInSingleTransaction(spendings, sessionHolder);
    }

    private static long randomTimestamp() {
        LocalDateTime dateTime = new LocalDateTime(2017, 1 + r.nextInt(12), 1 + r.nextInt(28), r.nextInt(24), r.nextInt(60));
        return DateTimeUtils.convert(dateTime);
    }

}
