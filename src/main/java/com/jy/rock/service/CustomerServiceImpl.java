package com.jy.rock.service;

import com.jy.rock.bean.customer.CustomerQueryVO;
import com.jy.rock.dao.CustomerDao;
import com.jy.rock.domain.Customer;
import com.xmgsd.lan.roadhog.mybatis.BaseService;
import com.xmgsd.lan.roadhog.mybatis.mappers.BasePaginationMapper;
import com.xmgsd.lan.roadhog.mybatis.service.PaginationService;
import com.xmgsd.lan.roadhog.mybatis.service.SimpleCurdViewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author hzhou
 */
@Service
@Slf4j
public class CustomerServiceImpl extends BaseService<CustomerDao>
        implements SimpleCurdViewService<String, Customer>, PaginationService<CustomerQueryVO, Customer> {

    @Override
    public BasePaginationMapper<Customer> getPaginationMapper() {
        return this.getMapper();
    }
}
