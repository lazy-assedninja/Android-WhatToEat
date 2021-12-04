package me.lazy_assedninja.app.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import me.lazy_assedninja.app.vo.Post;

/**
 * Interface for database access on Post related operations.
 */
@Dao
public interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Post> list);

    @Query("SELECT * FROM post WHERE storeID == :storeID")
    LiveData<List<Post>> getPosts(int storeID);

    @Query("DELETE FROM post WHERE storeID == :storeID")
    void deleteByStoreID(int storeID);
}