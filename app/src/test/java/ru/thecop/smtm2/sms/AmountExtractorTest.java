package ru.thecop.smtm2.sms;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

public class AmountExtractorTest {
    @Test
    public void tinkoff() {
        AmountParseResult r;
        r = AmountExtractor.extractSum("Pokupka. Karta *7464. Summa 180.00 RUB. MEALTY 127560 SUPER, MOSCOW. 12.05.2017 13:08. Dostupno 6661313.32 RUB. Tinkoff.ru");
        assertEquals(new BigDecimal("180.00"), r.getBestFit());
        assertEquals(3, r.getAllSums().size());
        assertThat(r.getAllSums(), IsIterableContainingInOrder.contains(
                new BigDecimal("180.00"),
                new BigDecimal("127560"),
                new BigDecimal("6661313.32")
        ));

        r = AmountExtractor.extractSum("Platezh. Karta *7464. Summa 500.00 RUB. mBank.Beeline-Internet. 26.08.2016 00:01. Dostupno 28538.28 RUB. Tinkoff.ru");
        assertEquals(new BigDecimal("500.00"), r.getBestFit());
        assertEquals(2, r.getAllSums().size());
        assertThat(r.getAllSums(), IsIterableContainingInOrder.contains(
                new BigDecimal("500.00"),
                new BigDecimal("28538.28")
        ));

        r = AmountExtractor.extractSum("Pokupka za 01.05.2017. Karta *7464. Summa 297.00 RUB. VIKTORIJA-61, MOSKVA. Dostupno 26499.73 RUB. Tinkoff.ru");
        assertEquals(new BigDecimal("297.00"), r.getBestFit());
        assertEquals(2, r.getAllSums().size());
        assertThat(r.getAllSums(), IsIterableContainingInOrder.contains(
                new BigDecimal("297.00"),
                new BigDecimal("26499.73")
        ));

        r = AmountExtractor.extractSum("Pokupka. Beskontaktnaya oplata. Summa 218.00 RUB. OOO CPT (125212), Mosk, MOSKVA. 02.12.2016 13:55. Dostupno s uchetom rashodnogo limita 14782.00 RUB. Tinkoff.ru");
        assertEquals(new BigDecimal("218.00"), r.getBestFit());
        assertEquals(2, r.getAllSums().size());
        assertThat(r.getAllSums(), IsIterableContainingInOrder.contains(
                new BigDecimal("218.00"),
                new BigDecimal("14782.00")
        ));

        r = AmountExtractor.extractSum("Regulyarnaya pokupka. Karta *4256. Summa 5.69 USD. UBER   *US OCT15 FWAME, SAN FRANCISCO. 16.10.2016 02:25. Dostupno 487.51 USD. Tinkoff.ru");
        assertEquals(new BigDecimal("5.69"), r.getBestFit());
        assertEquals(2, r.getAllSums().size());
        assertThat(r.getAllSums(), IsIterableContainingInOrder.contains(
                new BigDecimal("5.69"),
                new BigDecimal("487.51")
        ));
    }

    @Test
    public void tinkoffCommas() {
        AmountParseResult r;
        r = AmountExtractor.extractSum("Pokupka. Karta *7464. Summa 180,00 RUB. MEALTY 127560 SUPER, MOSCOW. 12.05.2017 13:08. Dostupno 6661313,32 RUB. Tinkoff.ru");
        assertEquals(new BigDecimal("180.00"), r.getBestFit());
        assertEquals(3, r.getAllSums().size());
        assertThat(r.getAllSums(), IsIterableContainingInOrder.contains(
                new BigDecimal("180.00"),
                new BigDecimal("127560"),
                new BigDecimal("6661313.32")
        ));

        r = AmountExtractor.extractSum("Platezh. Karta *7464. Summa 500,00 RUB. mBank.Beeline-Internet. 26.08.2016 00:01. Dostupno 28538,28 RUB. Tinkoff.ru");
        assertEquals(new BigDecimal("500.00"), r.getBestFit());
        assertEquals(2, r.getAllSums().size());
        assertThat(r.getAllSums(), IsIterableContainingInOrder.contains(
                new BigDecimal("500.00"),
                new BigDecimal("28538.28")
        ));

        r = AmountExtractor.extractSum("Pokupka za 01.05.2017. Karta *7464. Summa 297,00 RUB. VIKTORIJA-61, MOSKVA. Dostupno 26499,73 RUB. Tinkoff.ru");
        assertEquals(new BigDecimal("297.00"), r.getBestFit());
        assertEquals(2, r.getAllSums().size());
        assertThat(r.getAllSums(), IsIterableContainingInOrder.contains(
                new BigDecimal("297.00"),
                new BigDecimal("26499.73")
        ));

        r = AmountExtractor.extractSum("Pokupka. Beskontaktnaya oplata. Summa 218,00 RUB. OOO CPT (125212), Mosk, MOSKVA. 02.12.2016 13:55. Dostupno s uchetom rashodnogo limita 14782,00 RUB. Tinkoff.ru");
        assertEquals(new BigDecimal("218.00"), r.getBestFit());
        assertEquals(2, r.getAllSums().size());
        assertThat(r.getAllSums(), IsIterableContainingInOrder.contains(
                new BigDecimal("218.00"),
                new BigDecimal("14782.00")
        ));

        r = AmountExtractor.extractSum("Regulyarnaya pokupka. Karta *4256. Summa 5,69 USD. UBER   *US OCT15 FWAME, SAN FRANCISCO. 16.10.2016 02:25. Dostupno 487,51 USD. Tinkoff.ru");
        assertEquals(new BigDecimal("5.69"), r.getBestFit());
        assertEquals(2, r.getAllSums().size());
        assertThat(r.getAllSums(), IsIterableContainingInOrder.contains(
                new BigDecimal("5.69"),
                new BigDecimal("487.51")
        ));
    }

    @Test
    public void vtb() {
        AmountParseResult r;
        r = AmountExtractor.extractSum("Karta *6454: Oplata 310.00 RUR; OKEY; 15.05.2017 19:26, dostupno 47123.24 RUR (v tom chisle kred. 0.00 RUR). VTB24");
        assertEquals(new BigDecimal("310.00"), r.getBestFit());
        assertEquals(3, r.getAllSums().size());
        assertThat(r.getAllSums(), IsIterableContainingInOrder.contains(
                new BigDecimal("310.00"),
                new BigDecimal("47123.24"),
                new BigDecimal("0.00")
        ));
    }

    @Test
    public void sber() {
        AmountParseResult r;
        r = AmountExtractor.extractSum("VISA9770 04.05.17 09:38 покупка 7660р URAL AIRLINES Баланс: 6492.96р");
        assertEquals(new BigDecimal("7660"), r.getBestFit());
        assertEquals(2, r.getAllSums().size());
        assertThat(r.getAllSums(), IsIterableContainingInOrder.contains(
                new BigDecimal("7660"),
                new BigDecimal("6492.96")
        ));
    }
}
