package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.entity.user.User;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends BaseRepository<User> {

    @Query("From User u where u.id=:id and u.password=:currentPassword")
    User isCorrectByPassword(Long id, String currentPassword);

    @Modifying
    @Query("Update User u set u.password=:newPassword where u.id=:id")
    int updatePassword(Long id, String newPassword);
}
