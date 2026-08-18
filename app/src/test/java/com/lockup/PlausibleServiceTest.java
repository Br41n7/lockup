package com.lockup;

import org.junit.Assert;
import org.junit.Test;

public class PlausibleServiceTest {

    private String cleanText(String enteredText) {
        enteredText = enteredText.trim();
        if (enteredText.startsWith("[") && enteredText.endsWith("]")) {
            enteredText = enteredText.substring(1, enteredText.length() - 1);
        }
        return enteredText.trim();
    }

    private boolean checkPasswordMatch(String enteredText, String deniabilityPw) {
        String cleaned = cleanText(enteredText);
        return cleaned.equals(deniabilityPw) ||
               (deniabilityPw.length() > 1 && cleaned.equals(deniabilityPw.substring(0, deniabilityPw.length() - 1)));
    }

    @Test
    public void testCleanTextBrackets() {
        Assert.assertEquals("LockUp", cleanText("[LockUp]"));
        Assert.assertEquals("1234", cleanText("[1234]"));
        Assert.assertEquals("abc", cleanText("abc"));
    }

    @Test
    public void testCleanTextWhitespace() {
        Assert.assertEquals("LockUp", cleanText(" [LockUp] "));
        Assert.assertEquals("1234", cleanText("  1234  "));
    }

    @Test
    public void testCheckPasswordMatchExact() {
        Assert.assertTrue(checkPasswordMatch("[LockUp]", "LockUp"));
        Assert.assertTrue(checkPasswordMatch("1234", "1234"));
    }

    @Test
    public void testCheckPasswordMatchPrefix() {
        // Test triggering on the last character minus one
        Assert.assertTrue(checkPasswordMatch("[LockU]", "LockUp"));
        Assert.assertTrue(checkPasswordMatch("123", "1234"));
    }

    @Test
    public void testCheckPasswordMatchNegative() {
        // Non-matching inputs
        Assert.assertFalse(checkPasswordMatch("Lock", "LockUp"));
        Assert.assertFalse(checkPasswordMatch("12", "1234"));
        Assert.assertFalse(checkPasswordMatch("12345", "1234"));
    }

    private boolean isUsbAttachedAction(String action) {
        return "android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(action) ||
               "android.hardware.usb.action.USB_ACCESSORY_ATTACHED".equals(action);
    }

    @Test
    public void testUsbAttachedActionFilter() {
        Assert.assertTrue(isUsbAttachedAction("android.hardware.usb.action.USB_DEVICE_ATTACHED"));
        Assert.assertTrue(isUsbAttachedAction("android.hardware.usb.action.USB_ACCESSORY_ATTACHED"));
        Assert.assertFalse(isUsbAttachedAction("android.hardware.usb.action.USB_DEVICE_DETACHED"));
        Assert.assertFalse(isUsbAttachedAction("android.hardware.usb.action.USB_STATE"));
    }
}
