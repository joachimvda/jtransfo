package org.jtransfo.internal;

import org.jtransfo.ConvertInterceptor;
import org.jtransfo.ConvertSourceTarget;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Test for ConvertInterceptorChainPiece.
 */
public class ConvertInterceptorChainPieceTest {

    @Test
    public void testChain() throws Exception {
        ConvertInterceptor interceptor = mock(ConvertInterceptor.class);
        ConvertSourceTarget next = mock(ConvertSourceTarget.class);
        ConvertInterceptorChainPiece chainPiece = new ConvertInterceptorChainPiece(interceptor, next);
        Object source = mock(Object.class);
        Object target = mock(Object.class);
        String[] tags = new String[] { "bla" };

        chainPiece.convert(source, target, true, tags);

        verify(interceptor).convert(source, target, true, next, tags);
    }
}
