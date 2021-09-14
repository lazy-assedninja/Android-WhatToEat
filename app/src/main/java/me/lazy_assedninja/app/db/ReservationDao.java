package me.lazy_assedninja.app.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import me.lazy_assedninja.app.vo.Reservation;

/**
 * Interface for database access on Reservation related operations.
 */
@Dao
public interface ReservationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Reservation reservation);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Reservation> list);

    @Query("SELECT * FROM reservation")
    LiveData<List<Reservation>> getReservations();

    @Delete
    void delete(Reservation reservation);

    @Query("DELETE FROM reservation")
    void delete();
}