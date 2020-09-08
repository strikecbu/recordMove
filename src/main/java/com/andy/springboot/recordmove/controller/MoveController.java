package com.andy.springboot.recordmove.controller;

import com.andy.springboot.recordmove.service.MoveRecordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.io.File;
import java.util.List;

/**
 * @author AndyChen
 * @version <ul>
 * <li>2020/8/28 AndyChen,new
 * </ul>
 * @since 2020/8/28
 */
@Controller
public class MoveController {

    private final MoveRecordService moveRecordService;

    public MoveController(MoveRecordService moveRecordService) {
        this.moveRecordService = moveRecordService;
    }

//    @RequestMapping("move")
    public String moveRecord(Model model) {
        final List<File> files = moveRecordService.move();
        model.addAttribute("files", files);

        return "fileList";
    }

}
