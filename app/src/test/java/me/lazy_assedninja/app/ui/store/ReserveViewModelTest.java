package me.lazy_assedninja.app.ui.store;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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

import me.lazy_assedninja.app.repository.ReservationRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.ui.store.reservation.reserve.ReserveViewModel;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Reservation;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class ReserveViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final UserRepository userRepository = mock(UserRepository.class);
    private final ReservationRepository reservationRepository = mock(ReservationRepository.class);
    private final ReserveViewModel viewModel = new ReserveViewModel(userRepository,
            reservationRepository);

    @Test
    public void testNull() {
        assertThat(viewModel.result, notNullValue());

        verify(reservationRepository, never()).createOrCancelReservation(anyBoolean(), any());
        viewModel.reserve(createReservation());
        verify(reservationRepository, never()).createOrCancelReservation(anyBoolean(), any());
    }

    @Test
    public void sendResultToUI() {
        Reservation reservation = createReservation();
        MutableLiveData<Event<Resource<Result>>> result = new MutableLiveData<>();
        when(reservationRepository.createOrCancelReservation(true, reservation))
                .thenReturn(result);
        Observer<Event<Resource<Result>>> observer = mock(Observer.class);
        viewModel.result.observeForever(observer);
        viewModel.reserve(reservation);
        verify(observer, never()).onChanged(any());

        Event<Resource<Result>> resource = new Event<>(Resource.success(createResult()));
        result.setValue(resource);
        verify(observer).onChanged(resource);
    }

    @Test
    public void reserve() {
        viewModel.result.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(reservationRepository);

        Reservation reservation = createReservation();
        viewModel.reserve(reservation);
        verify(reservationRepository).createOrCancelReservation(true, reservation);
        verifyNoMoreInteractions(reservationRepository);
    }
}