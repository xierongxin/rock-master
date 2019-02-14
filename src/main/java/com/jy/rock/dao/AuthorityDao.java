package com.jy.rock.dao;

import com.jy.rock.domain.Authority;
import com.xmgsd.lan.roadhog.mybatis.mappers.CurdMapper;
import org.springframework.stereotype.Repository;

/**
 * @author hzhou
 */
@Repository
public interface AuthorityDao extends CurdMapper<Authority, String> {
}
