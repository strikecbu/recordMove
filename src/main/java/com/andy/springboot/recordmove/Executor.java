package com.andy.springboot.recordmove;

import com.andy.springboot.recordmove.service.CheckFolderService;
import com.andy.springboot.recordmove.service.MoveRecordService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author AndyChen
 * @version <ul>
 * <li>2020/9/8 AndyChen,new
 * </ul>
 * @since 2020/9/8
 */
@Component
public class Executor implements CommandLineRunner {

    Logger logger = LogManager.getLogger(getClass());

    @Value("${record.path}")
    private String recordPath;

    @Value("${record.format}")
    private String recordFormat;

    @Value("${nas.path}")
    private String nasPath;

    final private MoveRecordService moveRecordService;

    final private CheckFolderService checkFolderService;

    public Executor(MoveRecordService moveRecordService, CheckFolderService checkFolderService) {
        this.moveRecordService = moveRecordService;
        this.checkFolderService = checkFolderService;
    }

    @Override
    public void run(String... args) throws Exception {
        if(checkPathNotFound(recordPath) || checkPathNotFound(nasPath)) {
            logger.info("Shutdown." );
            return;
        }
        final long start = System.currentTimeMillis();
        moveRecordService.move();
        moveRecordService.clean();
        checkFolderService.removeOutDateFolder();
        final long done = System.currentTimeMillis();
        logger.info("Total cause time: {} sec", (done - start) / 1000);
    }

    private boolean checkPathNotFound(String path) {
        if (!new File(path).exists()) {
            logger.info("Path is not exist: {}", path);
            return true;
        }
        return false;
    }
}
