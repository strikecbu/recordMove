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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author AndyChen
 * @version <ul>
 * <li>2020/8/28 AndyChen,new
 * </ul>
 * @since 2020/8/28
 */

@Service
public class MoveRecordServiceImpl implements MoveRecordService {

    Logger logger = LogManager.getLogger(getClass());

    @Value("${record.path}")
    private String recordPath;

    @Value("${record.format}")
    private String recordFormat;

    @Value("${nas.path}")
    private String nasPath;

    @Override
    public List<File> move() {
        String s = "";
        final File folder = new File(recordPath);
        final List<File> allFiles = getAllFiles(folder);
        logger.info("Total {} files needs to move.", allFiles.size());
        final List<File> collect = allFiles.stream()
                .sorted((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()))
                .collect(Collectors.toList());
        collect.stream().parallel()
                .forEach(file -> {
                    final String fileName = file.getName();
                    final Matcher matcher = Pattern.compile(recordFormat).matcher(fileName);
                    if (matcher.find()) {
                        final String date = matcher.group(1);
                        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd-HHmmss");
                        final SimpleDateFormat folderFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            final Date parse = dateFormat.parse(date);
                            final String folderName = folderFormat.format(parse);
                            File nasFolder = new File(nasPath, folderName);
                            FileUtils.copyFileToDirectory(file, nasFolder);
                            logger.info("File : {}, from: {}, to {}", file.getName(), file.getAbsolutePath(), nasFolder.getAbsolutePath());
                        } catch (ParseException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        logger.info("All files move completed.");
        return collect;
    }

    private List<File> getAllFiles(File folder) {
        List<File> list = new ArrayList<>();
        if (folder.isDirectory()) {
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                if (file.isDirectory()) {
                    list.addAll(getAllFiles(file));
                } else {
                    list.add(file);
                }
            }
        } else {
            list.add(folder);
        }
        return list;
    }

    @Override
    public void clean() throws IOException {
        for (File file : Objects.requireNonNull(new File(recordPath).listFiles())) {
            final String fileName = file.getName();
            final Matcher matcher = Pattern.compile(recordFormat).matcher(fileName);
            if (matcher.find()) {
                FileUtils.forceDelete(file);
                logger.info("Remove file: {}", fileName);
            }
        }
    }
}
