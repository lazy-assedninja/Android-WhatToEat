package me.lazy_assedninja.what_to_eat.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static me.lazy_assedninja.what_to_eat.common.LiveDataTestUtil.getOrAwaitValue;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createStore;

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

import me.lazy_assedninja.what_to_eat.vo.Store;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class FavoriteDaoTest extends DbTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private StoreDao storeDao;
    private FavoriteDao favoriteDao;
    private final String name = "Lazy-assed Ninja";

    @Before
    public void init() {
        storeDao = db.storeDao();
        favoriteDao = db.favoriteDao();

        List<Store> list = new ArrayList<>();
        list.add(createStore(1, name, true));
        storeDao.insertAll(list);
    }

    @Test
    public void insertAndGetFavorites() throws TimeoutException, InterruptedException {
        List<Store> data = getOrAwaitValue(favoriteDao.getFavorites(true));
        assertThat(data.get(0).getName(), is(name));
    }

    @Test
    public void updateFavoriteStatusAndGet() throws TimeoutException, InterruptedException {
        favoriteDao.updateFavoriteStatus(1, false);

        Store data = getOrAwaitValue(storeDao.get(1));
        assertThat(data.isFavorite(), is(false));
    }
}