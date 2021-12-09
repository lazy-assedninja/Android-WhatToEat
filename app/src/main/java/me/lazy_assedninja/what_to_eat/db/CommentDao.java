package me.lazy_assedninja.what_to_eat.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import me.lazy_assedninja.what_to_eat.vo.Comment;

/**
 * Interface for database access on Comment related operations.
 */
@Dao
public interface CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Comment comment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Comment> list);

    @Query("SELECT * FROM comment WHERE storeID == :storeID")
    LiveData<List<Comment>> getComments(int storeID);

    @Query("SELECT id FROM comment ORDER BY id DESC LIMIT 1")
    int getLastCommentID();

    @Query("DELETE FROM comment WHERE storeID == :storeID")
    void deleteByStoreID(int storeID);
}