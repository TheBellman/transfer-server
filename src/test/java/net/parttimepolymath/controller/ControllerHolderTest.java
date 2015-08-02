package net.parttimepolymath.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ControllerHolderTest {
    @Mock
    private Controller controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAndSet() {
        ControllerHolder.reset();
        assertNull(ControllerHolder.getController());

        ControllerHolder.setController(controller);
        assertNotNull(ControllerHolder.getController());
        assertEquals(controller, ControllerHolder.getController());
    }

}
