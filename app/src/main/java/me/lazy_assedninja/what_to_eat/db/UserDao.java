package me.lazy_assedninja.what_to_eat.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import me.lazy_assedninja.what_to_eat.vo.User;

/**
 * Interface for database access on User related operations.
 */
@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Query("SELECT * FROM user LIMIT 1")
    LiveData<User> get();

    @Query("SELECT * FROM user LIMIT 1")
    User getUser();

    @Query("UPDATE user SET google_googleID = :googleID, updateTime = :updateTime")
    int updateGoogleID(String googleID, String updateTime);

    @Query("UPDATE user SET password = :password, updateTime = :updateTime")
    int updatePassword(String password, String updateTime);

    @Query("UPDATE user SET updateTime = :updateTime")
    int updateFile(String updateTime);

    @Query("DELETE FROM user")
    void delete();
}