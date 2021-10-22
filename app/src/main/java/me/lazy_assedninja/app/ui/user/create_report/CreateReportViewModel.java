package me.lazy_assedninja.app.ui.user.create_report;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.app.repository.CustomServiceRepository;
import me.lazy_assedninja.app.repository.UserRepository;
import me.lazy_assedninja.app.utils.AbsentLiveData;
import me.lazy_assedninja.app.vo.Event;
import me.lazy_assedninja.app.vo.Report;
import me.lazy_assedninja.app.vo.Resource;
import me.lazy_assedninja.app.vo.Result;
import me.lazy_assedninja.library.utils.TimeUtils;

@HiltViewModel
public class CreateReportViewModel extends ViewModel {

    private final TimeUtils timeUtils;
    private final UserRepository userRepository;
    private CustomServiceRepository customServiceRepository;

    private final MutableLiveData<Report> createReport = new MutableLiveData<>();

    private Integer id;

    @Inject
    public CreateReportViewModel(TimeUtils timeUtils, UserRepository userRepository,
                                 CustomServiceRepository customServiceRepository) {
        this.timeUtils = timeUtils;
        this.userRepository = userRepository;
        this.customServiceRepository = customServiceRepository;
    }

    public int getUserID() {
        return userRepository.getUserID();
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LiveData<Event<Resource<Result>>> result = Transformations.switchMap(createReport, report -> {
        if (report == null) {
            return AbsentLiveData.create();
        } else {
            return customServiceRepository.createReport(report);
        }
    });

    public void createReport(String content) {
        createReport.setValue(new Report(content, timeUtils.now(), id, userRepository.getUserID()));
    }
}