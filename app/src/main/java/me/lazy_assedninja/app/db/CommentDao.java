package me.lazy_assedninja.app.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import me.lazy_assedninja.app.vo.Comment;

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

    @Query("DELETE FROM comment WHERE storeID == :storeID")
    void deleteByStoreID(int storeID);

    @Query("SELECT COUNT(id) FROM comment")
    int getCommentSize();
}