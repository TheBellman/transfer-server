package net.parttimepolymath.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import net.parttimepolymath.controller.Controller;
import net.parttimepolymath.controller.ControllerHolder;
import net.parttimepolymath.controller.Status;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class StatusServiceTest {
    @Mock
    private Controller controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ControllerHolder.reset();
        ControllerHolder.setController(controller);
        when(controller.getStatus()).thenReturn(new Status("testmode"));
    }

    @Test
    public void testGetStatus() {
        StatusService instance = new StatusService();
        assertEquals("testmode", instance.getStatus().getStatus());
    }

}
