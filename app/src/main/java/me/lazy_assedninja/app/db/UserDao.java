package me.lazy_assedninja.app.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import me.lazy_assedninja.app.vo.User;

/**
 * Interface for database access on User related operations.
 */
@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Query("SELECT * FROM user LIMIT 1")
    LiveData<User> get();

    @Query("UPDATE user SET password = :password, updateTime = :updateTime")
    void updatePassword(String password, String updateTime);

    @Query("DELETE FROM user")
    void delete();
}