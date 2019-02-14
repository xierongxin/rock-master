package com.jy.rock.service;

import com.jy.rock.dao.AuditLogDao;
import com.jy.rock.domain.AuditLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hzhou
 */
@Service
public class AuditLogServiceImpl {

    @Autowired
    private AuditLogDao auditLogDao;

    public void add(AuditLog al) {
        this.auditLogDao.insert(al);
    }
}
