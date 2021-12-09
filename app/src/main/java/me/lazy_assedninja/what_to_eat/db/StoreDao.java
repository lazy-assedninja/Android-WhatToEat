package me.lazy_assedninja.what_to_eat.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import me.lazy_assedninja.what_to_eat.vo.Store;

/**
 * Interface for database access on Store related operations.
 */
@Dao
public interface StoreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Store> list);

    @Query("SELECT * FROM store WHERE id == :id")
    LiveData<Store> get(int id);

    @Query("SELECT * FROM store WHERE name == :name")
    LiveData<Store> get(String name);

    @Query("SELECT * FROM store WHERE tagID == :tagID")
    LiveData<List<Store>> getStores(int tagID);

    @Query("SELECT * FROM store")
    LiveData<List<Store>> getStores();

    @Query("SELECT * FROM store WHERE name LIKE :keyword")
    LiveData<List<Store>> search(String keyword);
}