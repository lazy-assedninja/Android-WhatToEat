package me.lazy_assedninja.what_to_eat.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static me.lazy_assedninja.what_to_eat.common.LiveDataTestUtil.getOrAwaitValue;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createPromotion;

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

import me.lazy_assedninja.what_to_eat.vo.Promotion;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class PromotionDaoTest extends DbTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private PromotionDao promotionDao;
    private final String title = "Lazy-assed Ninja";

    @Before
    public void init() {
        promotionDao = db.promotionDao();

        List<Promotion> list = new ArrayList<>();
        list.add(createPromotion(1, title));
        promotionDao.insertAll(list);
    }

    @Test
    public void insertAndGetByID() throws TimeoutException, InterruptedException {
        Promotion data = getOrAwaitValue(promotionDao.get(1));
        assertThat(data.getTitle(), is(title));
    }

    @Test
    public void insertAndGetAllPromotions() throws TimeoutException, InterruptedException {
        List<Promotion> data = getOrAwaitValue(promotionDao.getPromotions());
        assertThat(data.get(0).getTitle(), is(title));
    }

    @Test
    public void replaceWhenInsertOnConflictAndGetByID() throws TimeoutException,
            InterruptedException {
        String newTitle = "new Lazy-assed Ninja";
        List<Promotion> list = new ArrayList<>();
        list.add(createPromotion(1, newTitle));
        promotionDao.insertAll(list);

        Promotion data = getOrAwaitValue(promotionDao.get(1));
        assertThat(data.getTitle(), is(newTitle));
    }

    @Test
    public void deleteAllPromotionsAndCheck() throws TimeoutException, InterruptedException {
        promotionDao.delete();
        List<Promotion> data = getOrAwaitValue(promotionDao.getPromotions());
        assertThat(data.size(), is(0));
    }
}