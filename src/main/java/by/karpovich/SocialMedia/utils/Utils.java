package by.karpovich.SocialMedia.utils;

import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Utils {

    public static final String UPLOAD_PATH = "D://image//poster";
    public static final String DATE_STRING = "dd MMMM yyyy HH:mm";

    public static String saveFile(MultipartFile file) {
        if (file == null) {
            return null;
        }

        Path uploadPath = Paths.get(UPLOAD_PATH);

        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("Incorrect path");
            }
        }

        String uuidFile = UUID.randomUUID().toString();
        String name = uuidFile + "-" + file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(name);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Incorrect path");
        }

        return name;
    }

    public static byte[] getImageAsResponseEntity(String fileName) {
        if (fileName == null) {
            return null;
        }
        String dirPath = UPLOAD_PATH + "//";
        InputStream in = null;
        byte[] media = new byte[0];
        try {
            in = new FileInputStream(dirPath + fileName);
            media = IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw new RuntimeException("Incorrect path");
        }
        return media;
    }

    public static String mapStringFromInstant(Instant instant) {
        if (instant == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_STRING)
                .withZone(ZoneId.systemDefault());

        Instant date = Instant.parse(instant.toString());

        return formatter.format(date);
    }
}
