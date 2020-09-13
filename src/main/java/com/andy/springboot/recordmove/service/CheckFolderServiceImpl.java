package com.andy.springboot.recordmove.service;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CheckFolderServiceImpl implements CheckFolderService {

    private Logger logger = LogManager.getLogger(getClass());

    @Value("${keepDateOfData}")
    private String keepDateOfData = "30";

    @Value("${nas.path}")
    private String nasPath;

    public CheckFolderServiceImpl(@Value("${keepDateOfData}")String keepDateOfData,@Value("${nas.path}") String nasPath) {
        this.keepDateOfData = keepDateOfData;
        this.nasPath = nasPath;
    }

    @Override
    public void removeOutDateFolder() {
        logger.info("System set clean days before: {}", keepDateOfData);
        String folderDatePattern = "yyyy-MM-dd";
        String folderPattern = "^\\d{4}-\\d{2}-\\d{2}$";
        File nasFolder = new File(nasPath);
        SimpleDateFormat dateFormat = new SimpleDateFormat(folderDatePattern);
        Calendar checkDate = Calendar.getInstance(TimeZone.getTimeZone("Asia/Taipei"));

        checkDate.add(Calendar.DATE, -1 * Integer.parseInt(keepDateOfData));

        logger.info("Delete {} before folder.", dateFormat.format(new Date(checkDate.getTimeInMillis())));
        Arrays.stream(Objects.requireNonNull(nasFolder.listFiles())).filter(file -> {
            if (!file.isDirectory()) {
                return false;
            }
            String folderName = file.getName();
            return folderName.matches(folderPattern);
        }).forEach(folder -> {
            String folderName = folder.getName();
            logger.info("Check folder: {}", folderName);
            try {
                Date folderDate = dateFormat.parse(folderName);
                if(folderDate.getTime() < checkDate.getTimeInMillis()) {
                    FileUtils.deleteDirectory(folder);
                    logger.info("Folder {} is over {} days, delete complete.", folderName, keepDateOfData);
                }
            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
        });
    }
}
