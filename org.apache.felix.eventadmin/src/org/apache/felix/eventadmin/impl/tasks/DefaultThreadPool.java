/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.felix.eventadmin.impl.tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.felix.eventadmin.impl.util.LogWrapper;


/**
 * A thread pool that allows to execute tasks using pooled threads in order
 * to ease the thread creation overhead.
 *
 * @author <a href="mailto:dev@felix.apache.org">Felix Project Team</a>
 */
public class DefaultThreadPool
{

    private volatile ExecutorService executor;

    private final ThreadFactory threadFactory;

    private final AtomicInteger oldSize = new AtomicInteger(-1);

    /**
     * Create a new pool.
     */
    public DefaultThreadPool(final int poolSize, final boolean syncThreads)
    {
        if ( syncThreads )
        {
            threadFactory = new ThreadFactory()
            {

                @Override
                public Thread newThread( final Runnable command )
                {
                    final Thread thread = new SyncThread( command );
                    thread.setPriority( Thread.NORM_PRIORITY );
                    thread.setDaemon( true );

                    return thread;
                }
            };
        }
        else
        {
            threadFactory = new ThreadFactory()
            {

                @Override
                public Thread newThread( final Runnable command )
                {
                    final Thread thread = new Thread( command );
                    thread.setPriority( Thread.NORM_PRIORITY );
                    thread.setDaemon( true );

                    return thread;
                }
            };
        }
   	    configure(poolSize);
    }

    /**
     * Configure a new pool size.
     */
    public synchronized void configure(final int poolSize)
    {
        if ( oldSize.get() != poolSize)
        {
            oldSize.set(poolSize);
            final ExecutorService oldService = this.executor;
            this.executor = Executors.newFixedThreadPool(poolSize, threadFactory);
            if ( oldService != null )
            {
                oldService.shutdown();
            }
        }
    }

    /**
     * Returns current pool size.
     */
    public int getPoolSize()
    {
    	return oldSize.get();
    }

    /**
     * Close the pool i.e, stop pooling threads. Note that subsequently, task will
     * still be executed but no pooling is taking place anymore.
     */
    public void close()
    {
        ExecutorService executor = this.executor;
        if (null != executor)
        {
            executor.shutdownNow();
        }
    }

    /**
     * Execute the task in a free thread or create a new one.
     * @param task The task to execute
     * @return <code>true</code> if successfully submitted; otherwise <code>false</code>
     */
    public boolean executeTask(final Runnable task)
    {
        try
        {
            ExecutorService executor = this.executor;
            if (null != executor)
            {
                executor.submit(task);
                return true;
            }
        }
        catch (final Throwable t)
        {
            LogWrapper.getLogger().log(
                    LogWrapper.LOG_WARNING,
                    "Exception: " + t, t);
            // ignore this
        }
        return false;
    }
}
