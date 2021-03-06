package org.sonatype.aether.util.listener;

/*******************************************************************************
 * Copyright (c) 2010-2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.sonatype.aether.transfer.AbstractTransferListener;
import org.sonatype.aether.transfer.TransferCancelledException;
import org.sonatype.aether.transfer.TransferEvent;
import org.sonatype.aether.transfer.TransferListener;

/**
 * A transfer listener that delegates to zero or more other listeners (multicast). The list of target listeners is
 * thread-safe, i.e. target listeners can be added or removed by any thread at any time.
 * 
 * @author Benjamin Bentmann
 */
public class ChainedTransferListener
    extends AbstractTransferListener
{

    private final List<TransferListener> listeners = new CopyOnWriteArrayList<TransferListener>();

    /**
     * Creates a new multicast listener that delegates to the specified listeners.
     * 
     * @param listeners The listeners to delegate to, may be {@code null} or empty.
     */
    public ChainedTransferListener( TransferListener... listeners )
    {
        if ( listeners != null )
        {
            add( Arrays.asList( listeners ) );
        }
    }

    /**
     * Creates a new multicast listener that delegates to the specified listeners.
     * 
     * @param listeners The listeners to delegate to, may be {@code null} or empty.
     */
    public ChainedTransferListener( Collection<TransferListener> listeners )
    {
        add( listeners );
    }

    /**
     * Adds the specified listeners to the end of the multicast chain.
     * 
     * @param listeners The listeners to add, may be {@code null} or empty.
     */
    public void add( Collection<TransferListener> listeners )
    {
        if ( listeners != null )
        {
            for ( TransferListener listener : listeners )
            {
                add( listener );
            }
        }
    }

    /**
     * Adds the specified listener to the end of the multicast chain.
     * 
     * @param listener The listener to add, may be {@code null}.
     */
    public void add( TransferListener listener )
    {
        if ( listener != null )
        {
            listeners.add( listener );
        }
    }

    /**
     * Removes the specified listener from the multicast chain. Trying to remove a non-existing listener has no effect.
     * 
     * @param listener The listener to remove, may be {@code null}.
     */
    public void remove( TransferListener listener )
    {
        if ( listener != null )
        {
            listeners.remove( listener );
        }
    }

    protected void handleError( TransferEvent event, TransferListener listener, RuntimeException error )
    {
        // default just swallows errors
    }

    @Override
    public void transferInitiated( TransferEvent event )
        throws TransferCancelledException
    {
        for ( TransferListener listener : listeners )
        {
            try
            {
                listener.transferInitiated( event );
            }
            catch ( RuntimeException e )
            {
                handleError( event, listener, e );
            }
        }
    }

    @Override
    public void transferStarted( TransferEvent event )
        throws TransferCancelledException
    {
        for ( TransferListener listener : listeners )
        {
            try
            {
                listener.transferStarted( event );
            }
            catch ( RuntimeException e )
            {
                handleError( event, listener, e );
            }
        }
    }

    @Override
    public void transferProgressed( TransferEvent event )
        throws TransferCancelledException
    {
        for ( TransferListener listener : listeners )
        {
            try
            {
                listener.transferProgressed( event );
            }
            catch ( RuntimeException e )
            {
                handleError( event, listener, e );
            }
        }
    }

    @Override
    public void transferCorrupted( TransferEvent event )
        throws TransferCancelledException
    {
        for ( TransferListener listener : listeners )
        {
            try
            {
                listener.transferCorrupted( event );
            }
            catch ( RuntimeException e )
            {
                handleError( event, listener, e );
            }
        }
    }

    @Override
    public void transferSucceeded( TransferEvent event )
    {
        for ( TransferListener listener : listeners )
        {
            try
            {
                listener.transferSucceeded( event );
            }
            catch ( RuntimeException e )
            {
                handleError( event, listener, e );
            }
        }
    }

    @Override
    public void transferFailed( TransferEvent event )
    {
        for ( TransferListener listener : listeners )
        {
            try
            {
                listener.transferFailed( event );
            }
            catch ( RuntimeException e )
            {
                handleError( event, listener, e );
            }
        }
    }

}
