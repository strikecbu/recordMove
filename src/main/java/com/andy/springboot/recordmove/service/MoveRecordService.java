package com.andy.springboot.recordmove.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author AndyChen
 * @version <ul>
 * <li>2020/8/28 AndyChen,new
 * </ul>
 * @since 2020/8/28
 */
public interface MoveRecordService {
    List<File> move();
    void clean() throws IOException;
}
