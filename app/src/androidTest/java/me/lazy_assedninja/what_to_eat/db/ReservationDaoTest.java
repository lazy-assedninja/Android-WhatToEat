package me.lazy_assedninja.what_to_eat.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static me.lazy_assedninja.what_to_eat.common.LiveDataTestUtil.getOrAwaitValue;
import static me.lazy_assedninja.what_to_eat.common.TestUtil.createReservation;

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

import me.lazy_assedninja.what_to_eat.vo.Reservation;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ReservationDaoTest extends DbTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ReservationDao reservationDao;
    private final String name = "Lazy-assed Ninja";
    private final String newName = "new Lazy-assed Ninja";
    private final Reservation reservation = createReservation(1, name);

    @Before
    public void init() {
        reservationDao = db.reservationDao();

        reservationDao.insert(reservation);
    }

    @Test
    public void insertAndGet() throws TimeoutException, InterruptedException {
        List<Reservation> data = getOrAwaitValue(reservationDao.getReservations());
        assertThat(data.get(0).getName(), is(name));
    }

    @Test
    public void replaceWhenInsertOnConflictAndGet() throws TimeoutException, InterruptedException {
        reservationDao.insert(createReservation(1, newName));

        List<Reservation> data = getOrAwaitValue(reservationDao.getReservations());
        assertThat(data.get(0).getName(), is(newName));
    }

    @Test
    public void replaceWhenInsertReservationsOnConflictAndGet() throws TimeoutException,
            InterruptedException {
        List<Reservation> list = new ArrayList<>();
        list.add(createReservation(1, newName));
        reservationDao.insertAll(list);

        List<Reservation> data = getOrAwaitValue(reservationDao.getReservations());
        assertThat(data.get(0).getName(), is(newName));
    }

    @Test
    public void deleteSpecifyReservationAndCheck() throws TimeoutException, InterruptedException {
        reservationDao.delete(reservation);
        List<Reservation> data = getOrAwaitValue(reservationDao.getReservations());
        assertThat(data.size(), is(0));
    }

    @Test
    public void deleteAllReservationsAndCheck() throws TimeoutException, InterruptedException {
        reservationDao.delete();
        List<Reservation> data = getOrAwaitValue(reservationDao.getReservations());
        assertThat(data.size(), is(0));
    }
}