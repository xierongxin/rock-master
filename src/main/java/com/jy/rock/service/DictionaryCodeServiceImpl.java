package com.jy.rock.service;

import com.jy.rock.dao.DictionaryCodeDao;
import com.jy.rock.domain.DictionaryCode;
import com.jy.rock.enums.Caches;
import com.xmgsd.lan.roadhog.mybatis.BaseService;
import com.xmgsd.lan.roadhog.mybatis.service.SimpleCurdViewService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * @author hzhou
 */
@Service
@Slf4j
public class DictionaryCodeServiceImpl extends BaseService<DictionaryCodeDao> implements SimpleCurdViewService<String, DictionaryCode> {

    @Autowired
    private CacheManager cacheManager;

    private Cache getCache() {
        Cache cache = this.cacheManager.getCache(Caches.DICTIONARY_CODE);
        if (cache == null) {
            throw new IllegalArgumentException("can not get cache: " + Caches.DICTIONARY_CODE);
        }
        return cache;
    }

    @PostConstruct
    private void init() {
        List<DictionaryCode> codes = this.getMapper().selectAll();
        Cache cache = this.getCache();
        for (DictionaryCode code : codes) {
            cache.put(code.getId(), code);
        }
    }

    @CachePut(cacheNames = Caches.DICTIONARY_CODE, key = "#item.id")
    @Override
    public DictionaryCode add(@NotNull DictionaryCode item) {
        this.getMapper().insert(item);
        return item;
    }

    @CacheEvict(cacheNames = Caches.DICTIONARY_CODE, key = "#id")
    @Override
    public void remove(@NotNull String id) {
        DictionaryCode code = this.getMapper().selectByPrimaryKey(id);
        if (code == null) {
            return;
        }

        if (!code.getEditable()) {
            throw new IllegalArgumentException("can not edit DictionaryCode: " + code.getName());
        }
        this.getMapper().deleteByPrimaryKey(id);
    }

    @CachePut(cacheNames = Caches.DICTIONARY_CODE, key = "#id")
    @Override
    public DictionaryCode update(@NotNull String id, @NotNull DictionaryCode item) {
        DictionaryCode code = this.getMapper().selectByPrimaryKey(id);
        if (!code.getEditable()) {
            throw new IllegalArgumentException("can not edit DictionaryCode: " + code.getName());
        }
        code.update(item);
        this.getMapper().updateByPrimaryKey(item);
        return code;
    }

    @Cacheable(cacheNames = Caches.DICTIONARY_CODE, key = "#id")
    @NotNull
    @Override
    public DictionaryCode get(@NotNull String id) throws IllegalArgumentException {
        DictionaryCode code = this.getMapper().selectByPrimaryKey(id);
        if (code == null) {
            throw new IllegalArgumentException("no item with id: " + id);
        }
        return code;
    }

    @Override
    public List<DictionaryCode> list() {
        CaffeineCache c = (CaffeineCache) this.getCache();
        ConcurrentMap<Object, Object> map = c.getNativeCache().asMap();
        List<DictionaryCode> result = new ArrayList<>(map.size());
        map.forEach((k, v) -> result.add((DictionaryCode) v));
        return result;
    }
}
