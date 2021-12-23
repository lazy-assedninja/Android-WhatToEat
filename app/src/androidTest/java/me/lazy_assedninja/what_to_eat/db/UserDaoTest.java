package me.lazy_assedninja.what_to_eat.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static me.lazy_assedninja.what_to_eat.common.LiveDataTestUtil.getOrAwaitValue;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createUser;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import me.lazy_assedninja.what_to_eat.vo.User;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class UserDaoTest extends DbTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private UserDao userDao;
    private final String name = "Lazy-assed Ninja";
    private final String updateTime = "update time";

    @Before
    public void init() {
        userDao = db.userDao();

        userDao.insert(createUser(name));
    }

    @Test
    public void insertAndGet() {
        User data = userDao.getUser();
        assertThat(data.getName(), is(name));
    }

    @Test
    public void insertAndGetLiveData() throws TimeoutException, InterruptedException {
        User liveData = getOrAwaitValue(userDao.get());
        assertThat(liveData.getName(), is(name));
    }

    @Test
    public void replaceWhenInsertOnConflictAndGet() {
        String newName = "new Lazy-assed Ninja";
        userDao.insert(createUser(newName));

        User data = userDao.getUser();
        assertThat(data.getName(), is(newName));
    }

    @Test
    public void updateGoogleIDAndGet() {
        String googleID = "google ID";
        userDao.updateGoogleID(googleID, updateTime);

        User data = userDao.getUser();
        assertThat(data.getGoogleAccount().getGoogleID(), is(googleID));
    }

    @Test
    public void updatePasswordAndGet() {
        String password = "password";
        userDao.updatePassword(password, updateTime);

        User data = userDao.getUser();
        assertThat(data.getPassword(), is(password));
    }

    @Test
    public void updateTimeAndGet() {
        String newUpdateTime = "new update time";
        userDao.updateFile(newUpdateTime);

        User data = userDao.getUser();
        assertThat(data.getUpdateTime(), is(newUpdateTime));
    }

    @Test
    public void deleteUserAndCheck() {
        userDao.delete();

        User data = userDao.getUser();
        assertThat(data, is(nullValue()));
    }
}