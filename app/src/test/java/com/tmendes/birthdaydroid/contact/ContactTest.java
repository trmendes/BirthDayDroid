package com.tmendes.birthdaydroid.contact;

import org.junit.Assert;
import org.junit.Test;

public class ContactTest {
    @Test
    public void testFavoriteAndIgnoreSwitch() {
        Contact contact = new Contact();

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
        Contact contact = new Contact();

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
