package me.lazy_assedninja.app.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static me.lazy_assedninja.app.common.LiveDataTestUtil.getOrAwaitValue;
import static me.lazy_assedninja.app.common.TestUtil.createStore;
import static me.lazy_assedninja.app.common.TestUtil.createTag;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import me.lazy_assedninja.app.vo.Store;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class StoreDaoTest extends DbTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private StoreDao storeDao;
    private final String name = "Lazy-assed Ninja";

    @Before
    public void init() {
        db.tagDao().insert(createTag(1));
        storeDao = db.storeDao();

        List<Store> list = new ArrayList<>();
        list.add(createStore(1, name));
        storeDao.insertAll(list);
    }

    @Test
    public void insertAndGetByID() throws TimeoutException, InterruptedException {
        Store data = getOrAwaitValue(storeDao.get(1));
        assertThat(data.getName(), is(name));
    }

    @Test
    public void insertAndGetByName() throws TimeoutException, InterruptedException {
        Store data = getOrAwaitValue(storeDao.get(name));
        assertThat(data.getName(), is(name));
    }

    @Test
    public void replaceWhenInsertOnConflictAndGet() throws TimeoutException, InterruptedException {
        String newName = "new Lazy-assed Ninja";
        List<Store> list = new ArrayList<>();
        list.add(createStore(1, newName));
        storeDao.insertAll(list);

        Store newData = getOrAwaitValue(storeDao.get(1));
        assertThat(newData.getName(), is(newName));
    }

    @Test
    public void insertAndGetStoresByTagID() throws TimeoutException, InterruptedException {
        List<Store> data = getOrAwaitValue(storeDao.getStores(1));
        assertThat(data.get(0).getName(), is(name));
    }

    @Test
    public void insertAndGetAllStores() throws TimeoutException, InterruptedException {
        List<Store> data = getOrAwaitValue(storeDao.getStores());
        assertThat(data.get(0).getName(), is(name));
    }

    @Test
    public void insertAndSearchByName() throws TimeoutException, InterruptedException {
        List<Store> data = getOrAwaitValue(storeDao.search("%Ninja%"));
        assertThat(data.get(0).getName(), is(name));
    }
}