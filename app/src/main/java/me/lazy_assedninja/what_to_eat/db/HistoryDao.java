package me.lazy_assedninja.what_to_eat.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import me.lazy_assedninja.what_to_eat.vo.History;
import me.lazy_assedninja.what_to_eat.vo.Store;

/**
 * Interface for database access on History related operations.
 */
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