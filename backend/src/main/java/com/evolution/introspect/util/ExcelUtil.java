package com.evolution.introspect.util;

import com.alibaba.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author ZhuMing
 * @date 2024/5/14
 **/
public class ExcelUtil {

    /**
     * 从Excel文件中读取数据。
     *
     * @param file 需要读取的MultipartFile文件对象，通常来自上传的文件。
     * @param head 数据实体的Class类型，用于解析Excel的头部信息并映射到对应的实体对象。
     * @return 返回一个包含读取到的所有数据实体的List集合。
     * @throws IOException 当读取文件发生错误时抛出IOException。
     */
    public static <T> List<T> read(MultipartFile file, Class<T> head) throws IOException {
        // 使用EasyExcel框架读取Excel文件内容
        return EasyExcel.read(file.getInputStream(), head, null)
                .autoCloseStream(Boolean.FALSE)
                .doReadAllSync();
    }

    /**
     * 将数据写入Excel文件。
     *
     * @param response  HttpServletResponse对象，用于将Excel文件写入响应。
     * @param fileName  Excel文件的名称，用于设置响应的Content-Disposition头信息。
     * @param sheetName Excel文件的sheet名称，用于设置Excel文件的sheet名称。
     * @param head      数据实体的Class类型，用于解析Excel的头部信息并映射到对应的实体对象。
     * @param data      需要写入的数据，类型为List<T>，其中T为数据实体的Class类型。
     */
    public static <T> void write(HttpServletResponse response, String fileName, String sheetName, Class<T> head, List<T> data) throws IOException {
        // 输出 Excel
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        EasyExcel.write(response.getOutputStream(), head)
                .autoCloseStream(Boolean.FALSE)
                .sheet(sheetName).doWrite(data);
    }
}
