package me.lazy_assedninja.app.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import me.lazy_assedninja.app.vo.Promotion;
import me.lazy_assedninja.app.vo.Store;

@Dao
public interface PromotionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Promotion> list);

    @Query("SELECT * FROM promotion WHERE id == :id")
    LiveData<Promotion> getPromotion(int id);

    @Query("SELECT * FROM promotion")
    LiveData<List<Promotion>> getPromotions();
}