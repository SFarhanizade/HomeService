package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.entity.user.UserAdmin;
import ir.farhanizade.homeservice.repository.BaseRepository;

public interface AdminRepository extends BaseRepository<UserAdmin> {
    UserAdmin findByEmail(String email);
}
