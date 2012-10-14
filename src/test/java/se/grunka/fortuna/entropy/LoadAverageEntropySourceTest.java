package se.grunka.fortuna.entropy;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import se.grunka.fortuna.accumulator.EventAdder;
import se.grunka.fortuna.accumulator.EventScheduler;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LoadAverageEntropySourceTest {

    private LoadAverageEntropySource target;
    private EventScheduler scheduler;
    private EventAdder adder;

    @Before
    public void before() throws Exception {
        target = new LoadAverageEntropySource();
        scheduler = mock(EventScheduler.class);
        adder = mock(EventAdder.class);
    }

    @Test
    public void shouldAddTwoBytesAndSchedule() throws Exception {
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                byte[] bytes = (byte[]) invocationOnMock.getArguments()[0];
                assertEquals(2, bytes.length);
                return null;
            }
        }).when(adder).add(any(byte[].class));

        target.event(scheduler, adder);

        verify(scheduler).schedule(1000, TimeUnit.MILLISECONDS);
    }
}
