package com.jy.rock.core.audit;

import com.google.common.reflect.TypeToken;
import com.jy.rock.domain.AuditLog;
import com.xmgsd.lan.roadhog.bean.BasePaginationQuery;
import com.xmgsd.lan.roadhog.bean.PaginationResultVO;
import com.xmgsd.lan.roadhog.bean.SimpleResponseVO;
import com.xmgsd.lan.roadhog.mybatis.BaseDomain;
import com.xmgsd.lan.roadhog.mybatis.service.CurdService;
import com.xmgsd.lan.roadhog.mybatis.service.PaginationService;
import com.xmgsd.lan.roadhog.mybatis.service.ViewService;
import com.xmgsd.lan.roadhog.utils.JSON;
import com.xmgsd.lan.roadhog.web.controller.MyController;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.instrument.IllegalClassFormatException;
import java.text.MessageFormat;
import java.util.List;

/**
 * 继承这个类会给Controller增加审计功能，默认不记录 list 和 pagination 的审计日志
 *
 * @author hzhou
 */
@SuppressWarnings("unchecked")
public abstract class AbstractAuditCurdController<S extends CurdService> implements MyController<S> {

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    protected S service;

    private TypeToken<S> sTypeToken = new TypeToken<S>(this.getClass()) {
    };

    @Override
    public S getService() {
        return this.service;
    }

    /**
     * 获取设计日志中的“数据条目的Id”
     *
     * @param item 数据条目
     * @return 数据条目名称
     */
    protected String getItemId(@NotNull Object item) {
        if (item instanceof BaseDomain) {
            return ((BaseDomain) item).getId().toString();
        }

        throw new IllegalArgumentException("can not auto determine item id property, please override method: getItemId");
    }

    @PostMapping("")
    @AuditMethod(name = "新增")
    public SimpleResponseVO insert(@NotNull String payload, AuditLog al) throws Exception {
        TypeToken<?> iTypeToken = sTypeToken.resolveType(CurdService.class.getTypeParameters()[1]);
        Class insertType = (Class) iTypeToken.getType();
        Object item = JSON.deserialize(payload, insertType);
        this.getService().add(item);
        al.setDetails(this.getItemId(item));
        return new SimpleResponseVO(true);
    }

    @PutMapping("/{id}")
    @AuditMethod(name = "修改")
    public SimpleResponseVO update(@PathVariable("id") String id, @NotNull String payload, AuditLog al) throws Exception {
        TypeToken<?> uTypeToken = sTypeToken.resolveType(CurdService.class.getTypeParameters()[2]);
        Class updateType = (Class) uTypeToken.getType();
        Object item = JSON.deserialize(payload, updateType);

        this.getService().update(id, item);
        al.setDetails(this.getItemId(item));
        return new SimpleResponseVO(true);
    }

    @DeleteMapping("/{id}")
    @AuditMethod(name = "删除")
    public SimpleResponseVO remove(@PathVariable("id") String id, AuditLog al) throws Exception {
        Object item = this.getService().get(id);
        this.getService().remove(id);
        al.setMethodName(this.getItemId(item));
        return new SimpleResponseVO(true);
    }

    @GetMapping("/{id}")
    @AuditMethod(name = "查看")
    public Object get(@PathVariable("id") String id, AuditLog al) {
        Object item = this.getService().get(id);
        if (item == null) {
            al.setDetails(id);
        } else {
            al.setDetails(this.getItemId(item));
        }
        return item;
    }

    @PostMapping("/pagination")
    @AuditMethod(name = "分页浏览")
    public PaginationResultVO pagination(@RequestBody String payload, AuditLog al) throws Exception {
        if (!PaginationService.class.isAssignableFrom(this.getService().getClass())) {
            throw new IllegalClassFormatException(MessageFormat.format("{0} not implement {1}",
                    this.getService().getClass().getSimpleName(), PaginationService.class.getSimpleName()));
        }

        al.setParams(payload);

        TypeToken<?> queryToken = sTypeToken.resolveType(PaginationService.class.getTypeParameters()[0]);
        BasePaginationQuery query = (BasePaginationQuery) JSON.deserialize(payload, (Class<?>) queryToken.getType());
        PaginationResultVO pagination = ((PaginationService) this.getService()).pagination(query);

        // 记录查询结果的ID列表
        StringBuilder ids = new StringBuilder();
        for (Object item : pagination.getItems()) {
            ids.append(this.getItemId(item));
            ids.append(";");
        }
        al.setDetails(ids.toString());
        return pagination;
    }

    @GetMapping("")
    @AuditMethod(name = "列表浏览")
    public List list(AuditLog al) throws IllegalClassFormatException {
        if (!ViewService.class.isAssignableFrom(this.getService().getClass())) {
            throw new IllegalClassFormatException(MessageFormat.format("{0} not implement {1}",
                    this.getService().getClass().getSimpleName(), ViewService.class.getSimpleName()));
        }

        List result = ((ViewService) this.getService()).list();
        // 记录查询结果的ID列表
        StringBuilder ids = new StringBuilder();
        for (Object item : result) {
            ids.append(this.getItemId(item));
            ids.append(";");
        }
        al.setDetails(ids.toString());
        return result;
    }
}
