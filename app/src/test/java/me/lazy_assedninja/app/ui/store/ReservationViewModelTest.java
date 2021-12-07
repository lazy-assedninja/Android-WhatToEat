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
import static me.lazy_assedninja.app.common.TestUtil.createReservationDTO;
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

import me.lazy_assedninja.app.dto.ReservationDTO;
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
        viewModel.requestReservation(createReservationDTO());
        verify(reservationRepository, never()).loadReservations(any());

        // cancel reservation
        assertThat(viewModel.result, notNullValue());

        verify(reservationRepository, never()).createOrCancelReservation(anyBoolean(), any());
        viewModel.cancelReservation(createReservation());
        verify(reservationRepository, never()).createOrCancelReservation(anyBoolean(), any());
    }

    @Test
    public void sendResultToUI() {
        // Load reservations
        ReservationDTO reservationDTO = createReservationDTO();
        MutableLiveData<Resource<List<Reservation>>> list = new MutableLiveData<>();
        when(reservationRepository.loadReservations(reservationDTO)).thenReturn(list);
        Observer<Resource<List<Reservation>>> listObserver = mock(Observer.class);
        viewModel.reservations.observeForever(listObserver);
        viewModel.requestReservation(reservationDTO);
        verify(listObserver, never()).onChanged(any());

        List<Reservation> listData = new ArrayList<>();
        Resource<List<Reservation>> listResource = Resource.success(listData);
        list.setValue(listResource);
        verify(listObserver).onChanged(listResource);

        // Cancel reservation
        Reservation reservation = createReservation();
        MutableLiveData<Event<Resource<Result>>> result = new MutableLiveData<>();
        when(reservationRepository.createOrCancelReservation(false, reservation))
                .thenReturn(result);
        Observer<Event<Resource<Result>>> resultObserver = mock(Observer.class);
        viewModel.result.observeForever(resultObserver);
        viewModel.cancelReservation(reservation);
        verify(resultObserver, never()).onChanged(any());

        Event<Resource<Result>> resultResource = new Event<>(Resource.success(createResult()));
        result.setValue(resultResource);
        verify(resultObserver).onChanged(resultResource);
    }

    @Test
    public void loadReservations() {
        viewModel.reservations.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(reservationRepository);

        ReservationDTO reservationDTO = createReservationDTO();
        viewModel.requestReservation(reservationDTO);
        verify(reservationRepository).loadReservations(reservationDTO);
        verifyNoMoreInteractions(reservationRepository);
    }

    @Test
    public void cancelReservation() {
        viewModel.result.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(reservationRepository);

        Reservation reservation = createReservation();
        viewModel.cancelReservation(reservation);
        verify(reservationRepository).createOrCancelReservation(false, reservation);
        verifyNoMoreInteractions(reservationRepository);
    }

    @Test
    public void refresh() {
        ReservationDTO reservationDTO = createReservationDTO();
        viewModel.requestReservation(reservationDTO);
        verifyNoMoreInteractions(reservationRepository);
        viewModel.refresh();
        verifyNoMoreInteractions(reservationRepository);

        Observer<Resource<List<Reservation>>> userObserver = mock(Observer.class);
        viewModel.reservations.observeForever(userObserver);
        verify(reservationRepository).loadReservations(reservationDTO);
        reset(reservationRepository);

        viewModel.refresh();
        verify(reservationRepository).loadReservations(reservationDTO);
        reset(reservationRepository);
        viewModel.reservations.removeObserver(userObserver);

        viewModel.refresh();
        verifyNoMoreInteractions(reservationRepository);
    }
}