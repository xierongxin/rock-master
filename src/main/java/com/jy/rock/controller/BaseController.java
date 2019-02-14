package com.jy.rock.controller;

import com.xmgsd.lan.roadhog.mybatis.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hzhou
 */
public abstract class BaseController<S extends MyService> {

    @Autowired
    private S service;

    public S getService() {
        return this.service;
    }
}
