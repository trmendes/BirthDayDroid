package com.tmendes.birthdaydroid.contact;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class ContactTest {
    @Test
    public void testHasBirthdayToday() {
        Contact contact = spy(new WritableContact());

        doReturn(0).when(contact).getDaysUntilNextBirthday();
        doReturn(false).when(contact).isBornInFuture();
        Assert.assertTrue(contact.hasBirthDayToday());

        doReturn(1).when(contact).getDaysUntilNextBirthday();
        doReturn(false).when(contact).isBornInFuture();
        Assert.assertFalse(contact.hasBirthDayToday());

        doReturn(0).when(contact).getDaysUntilNextBirthday();
        doReturn(true).when(contact).isBornInFuture();
        Assert.assertFalse(contact.hasBirthDayToday());

        doReturn(0).when(contact).getDaysUntilNextBirthday();
        doReturn(true).when(contact).isBornInFuture();
        Assert.assertFalse(contact.hasBirthDayToday());
    }

    @Test
    public void testFavoriteAndIgnoreSwitch() {
        Contact contact = new WritableContact();

        contact.setFavorite(false);
        contact.setIgnore(false);
        Assert.assertFalse(contact.isFavorite());
        Assert.assertFalse(contact.isIgnore());

        contact.setFavorite(true);
        Assert.assertTrue(contact.isFavorite());
        Assert.assertFalse(contact.isIgnore());

        contact.setIgnore(true);
        Assert.assertFalse(contact.isFavorite());
        Assert.assertTrue(contact.isIgnore());

        contact.setFavorite(true);
        Assert.assertTrue(contact.isFavorite());
        Assert.assertFalse(contact.isIgnore());
    }

    @Test
    public void testFavoriteAndIgnoreToggle() {
        Contact contact = new WritableContact();

        contact.setFavorite(false);
        contact.setIgnore(false);
        Assert.assertFalse(contact.isFavorite());
        Assert.assertFalse(contact.isIgnore());

        contact.toggleFavorite();
        Assert.assertTrue(contact.isFavorite());
        Assert.assertFalse(contact.isIgnore());

        contact.toggleFavorite();
        Assert.assertFalse(contact.isFavorite());
        Assert.assertFalse(contact.isIgnore());

        contact.toggleIgnore();
        Assert.assertFalse(contact.isFavorite());
        Assert.assertTrue(contact.isIgnore());

        contact.toggleIgnore();
        Assert.assertFalse(contact.isFavorite());
        Assert.assertFalse(contact.isIgnore());

        contact.toggleFavorite();
        contact.toggleIgnore();
        Assert.assertFalse(contact.isFavorite());
        Assert.assertTrue(contact.isIgnore());

        contact.toggleFavorite();
        Assert.assertTrue(contact.isFavorite());
        Assert.assertFalse(contact.isIgnore());
    }
}
