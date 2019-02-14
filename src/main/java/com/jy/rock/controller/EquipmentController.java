package com.jy.rock.controller;

import com.jy.rock.service.EquipmentServiceImpl;
import com.xmgsd.lan.roadhog.web.controller.CurdController;
import com.xmgsd.lan.roadhog.web.controller.PaginationController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hzhou
 */
@RestController
@RequestMapping("/equipment")
public class EquipmentController extends BaseController<EquipmentServiceImpl>
        implements CurdController<EquipmentServiceImpl>, PaginationController<EquipmentServiceImpl> {
}
