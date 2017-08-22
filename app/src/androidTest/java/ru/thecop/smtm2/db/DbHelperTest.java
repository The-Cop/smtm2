package ru.thecop.smtm2.db;

import org.junit.Test;
import ru.thecop.smtm2.model.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DbHelperTest extends GenericDatabaseTest {

    @Test
    public void testCreateCategory() {
        Category category = new Category();
        category.setName("Test");
        long id = DbHelper.create(category, this).getId();
        clearCache();
        Category loaded = getSession().getCategoryDao().load(id);
        assertNotNull(loaded);
        assertEquals("Test", loaded.getName());
        assertEquals("test", loaded.getLowerCaseName());
        assertTrue(loaded.getUpdatedTimestamp() > 0);
    }
}
