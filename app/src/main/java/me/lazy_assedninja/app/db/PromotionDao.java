package me.lazy_assedninja.app.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import me.lazy_assedninja.app.vo.Promotion;

/**
 * Interface for database access on Promotion related operations.
 */
@Dao
public interface PromotionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Promotion> list);

    @Query("SELECT * FROM promotion WHERE id == :id")
    LiveData<Promotion> get(int id);

    @Query("SELECT * FROM promotion")
    LiveData<List<Promotion>> getPromotions();

    @Query("DELETE FROM promotion")
    void delete();
}