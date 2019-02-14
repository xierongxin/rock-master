package com.jy.rock.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.jy.rock.bean.JsonFileVO;
import com.jy.rock.domain.EquipmentModel;
import com.jy.rock.service.EquipmentModelServiceImpl;
import com.xmgsd.lan.roadhog.bean.SimpleResponseVO;
import com.xmgsd.lan.roadhog.utils.JSON;
import com.xmgsd.lan.roadhog.web.controller.SimpleCurdViewController;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * @author hzhou
 */
@RestController
@RequestMapping("/equipment_model")
public class EquipmentModelController extends BaseController<EquipmentModelServiceImpl> implements SimpleCurdViewController<EquipmentModelServiceImpl> {

    private static EquipmentModel getEntity(String payload) throws IOException {
        return JSON.deserialize(payload, EquipmentModel.class);
    }

    private static List<JsonFileVO> getJsonFiles(String payload) throws IOException {
        JsonNode jsonNode = JSON.deserializeToNode(payload);
        JsonNode filesNode = jsonNode.get("instructions");
        return JSON.convertJsonNodeToList(filesNode, JsonFileVO.class);
    }

    @Override
    public SimpleResponseVO insert(@NotNull String payload) throws Exception {
        EquipmentModel em = getEntity(payload);
        // 获取说明书
        List<JsonFileVO> files = getJsonFiles(payload);
        this.getService().add(em, files);
        return new SimpleResponseVO(true);
    }

    @Override
    public SimpleResponseVO update(Serializable id, @NotNull String payload) throws Exception {
        EquipmentModel em = getEntity(payload);
        // 获取说明书
        List<JsonFileVO> files = getJsonFiles(payload);
        this.getService().update(id.toString(), em, files);
        return new SimpleResponseVO(true);
    }
}
