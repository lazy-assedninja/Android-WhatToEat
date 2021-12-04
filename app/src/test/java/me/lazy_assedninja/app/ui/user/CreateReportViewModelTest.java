package me.lazy_assedninja.app.ui.user;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import static me.lazy_assedninja.app.common.TestUtil.createResult;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.lazy_assedninja.app.repository.CustomServiceRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.ui.user.create_report.CreateReportViewModel;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.library.util.TimeUtil;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class CreateReportViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final TimeUtil timeUtil = mock(TimeUtil.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CustomServiceRepository customServiceRepository =
            mock(CustomServiceRepository.class);
    private final CreateReportViewModel viewModel = new CreateReportViewModel(timeUtil,
            userRepository, customServiceRepository);

    @Test
    public void testNull() {
        assertThat(viewModel.result, notNullValue());

        verify(customServiceRepository, never()).createReport(any());
        viewModel.createReport("content");
        verify(customServiceRepository, never()).createReport(any());
    }

    @Test
    public void sendResultToUI() {
        MutableLiveData<Event<Resource<Result>>> result = new MutableLiveData<>();
        when(customServiceRepository.createReport(any())).thenReturn(result);
        Observer<Event<Resource<Result>>> resultObserver = mock(Observer.class);
        viewModel.result.observeForever(resultObserver);
        viewModel.createReport("content");
        verify(resultObserver, never()).onChanged(any());
        Event<Resource<Result>> resultResource = new Event<>(Resource.success(createResult()));

        result.setValue(resultResource);
        verify(resultObserver).onChanged(resultResource);
    }

    @Test
    public void createReport() {
        viewModel.result.observeForever(mock(Observer.class));
        verifyNoMoreInteractions(customServiceRepository);
        viewModel.createReport("content");
        verify(customServiceRepository).createReport(any());
        verifyNoMoreInteractions(customServiceRepository);
    }

    @Test
    public void getUserID() {
        viewModel.getUserID();

        verify(userRepository).getUserID();
    }
}