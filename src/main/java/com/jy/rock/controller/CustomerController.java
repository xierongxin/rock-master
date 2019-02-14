package com.jy.rock.controller;

import com.jy.rock.service.CustomerServiceImpl;
import com.xmgsd.lan.roadhog.web.controller.CurdController;
import com.xmgsd.lan.roadhog.web.controller.PaginationController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hzhou
 */
@RestController
@RequestMapping("/customer")
public class CustomerController extends BaseController<CustomerServiceImpl>
        implements CurdController<CustomerServiceImpl>, PaginationController<CustomerServiceImpl> {

}
