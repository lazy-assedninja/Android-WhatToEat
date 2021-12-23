package me.lazy_assedninja.what_to_eat.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static me.lazy_assedninja.what_to_eat.common.LiveDataTestUtil.getOrAwaitValue;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createComment;

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

import me.lazy_assedninja.what_to_eat.vo.Comment;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class CommentDaoTest extends DbTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private CommentDao commentDao;
    private final int commentID = 1;
    private final String star = "star";
    private final String newStar = "new star";
    private final int storeID = 1;

    @Before
    public void init() {
        commentDao = db.commentDao();

        commentDao.insert(createComment(commentID, star, storeID));
    }

    @Test
    public void insertAndGetByStoreID() throws TimeoutException, InterruptedException {
        List<Comment> data = getOrAwaitValue(commentDao.getComments(storeID));
        assertThat(data.get(0).getStar(), is(star));
    }

    @Test
    public void insertAndGetLastCommentID() {
        int data = commentDao.getLastCommentID();
        assertThat(data, is(1));
    }

    @Test
    public void replaceWhenInsertOnConflictAndGetByStoreID() throws TimeoutException,
            InterruptedException {
        commentDao.insert(createComment(commentID, newStar, storeID));

        List<Comment> data = getOrAwaitValue(commentDao.getComments(storeID));
        assertThat(data.get(0).getStar(), is(newStar));
    }

    @Test
    public void replaceWhenInsertCommentsOnConflictAndGetByStoreID() throws TimeoutException,
            InterruptedException {
        List<Comment> list = new ArrayList<>();
        list.add(createComment(commentID, newStar, storeID));
        commentDao.insertAll(list);

        List<Comment> data = getOrAwaitValue(commentDao.getComments(storeID));
        assertThat(data.get(0).getStar(), is(newStar));
    }
}