package ir.farhanizade.homeservice.repository.user;

import ir.farhanizade.homeservice.entity.user.User;
import ir.farhanizade.homeservice.repository.BaseRepository;

public interface UserRepository extends BaseRepository<User>, CustomUserRepository {

}
