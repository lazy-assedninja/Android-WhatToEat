package me.lazy_assedninja.what_to_eat.repository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static me.lazy_assedninja.what_to_eat.common.TestUtil.createHistory;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import me.lazy_assedninja.what_to_eat.db.HistoryDao;
import me.lazy_assedninja.what_to_eat.db.WhatToEatDatabase;
import me.lazy_assedninja.what_to_eat.util.InstantExecutorUtil;
import me.lazy_assedninja.what_to_eat.vo.History;

@RunWith(JUnit4.class)
public class HistoryRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private HistoryRepository repository;
    private final HistoryDao historyDao = mock(HistoryDao.class);

    @Before
    public void init() {
        WhatToEatDatabase db = mock(WhatToEatDatabase.class);
        when(db.historyDao()).thenReturn(historyDao);
        repository = new HistoryRepository(new InstantExecutorUtil(), historyDao);
    }

    @Test
    public void addHistory() {
        History history = createHistory(1);
        repository.addToHistory(history);

        verify(historyDao).insert(history);
    }

    @Test
    public void getHistoryIDs() {
        repository.getHistoryIDs();

        verify(historyDao).getHistoryIDs();
    }

    @Test
    public void loadHistories() {
        List<Integer> list = new ArrayList<>();
        repository.loadHistories(list);

        verify(historyDao).getHistoryStores(list);
    }

    @Test
    public void deleteAll() {
        repository.deleteAll();

        verify(historyDao).deleteAll();
    }
}