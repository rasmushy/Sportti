package fi.sportti.app.datastorage.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/*
 * @author rasmushy
 * Dao for User entities
 */

@Dao
public interface UserDao {

    @Query("SELECT * FROM user_data")
    LiveData<List<User>> getAllUsers();

    @Query("SELECT * FROM user_data WHERE uid LIKE :userId LIMIT 1")
    LiveData<User> findByUserId(int userId);

    @Query("SELECT * FROM user_data WHERE username LIKE :userName LIMIT 1")
    User findByName(String userName);

    @Query("SELECT * FROM user_data ORDER BY uid ASC LIMIT 1")
    User getFirstUser();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateUser(User updateUser);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUser(User newUser);

    @Delete
    void deleteUser(User user);

    @Delete
    void deleteListOfUsers(List<User> userList);
}
