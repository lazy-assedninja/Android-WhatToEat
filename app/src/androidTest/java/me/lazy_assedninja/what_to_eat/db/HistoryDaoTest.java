package me.lazy_assedninja.what_to_eat.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createHistory;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class HistoryDaoTest extends DbTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private HistoryDao historyDao;

    @Before
    public void init() {
        historyDao = db.historyDao();

        historyDao.insert(createHistory(1));
    }

    @Test
    public void insertAndGetIDs(){
        List<Integer> list = historyDao.getHistoryIDs();
        assertThat(list.get(0), is(1));
    }

    @Test
    public void replaceWhenInsertOnConflictAndGetIDs(){
        historyDao.insert(createHistory(1));

        List<Integer> data = historyDao.getHistoryIDs();
        assertThat(data.size(), is(1));
    }

    @Test
    public void deleteAllHistoryAndCheck() {
        historyDao.deleteAll();

        List<Integer> data = historyDao.getHistoryIDs();
        assertThat(data.size(), is(0));
    }
}