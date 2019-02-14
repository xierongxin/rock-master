package com.jy.rock.dao;

import com.jy.rock.bean.customer.CustomerQueryVO;
import com.jy.rock.domain.Customer;
import com.xmgsd.lan.roadhog.mybatis.mappers.CurdMapper;
import com.xmgsd.lan.roadhog.mybatis.mappers.PaginationMapper;import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hzhou
 */
public interface CustomerDao extends CurdMapper<Customer, String>, PaginationMapper<Customer, CustomerQueryVO> {

    @Override
    List<Customer> pagination(@Param("query")CustomerQueryVO query);
}
