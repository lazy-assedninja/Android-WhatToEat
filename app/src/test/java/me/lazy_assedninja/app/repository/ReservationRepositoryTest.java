package me.lazy_assedninja.app.repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static me.lazy_assedninja.app.common.TestUtil.createReservation;
import static me.lazy_assedninja.app.common.TestUtil.createReservationDTO;
import static me.lazy_assedninja.app.common.TestUtil.createResult;
import static me.lazy_assedninja.app.util.ApiUtil.successCall;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import me.lazy_assedninja.app.api.ApiResponse;
import me.lazy_assedninja.app.api.WhatToEatService;
import me.lazy_assedninja.app.db.ReservationDao;
import me.lazy_assedninja.app.db.WhatToEatDatabase;
import me.lazy_assedninja.app.dto.ReservationDTO;
import me.lazy_assedninja.app.util.InstantExecutorUtil;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Reservation;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.library.util.NetworkUtil;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class ReservationRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ReservationRepository repository;
    private final NetworkUtil networkUtil = mock(NetworkUtil.class);
    private final ReservationDao reservationDao = mock(ReservationDao.class);
    private final WhatToEatService service = mock(WhatToEatService.class);

    @Before
    public void init() {
        WhatToEatDatabase db = mock(WhatToEatDatabase.class);
        when(db.reservationDao()).thenReturn(reservationDao);
        doCallRealMethod().when(db).runInTransaction((Runnable) any());
        repository = new ReservationRepository(new InstantExecutorUtil(),
                networkUtil, db, reservationDao, service);
    }

    @Test
    public void loadReservationsFromNetwork() {
        ReservationDTO reservationDTO = createReservationDTO();
        MutableLiveData<List<Reservation>> dbData = new MutableLiveData<>();
        when(reservationDao.getReservations()).thenReturn(dbData);

        List<Reservation> list = new ArrayList<>();
        list.add(createReservation(1, "reservation name"));
        LiveData<ApiResponse<List<Reservation>>> call = successCall(list);
        when(service.getReservationList(reservationDTO)).thenReturn(call);

        LiveData<Resource<List<Reservation>>> data = repository.loadReservations(reservationDTO);
        verify(reservationDao).getReservations();
        verifyNoMoreInteractions(service);

        Observer<Resource<List<Reservation>>> observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(service);
        verify(observer).onChanged(Resource.loading(null));
        MutableLiveData<List<Reservation>> updateDbData = new MutableLiveData<>();
        when(reservationDao.getReservations()).thenReturn(updateDbData);

        dbData.postValue(null);
        verify(service).getReservationList(reservationDTO);
        verify(reservationDao).delete();
        verify(reservationDao).insertAll(list);

        updateDbData.postValue(list);
        verify(observer).onChanged(Resource.success(list));
    }

    @Test
    public void loadReservationsFromDb() {
        ReservationDTO reservationDTO = createReservationDTO();
        MutableLiveData<List<Reservation>> dbData = new MutableLiveData<>();
        when(reservationDao.getReservations()).thenReturn(dbData);

        LiveData<Resource<List<Reservation>>> data = repository.loadReservations(reservationDTO);
        verify(reservationDao).getReservations();
        verifyNoMoreInteractions(service);

        Observer<Resource<List<Reservation>>> observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(service);
        verify(observer).onChanged(Resource.loading(null));

        List<Reservation> list = new ArrayList<>();
        list.add(createReservation(1, "reservation name"));
        dbData.postValue(list);
        verify(observer).onChanged(Resource.success(list));
    }

    @Test
    public void addReservation() {
        Reservation reservation = createReservation();
        Result result = createResult();
        LiveData<ApiResponse<Result>> call = successCall(result);
        when(service.createReservation(reservation)).thenReturn(call);

        LiveData<Event<Resource<Result>>> data = repository.addOrCancelReservation(true,
                reservation);
        Observer<Event<Resource<Result>>> observer = mock(Observer.class);
        data.observeForever(observer);
        verify(service).createReservation(reservation);
        verify(reservationDao).insert(reservation);

        verify(observer).onChanged(new Event<>(Resource.success(result)));
    }
}