package ir.farhanizade.homeservice.security.user;

import ir.farhanizade.homeservice.exception.UUIDNotFoundException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserUUID {
    private static ConcurrentHashMap<String, Long> userUUID = new ConcurrentHashMap<>();

    public static Long getIdByUUID(String uuid) throws UUIDNotFoundException {
        Long id = userUUID.get(uuid);
        if (id == null) throw new UUIDNotFoundException("UUID Not Found!");
        removeUUID(uuid);
        return id;
    }

    public static String createUUID(Long id) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest salt = MessageDigest.getInstance("SHA-256");
        salt.update(UUID.randomUUID().toString().getBytes("UTF-8"));
        String uuid = bytesToHex(salt.digest());
        userUUID.put(uuid, id);
        return uuid;
    }

    private static void removeUUID(String uuid) {
        userUUID.remove(uuid);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte num : bytes) {
            char[] hexDigits = new char[2];
            hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
            hexDigits[1] = Character.forDigit((num & 0xF), 16);
            result.append(new String(hexDigits));
        }
        return result.toString();
    }
}
