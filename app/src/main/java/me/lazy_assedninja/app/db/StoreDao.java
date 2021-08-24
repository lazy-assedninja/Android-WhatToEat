package me.lazy_assedninja.app.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import me.lazy_assedninja.app.vo.Store;

@Dao
public interface StoreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Store> list);

    @Query("SELECT * FROM store WHERE id == :id")
    LiveData<Store> getStore(int id);

    @Query("SELECT * FROM store WHERE tagID == :tagID")
    LiveData<List<Store>> getStores(int tagID);
}