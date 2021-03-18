package com.zero.upload.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.fastjson.JSON;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.zero.upload.ExcelListener;
import com.zero.upload.MimeTypeEnum;
import com.zero.upload.dto.FileParseProcessDto;
import com.zero.upload.dto.SheetDto;
import com.zero.upload.entity.Resource;
import com.zero.upload.model.FileException;
import com.zero.upload.model.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.python.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author danyiran
 * @create 2021/3/16 13:52
 */
@Slf4j
@Component
public class HcFileUtils {

    private static Pattern linePattern = Pattern.compile("_(\\w)");

    @Value("${file.upload-dir}")
    private String localPath;

    /**
     * 单文件-根据文件后缀，判断文件类型
     *
     * @param file
     */
    public FileInfo uploadFile(MultipartFile file) throws IOException {
        Map<String, List<String>> fileSheetMap = Maps.newConcurrentMap();
        List<FileParseProcessDto> fileParseProcessDtos = new ArrayList<>();
        FileInfo fileInfo = new FileInfo();
        String originalFilename = file.getOriginalFilename();
        String prefix = originalFilename.substring(0, originalFilename.indexOf("."));
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String fileName = prefix + "_" + DateUtils.getTime() + "." + suffix;

        try {
            InputStream inputStream = file.getInputStream();
            String md5 = DigestUtils.md5DigestAsHex(inputStream);
            log.info(md5);
            //TODO:判断如果已经存储了该文件 查询出已经存储的文件路径
            /**
             * 在关联表中通过md5查询是否存在该文件，有则新建关联关系，无则存新文件
             */
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Copy file to the target location (Replacing existing file with the same name)
            String year = DateUtils.getYear();
            String month = DateUtils.getMonth();
            String day = DateUtils.getDay();
            Path dayDir = Paths.get(localPath).
                    resolve(year).
                    resolve(month).
                    resolve(day);

            Files.createDirectories(dayDir);

            Path targetLocation = dayDir.
                    resolve(fileName);
            //文件本地保存
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            if (MimeTypeEnum.XLS == MimeTypeEnum.findByExtension(suffix) || MimeTypeEnum.XLSX == MimeTypeEnum.findByExtension(suffix)) {
                //无需创建对象，解析excel,读取全部sheet,无需创建对象进行解析
                List<ReadSheet> readSheets = EasyExcel.read(file.getInputStream()).build().excelExecutor().sheetList();
                //sheetNo 和 sheetName 的map
                Map<Integer, String> noNameMap = readSheets.stream().collect(Collectors.toMap(ReadSheet::getSheetNo, ReadSheet::getSheetName));
                List<String> sheetList = readSheets.stream().map(e -> e.getSheetName()).collect(Collectors.toList());
                log.info("所有sheets:{}", sheetList);
                //解析excel,读取全部sheet,无需创建对象进行解析
                //EasyExcel.read(file.getInputStream(), new ExcelListener()).doReadAll();

                //部分读取
                ExcelReader excelReader = null;
                try {
                    excelReader = EasyExcel.read(file.getInputStream()).build();
                    ReadSheet[] readSheet = new ReadSheet[readSheets.size()];
                    for (int i = 0; i < readSheets.size(); i++) {
                        ReadSheet r = EasyExcel.readSheet(readSheets.get(i).getSheetName()).registerReadListener(new ExcelListener()).build();
                        readSheet[i] = r;
                    }
                    //读取excel
                    excelReader.read(readSheet);
                } finally {
                    if (!ObjectUtils.isEmpty(excelReader)) {
                        excelReader.finish();
                    }
                }
                //TODO:记录到表之后，清理缓存cache
                fileSheetMap.put(originalFilename, sheetList);
                Map<String, List<String>> mp = ExcelListener.headMapper;
                log.info("各个sheet对应的head头字段:{}", mp);
                sheetList.forEach(e -> {
                    FileParseProcessDto dto = new FileParseProcessDto();
                    dto.setFileName(originalFilename);
                    dto.setSheetName(e);
                    SheetDto sheetDetail = new SheetDto();
                    if (!StringUtils.isEmpty(e) && mp.containsKey(e)) {
                        Map<String, String> tmpMap = new HashMap<>();
                        for (String s : mp.get(e)) {
                            tmpMap.put(s, s);
                        }
                        sheetDetail.setTargetTable(tmpMap);
                    }
                    dto.setSheetDetail(sheetDetail);
                    fileParseProcessDtos.add(dto);
                });
                Map<String, List<Map<String, String>>> dataMap = ExcelListener.dataMap;
                log.info("每个sheet对应的数据:{}", dataMap);
                fileInfo.setFileParseProcessDtoList(fileParseProcessDtos);
                log.info("fileParseProcessDtos:{}", JSON.toJSONString(fileParseProcessDtos));
                //对缓存清理，防止错误的累加
                ExcelListener.dataMap.clear();
                ExcelListener.headMapper.clear();
            } else if (MimeTypeEnum.CSV == MimeTypeEnum.findByExtension(suffix)) {
                //TODO:解析csv
                CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), "GBK"));
                HeaderColumnNameTranslateMappingStrategy strategy = new HeaderColumnNameTranslateMappingStrategy<Resource>();
                //ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
                strategy.setType(Resource.class);
                //读取头
                String[] headers = reader.readNext();
                log.info("headers:{}", JSON.toJSONString(headers));
                Map<String, String> headerMap = new HashMap<>();
                for (String s : headers) {
                    headerMap.put(s, lineToHump(s));
                }
                strategy.setColumnMapping(headerMap);
                //读取表内容
                List<String[]> contents = reader.readAll();

                String contentString = JSON.toJSONString(contents);
                contentString = contentString.substring(1, contentString.length() - 1).replace('[', '(').replace(']', ')');
                log.info("table contents:{}", contentString);
                CsvToBean<Resource> csvToBean = new CsvToBeanBuilder<Resource>(reader).withMappingStrategy(strategy).build();
                List<Resource> parse = csvToBean.parse();
                log.info("解析的结果:{}", JSON.toJSONString(parse));
                reader.close();
            }

            fileInfo.setFileName(fileName);
            fileInfo.setYearDir(year);
            fileInfo.setMonthDir(month);
            fileInfo.setDayDir(day);

            /**
             * 生成下载地址
             */
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("file/download/")
                    .path(fileInfo.getYearDir() + "/")
                    .path(fileInfo.getMonthDir() + "/")
                    .path(fileInfo.getDayDir() + "/")
                    .path(fileInfo.getFileName())
                    .toUriString();
            return fileInfo;
            //TODO:生成记录到文件表中

        } catch (IOException ex) {
            throw new FileException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    /**
     * 删除文件
     *
     * @param path 文件访问的路径upload开始 如： /upload/image/test.jpg
     * @return true 删除成功； false 删除失败
     */
    public static boolean delete(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 下划线转驼峰
     */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
