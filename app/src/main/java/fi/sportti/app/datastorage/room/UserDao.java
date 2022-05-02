package fi.sportti.app.datastorage.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data access object for User entity (this app only has one user)
 *
 * @author Rasmus Hyyppä
 * @version 0.5
 */

@Dao
public interface UserDao {

    @Query("SELECT * FROM user_data")
    LiveData<List<User>> getAllUsers();

    @Query("SELECT * FROM user_data ORDER BY uid ASC LIMIT 1")
    User getFirstUser();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateUser(User updateUser);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUser(User newUser);
}
