package com.lab.vm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * packageName : com.jlab.vm.controller
 * fileName : HomeController
 * author : isbn8
 * date : 2022-01-13
 * description :
 * ===========================================================
 * DATE          AUTHOR          NOTE
 * -----------------------------------------------------------
 * 2022-01-13       isbn8         최초 생성
 */

@Controller
public class HomeController {
    @RequestMapping("/")
    public String home(){
        return "index";
    }

}
