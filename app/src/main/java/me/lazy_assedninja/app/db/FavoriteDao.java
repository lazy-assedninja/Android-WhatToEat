package me.lazy_assedninja.app.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import me.lazy_assedninja.app.vo.Store;

/**
 * Interface for database access on Favorite related operations.
 */
@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM store WHERE isFavorite == :isFavorite")
    LiveData<List<Store>> getFavorites(boolean isFavorite);

    @Query("UPDATE store SET updateTime = :dateTime, isFavorite = :status WHERE id = :storeID")
    void updateFavoriteStatus(int storeID, boolean status, String dateTime);
}