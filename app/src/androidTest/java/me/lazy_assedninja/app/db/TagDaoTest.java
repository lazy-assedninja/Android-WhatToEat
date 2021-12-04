package me.lazy_assedninja.app.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static me.lazy_assedninja.app.common.TestUtil.createTag;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TagDaoTest extends DbTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private TagDao tagDao;

    @Before
    public void init() {
        tagDao = db.tagDao();

        tagDao.insert(createTag(1));
    }

    @Test
    public void insertAndGet() {
        int dataSize = tagDao.getTagSize();
        assertThat(dataSize, is(1));
    }

    @Test
    public void replaceWhenInsertTagOnConflictAndGetSizeOfTags() {
        tagDao.insert(createTag(1));

        int dataSize = tagDao.getTagSize();
        assertThat(dataSize, is(1));
    }
}