package me.lazy_assedninja.app.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import me.lazy_assedninja.app.vo.History;
import me.lazy_assedninja.app.vo.Store;

@Dao
public interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(History history);

    @Query("SELECT storeID FROM history")
    LiveData<List<Integer>> getHistoryIDs();

    @Query("SELECT * FROM store WHERE id IN (:ids)")
    LiveData<List<Store>> getHistoryStores(List<Integer> ids);

    @Query("DELETE FROM history")
    void deleteAll();
}