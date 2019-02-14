package com.jy.rock.controller;

import com.jy.rock.service.DictionaryCodeServiceImpl;
import com.xmgsd.lan.roadhog.web.controller.SimpleCurdViewController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hzhou
 */
@RestController
@RequestMapping("/dictionary_code")
public class DictionaryCodeController extends BaseController<DictionaryCodeServiceImpl> implements SimpleCurdViewController<DictionaryCodeServiceImpl> {

}
