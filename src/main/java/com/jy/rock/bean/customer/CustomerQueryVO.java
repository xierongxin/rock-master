package com.jy.rock.bean.customer;

import com.xmgsd.lan.roadhog.bean.BasePaginationQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author hzhou
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CustomerQueryVO extends BasePaginationQuery {

    private String name;

    private String tel;

    private String mobile;

    private String address;
}
