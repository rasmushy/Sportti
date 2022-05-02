package fi.sportti.app.datastorage.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

/**
 * Data access object for User entity (this app only has one user)
 *
 * @author Rasmus Hyyppä
 * @version 1.0.0
 */

@Dao
public interface UserDao {

    @Query("SELECT * FROM user_data ORDER BY uid ASC LIMIT 1")
    User getFirstUser();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateUser(User updateUser);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUser(User newUser);
}
