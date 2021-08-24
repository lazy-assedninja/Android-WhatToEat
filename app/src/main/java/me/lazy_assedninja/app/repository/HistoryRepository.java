package me.lazy_assedninja.app.repository;

import androidx.lifecycle.LiveData;

import java.util.List;

import me.lazy_assedninja.app.db.HistoryDao;
import me.lazy_assedninja.app.vo.History;
import me.lazy_assedninja.app.vo.Store;
import me.lazy_assedninja.library.utils.ExecutorUtils;

public class HistoryRepository {

    private final ExecutorUtils executorUtils;
    private final HistoryDao historyDao;

    public HistoryRepository(ExecutorUtils executorUtils, HistoryDao historyDao) {
        this.executorUtils = executorUtils;
        this.historyDao = historyDao;
    }

    public void addHistory(int storeID) {
        executorUtils.diskIO().execute(() -> historyDao.insert(new History(storeID)));
    }

    public LiveData<List<Integer>> getHistoryIDs() {
        return historyDao.getHistoryIDs();
    }

    public LiveData<List<Store>> loadHistories(List<Integer> ids) {
        return historyDao.getHistoryStores(ids);
    }

    public void deleteAll() {
        executorUtils.diskIO().execute(historyDao::deleteAll);
    }
}