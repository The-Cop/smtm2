package ru.thecop.smtm2.sms;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Test;

import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

public class WordExtractorTest {

    @Test
    public void testUsual() {
        String text = "Pokupka. Karta *7464. Summa 180.00 RUB. MEALTY 127560 SUPER, MOSCOW";
        Set<String> result = WordExtractor.extractWords(text);
        assertEquals(10, result.size());
        assertThat(result, IsIterableContainingInAnyOrder.containsInAnyOrder(
                "Pokupka",
                "Karta",
                "7464",
                "Summa",
                "18000",
                "RUB",
                "MEALTY",
                "127560",
                "SUPER",
                "MOSCOW"
        ));
    }

    @Test
    public void testRusSdk() {
        String text = "./=-+_Тестовые русские.. СловА!";
        Set<String> result = WordExtractor.extractWords(text);
        assertEquals(3, result.size());
        assertThat(result, IsIterableContainingInAnyOrder.containsInAnyOrder(
                "Тестовые",
                "русские",
                "СловА"
        ));
    }
}
