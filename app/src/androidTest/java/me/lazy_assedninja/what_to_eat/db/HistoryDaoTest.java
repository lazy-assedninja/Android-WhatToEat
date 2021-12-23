package me.lazy_assedninja.what_to_eat.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static me.lazy_assedninja.what_to_eat.common.LiveDataTestUtil.getOrAwaitValue;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createHistory;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class HistoryDaoTest extends DbTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private HistoryDao historyDao;
    private final int historyID = 1;

    @Before
    public void init() {
        historyDao = db.historyDao();

        historyDao.insert(createHistory(historyID));
    }

    @Test
    public void insertAndGetIDs() throws TimeoutException, InterruptedException {
        List<Integer> list = getOrAwaitValue(historyDao.getHistoryIDs());
        assertThat(list.get(0), is(historyID));
    }

    @Test
    public void replaceWhenInsertOnConflictAndGetIDs() throws TimeoutException, InterruptedException {
        historyDao.insert(createHistory(historyID));

        List<Integer> data = getOrAwaitValue(historyDao.getHistoryIDs());
        assertThat(data.size(), is(historyID));
    }

    @Test
    public void deleteAllHistoryAndCheck() throws TimeoutException, InterruptedException {
        historyDao.deleteAll();

        List<Integer> data = getOrAwaitValue(historyDao.getHistoryIDs());
        assertThat(data.size(), is(0));
    }
}