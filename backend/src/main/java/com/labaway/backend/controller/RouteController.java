package com.labaway.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RouteController {

    @RequestMapping(value = {
            "/{path:^(?!api|static|favicon\\.ico|.*\\..*$).*$}",
            "/**/{path:^(?!api|static|favicon\\.ico|.*\\..*$).*$}"
    })
    public String forward() {
        return "forward:/index.html";
    }
}