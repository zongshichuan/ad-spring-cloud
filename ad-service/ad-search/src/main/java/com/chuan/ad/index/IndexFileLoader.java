package com.chuan.ad.index;

import com.alibaba.fastjson.JSON;
import com.chuan.ad.dump.DConstant;
import com.chuan.ad.dump.table.*;
import com.chuan.ad.handler.AdLevelDataHandler;
import com.chuan.ad.constant.OpType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 根据文件加载全量索引
 *
 * @DependsOn注解可以定义在类和方法上，意思是我这个组件要依赖于另一个组件，也就是说被依赖的组件会比该组件先注册到IOC容器中。
 */
@Component
@DependsOn("dataTable")
@Slf4j
public class IndexFileLoader {

    @PostConstruct
    public void init() {
        log.info("----------------全量广告索引数据加载开始-------------------");
        //1、加载二级索引数据  推广计划到JVM内存
        List<String> adPlanStrings = loadDumpData(
                String.format("%s%s",
                        DConstant.DATA_ROOT_DIR,
                        DConstant.AD_PLAN)
        );
        adPlanStrings.forEach(p -> AdLevelDataHandler.handleLevel2(
                JSON.parseObject(p, AdPlanTable.class),
                OpType.ADD
        ));
        //2、加载二级索引数据  广告创意到JVM内存
        List<String> adCreativeStrings = loadDumpData(
                String.format("%s%s",
                        DConstant.DATA_ROOT_DIR,
                        DConstant.AD_CREATIVE)
        );
        adCreativeStrings.forEach(c -> AdLevelDataHandler.handleLevel2(
                JSON.parseObject(c, AdCreativeTable.class),
                OpType.ADD
        ));

        //3、加载三级索引数据  推广单元到JVM内存
        List<String> adUnitStrings = loadDumpData(
                String.format("%s%s",
                        DConstant.DATA_ROOT_DIR,
                        DConstant.AD_UNIT)
        );
        adUnitStrings.forEach(u -> AdLevelDataHandler.handleLevel3(
                JSON.parseObject(u, AdUnitTable.class),
                OpType.ADD
        ));

        //4、加载三级索引数据  推广单元和广告创意关联 到JVM内存
        List<String> adCreativeUnitStrings = loadDumpData(
                String.format("%s%s",
                        DConstant.DATA_ROOT_DIR,
                        DConstant.AD_CREATIVE_UNIT)
        );
        adCreativeUnitStrings.forEach(cu -> AdLevelDataHandler.handleLevel3(
                JSON.parseObject(cu, AdCreativeUnitTable.class),
                OpType.ADD
        ));
        //5、加载四级索引数据  地域限制 到JVM内存
        List<String> adUnitDistrictStrings = loadDumpData(
                String.format("%s%s",
                        DConstant.DATA_ROOT_DIR,
                        DConstant.AD_UNIT_DISTRICT)
        );
        adUnitDistrictStrings.forEach(d -> AdLevelDataHandler.handleLevel4(
                JSON.parseObject(d, AdUnitDistrictTable.class),
                OpType.ADD
        ));
        //6、加载四级索引数据  兴趣限制 到JVM内存
        List<String> adUnitItStrings = loadDumpData(
                String.format("%s%s",
                        DConstant.DATA_ROOT_DIR,
                        DConstant.AD_UNIT_IT)
        );
        adUnitItStrings.forEach(i -> AdLevelDataHandler.handleLevel4(
                JSON.parseObject(i, AdUnitItTable.class),
                OpType.ADD
        ));
        //6、加载四级索引数据  关键词限制 到JVM内存
        List<String> adUnitKeywordStrings = loadDumpData(
                String.format("%s%s",
                        DConstant.DATA_ROOT_DIR,
                        DConstant.AD_UNIT_KEYWORD)
        );
        adUnitKeywordStrings.forEach(k -> AdLevelDataHandler.handleLevel4(
                JSON.parseObject(k, AdUnitKeywordTable.class),
                OpType.ADD
        ));

        log.info("----------------全量广告索引数据加载结束-------------------");
    }

    /**
     * 解析文件数据，返回List<String>
     *
     * @param fileName
     * @return
     */
    private List<String> loadDumpData(String fileName) {

        try (BufferedReader br = Files.newBufferedReader(
                Paths.get(fileName)
        )) {
            return br.lines().collect(Collectors.toList());
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
