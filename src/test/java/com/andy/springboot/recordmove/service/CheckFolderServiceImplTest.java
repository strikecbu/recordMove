package com.andy.springboot.recordmove.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;


class CheckFolderServiceImplTest {

    @Test
    void removeOutDateFolder() throws IOException {
        File resource = ResourceUtils.getFile("classpath:removeTestFolder");
        int testDays = 5;
        String testFolder = resource.getPath();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("Asia/Taipei"));
        File notDeleteFolder = new File(resource, format.format(new Date(instance.getTimeInMillis())));
        instance.add(Calendar.DATE, -1 * (testDays));
        File deleteFolder1 = new File(resource, format.format(new Date(instance.getTimeInMillis())));
        instance.add(Calendar.DATE, -1);
        File deleteFolder2 = new File(resource, format.format(new Date(instance.getTimeInMillis())));
        notDeleteFolder.mkdirs();
        deleteFolder1.mkdirs();
        deleteFolder2.mkdirs();
        CheckFolderService service = new CheckFolderServiceImpl(String.valueOf(testDays), testFolder);
        service.removeOutDateFolder();

        File[] files = Objects.requireNonNull(resource.listFiles(file -> file.getName().matches("^\\d{4}-\\d{2}-\\d{2}$")));
        Assert.assertEquals(1, files.length);
    }
}