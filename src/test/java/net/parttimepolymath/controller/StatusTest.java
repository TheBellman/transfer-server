package net.parttimepolymath.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StatusTest {

    @Test
    public void testDefault() {
        Status status = new Status();
        assertEquals("down", status.getStatus());
    }

    @Test
    public void testConstructor() {
        Status status = new Status("test");
        assertEquals("test", status.getStatus());
    }

    @Test
    public void testSetStatus() {
        Status status = new Status();
        assertEquals("down", status.getStatus());
        status.setStatus("bbq ribs");
        assertEquals("bbq ribs", status.getStatus());
    }
}
