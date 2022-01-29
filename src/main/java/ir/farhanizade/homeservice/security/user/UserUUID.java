package ir.farhanizade.homeservice.security.user;

import ir.farhanizade.homeservice.exception.UUIDNotFoundException;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserUUID {
    private static ConcurrentHashMap<UUID,Long> userUUID = new ConcurrentHashMap<>();

    public static Long getIdByUUID(UUID uuid) throws UUIDNotFoundException {
        Long id = userUUID.get(uuid);
        if (id==null) throw new UUIDNotFoundException("UUID Not Found!");
        return id;
    }

    public static UUID createUUID(Long id){
        UUID uuid = UUID.randomUUID();
        userUUID.put(uuid,id);
        return uuid;
    }
}
