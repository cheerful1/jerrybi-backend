package com.yupi.springbootinit.utils;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;

import org.springframework.web.multipart.MultipartFile;


import java.io.File;

import java.io.FileNotFoundException;

import java.io.IOException;

import java.util.LinkedHashMap;

import java.util.List;

import java.util.Map;

import java.util.stream.Collectors;
/**
 * @author : wangshanjie
 * @date : 10:50 2023/8/4
 */




/**

 * Excel 相关工具类，使用easyexcel工具库
 * 这里使用的库上传，具体的操作看伙伴匹配系统里面的上传的系统

 */

@Slf4j

public class ExcelUtils {



    /**

     * excel 转 csv

     *

     * @param multipartFile

     * @return

     */

    public static String excelToCsv(MultipartFile multipartFile) {

//        File file = null;

//        try {

//            file = ResourceUtils.getFile("classpath:网站数据.xlsx");

//        } catch (FileNotFoundException e) {

//            e.printStackTrace();

//        }

        // 读取数据

        List<Map<Integer, String>> list = null;

        try {

            list = EasyExcel.read(multipartFile.getInputStream())

                    .excelType(ExcelTypeEnum.XLSX)

                    .sheet()

                    .headRowNumber(0)

                    .doReadSync();

        } catch (IOException e) {

            log.error("表格处理错误", e);

        }

        if (CollUtil.isEmpty(list)) {

            return "";

        }

        // 转换为 csv

        StringBuilder stringBuilder = new StringBuilder();

        // 读取表头

        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);
        // 过滤为空的表格

        List<String> headerList = headerMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());

        stringBuilder.append(StringUtils.join(headerList, ",")).append("\n");

        // 读取数据

        for (int i = 1; i < list.size(); i++) {

            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) list.get(i);
            // 过滤为null的数据，空的数据可能是数据中的东西。

            List<String> dataList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());

            stringBuilder.append(StringUtils.join(dataList, ",")).append("\n");

        }

        return stringBuilder.toString();

    }



    public static void main(String[] args) {

        excelToCsv(null);

    }

}
