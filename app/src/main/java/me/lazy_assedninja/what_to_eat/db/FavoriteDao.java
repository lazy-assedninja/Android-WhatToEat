package me.lazy_assedninja.what_to_eat.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import me.lazy_assedninja.what_to_eat.vo.Store;

/**
 * Interface for database access on Favorite related operations.
 */
@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM store WHERE isFavorite == :isFavorite")
    LiveData<List<Store>> getFavorites(boolean isFavorite);

    @Query("UPDATE store SET isFavorite = :status WHERE id = :storeID")
    Integer updateFavoriteStatus(int storeID, boolean status);

    @Query("UPDATE store SET isFavorite = :status, updateTime = :updateTime WHERE id = :storeID")
    Integer updateFavoriteStatusAndTime(int storeID, boolean status, String updateTime);
}