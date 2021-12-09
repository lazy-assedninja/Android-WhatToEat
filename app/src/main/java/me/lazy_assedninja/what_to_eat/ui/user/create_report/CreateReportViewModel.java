package me.lazy_assedninja.what_to_eat.ui.user.create_report;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import me.lazy_assedninja.what_to_eat.repository.CustomServiceRepository;
import me.lazy_assedninja.what_to_eat.repository.UserRepository;
import me.lazy_assedninja.what_to_eat.util.AbsentLiveData;
import me.lazy_assedninja.what_to_eat.vo.Event;
import me.lazy_assedninja.what_to_eat.vo.Report;
import me.lazy_assedninja.what_to_eat.vo.Resource;
import me.lazy_assedninja.what_to_eat.vo.Result;
import me.lazy_assedninja.library.util.TimeUtil;

@HiltViewModel
public class CreateReportViewModel extends ViewModel {

    private final TimeUtil timeUtil;
    private final UserRepository userRepository;
    private CustomServiceRepository customServiceRepository;

    private final MutableLiveData<Report> createReport = new MutableLiveData<>();

    private Integer id;

    @Inject
    public CreateReportViewModel(TimeUtil timeUtil, UserRepository userRepository,
                                 CustomServiceRepository customServiceRepository) {
        this.timeUtil = timeUtil;
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

    public void createReport(Report report) {
        report.setCreateTime(timeUtil.now());
        report.setStoreID(id);
        report.setUserID(userRepository.getUserID());
        createReport.setValue(report);
    }
}