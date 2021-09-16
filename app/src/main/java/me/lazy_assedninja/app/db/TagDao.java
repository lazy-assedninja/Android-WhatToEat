package me.lazy_assedninja.app.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import me.lazy_assedninja.app.vo.Tag;

/**
 * Interface for database access on Tag related operations.
 */
@Dao
public interface TagDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Tag tag);

    @Query("SELECT COUNT(id) FROM tag")
    int getTagSize();
}