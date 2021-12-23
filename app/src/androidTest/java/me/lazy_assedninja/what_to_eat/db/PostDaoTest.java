package me.lazy_assedninja.what_to_eat.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static me.lazy_assedninja.what_to_eat.common.LiveDataTestUtil.getOrAwaitValue;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createPost;

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

import me.lazy_assedninja.what_to_eat.vo.Post;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class PostDaoTest extends DbTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private PostDao postDao;
    private final int postID = 1;
    private final String title = "Lazy-assed Ninja";
    private final int storeID = 1;

    @Before
    public void init() {
        postDao = db.postDao();

        List<Post> list = new ArrayList<>();
        list.add(createPost(postID, title, storeID));
        postDao.insertAll(list);
    }

    @Test
    public void insertAndGetByStoreID() throws TimeoutException, InterruptedException {
        List<Post> data = getOrAwaitValue(postDao.getPosts(storeID));
        assertThat(data.get(0).getTitle(), is(title));
    }

    @Test
    public void replaceWhenInsertOnConflictAndGetByStoreID() throws TimeoutException,
            InterruptedException {
        String newTitle = "new Lazy-assed Ninja";
        List<Post> list = new ArrayList<>();
        list.add(createPost(postID, newTitle, storeID));
        postDao.insertAll(list);

        List<Post> data = getOrAwaitValue(postDao.getPosts(storeID));
        assertThat(data.get(0).getTitle(), is(newTitle));
    }
}