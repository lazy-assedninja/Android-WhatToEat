package me.lazy_assedninja.what_to_eat.db;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import androidx.arch.core.executor.testing.CountingTaskExecutorRule;
import androidx.room.Room;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class DbTest {

    @Rule
    public CountingTaskExecutorRule countingTaskExecutorRule = new CountingTaskExecutorRule();

    public WhatToEatDatabase db;

    @Before
    public void initDb() {
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(getApplicationContext(), WhatToEatDatabase.class)
                .build();
    }

    @After
    public void closeDb() throws TimeoutException, InterruptedException {
        countingTaskExecutorRule.drainTasks(10, TimeUnit.SECONDS);
        db.close();
    }
}