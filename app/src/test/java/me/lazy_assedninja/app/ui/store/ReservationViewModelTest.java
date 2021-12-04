package me.lazy_assedninja.app.ui.store;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static me.lazy_assedninja.app.common.TestUtil.createReservation;
import static me.lazy_assedninja.app.common.TestUtil.createResult;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import me.lazy_assedninja.app.repository.ReservationRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.ui.store.reservation.ReservationViewModel;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Reservation;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class ReservationViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final UserRepository userRepository = mock(UserRepository.class);
    private final ReservationRepository reservationRepository = mock(ReservationRepository.class);
    private final ReservationViewModel viewModel = new ReservationViewModel(userRepository,
            reservationRepository);

    @Test
    public void testNull() {
        // Load reservations
        assertThat(viewModel.reservations, notNullValue());

        verify(reservationRepository, never()).loadReservations(any());
        viewModel.requestReservation();
        verify(reservationRepository, never()).loadReservations(any());

        // Add or cancel reservation
        assertThat(viewModel.result, notNullValue());

        verify(reservationRepository, never()).addOrCancelReservation(anyBoolean(), any());
        viewModel.setCancelRequest(createReservation());
        verify(reservationRepository, never()).addOrCancelReservation(anyBoolean(), any());
    }

    @Test
    public void sendResultToUI() {
        // Load reservations
        MutableLiveData<Resource<List<Reservation>>> list = new MutableLiveData<>();
        when(reservationRepository.loadReservations(any())).thenReturn(list);
        Observer<Resource<List<Reservation>>> listObserver = mock(Observer.class);
        viewModel.reservations.observeForever(listObserver);
        viewModel.requestReservation();
        verify(listObserver, never()).onChanged(any());
        List<Reservation> listData = new ArrayList<>();
        Resource<List<Reservation>> listResource = Resource.success(listData);

        list.setValue(listResource);
        verify(listObserver).onChanged(listResource);

        // Add or cancel reservation
        MutableLiveData<Event<Resource<Result>>> result = new MutableLiveData<>();
        when(reservationRepository.addOrCancelReservation(anyBoolean(), any())).thenReturn(result);
        Observer<Event<Resource<Result>>> resultObserver = mock(Observer.class);
        viewModel.result.observeForever(resultObserver);
        viewModel.setCancelRequest(createReservation());
        verify(resultObserver, never()).onChanged(any());
        Event<Resource<Result>> resultResource = new Event<>(Resource.success(createResult()));

        result.setValue(resultResource);
        verify(resultObserver).onChanged(resultResource);
    }

    @Test
    public void loadReservations() {
        viewModel.reservations.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(reservationRepository);
        viewModel.requestReservation();
        verify(reservationRepository).loadReservations(any());
        verifyNoMoreInteractions(reservationRepository);
    }

    @Test
    public void cancelReservation() {
        viewModel.result.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(reservationRepository);
        viewModel.setCancelRequest(createReservation());
        verify(reservationRepository).addOrCancelReservation(anyBoolean(), any());
        verifyNoMoreInteractions(reservationRepository);
    }

    @Test
    public void refresh() {
        viewModel.requestReservation();
        verifyNoMoreInteractions(reservationRepository);
        viewModel.refresh();
        verifyNoMoreInteractions(reservationRepository);
        Observer<Resource<List<Reservation>>> userObserver = mock(Observer.class);
        viewModel.reservations.observeForever(userObserver);

        verify(reservationRepository).loadReservations(any());
        reset(reservationRepository);

        viewModel.refresh();
        verify(reservationRepository).loadReservations(any());
        reset(reservationRepository);
        viewModel.reservations.removeObserver(userObserver);

        viewModel.refresh();
        verifyNoMoreInteractions(reservationRepository);
    }
}
