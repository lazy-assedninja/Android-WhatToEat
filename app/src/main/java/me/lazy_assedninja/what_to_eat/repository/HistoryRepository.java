package me.lazy_assedninja.what_to_eat.repository;

import androidx.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import me.lazy_assedninja.what_to_eat.db.HistoryDao;
import me.lazy_assedninja.what_to_eat.vo.History;
import me.lazy_assedninja.what_to_eat.vo.Store;
import me.lazy_assedninja.library.util.ExecutorUtil;

public class HistoryRepository {

    private final ExecutorUtil executorUtil;
    private final HistoryDao historyDao;

    @Inject
    public HistoryRepository(ExecutorUtil executorUtil, HistoryDao historyDao) {
        this.executorUtil = executorUtil;
        this.historyDao = historyDao;
    }

    public void addToHistory(History history) {
        executorUtil.diskIO().execute(() -> historyDao.insert(history));
    }

    public List<Integer> getHistoryIDs() {
        return historyDao.getHistoryIDs();
    }

    public LiveData<List<Store>> loadHistories(List<Integer> ids) {
        return historyDao.getHistoryStores(ids);
    }

    public void deleteAll() {
        executorUtil.diskIO().execute(historyDao::deleteAll);
    }
}