package com.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stock.dao.StockConfigDao;
import com.stock.dao.StockInfoDao;
import com.stock.dto.ApiResponse;
import com.stock.dto.StockBriefDTO;
import com.stock.dto.StockDetailDTO;
import com.stock.dto.StockInfoRealTimeDTO;
import com.stock.entity.StockConfigEntity;
import com.stock.entity.StockInfoEntity;
import com.stock.mybatis.service.impl.BaseServiceImpl;
import com.stock.mybatis.utils.ConvertUtils;
import com.stock.mybatis.utils.R;
import com.stock.service.StockInfoService;
import com.stock.vo.StockInfoVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class StockInfoServiceImpl extends BaseServiceImpl<StockInfoDao, StockInfoEntity> implements StockInfoService {

    private static final String api1 = "http://api.vvtr.com/v1/stock/kline";
    private static final String api4 = "http://api.vvtr.com/v1/index/kline";
    private final HttpServletRequest request;
    private final RestTemplate restTemplate;
    private final StockConfigDao stockConfigDao;
    private final String apiKey;
    private final String flag;


    public StockInfoServiceImpl(HttpServletRequest request, RestTemplate restTemplate, StockConfigDao stockConfigDao) {
        this.request = request;
        this.restTemplate = restTemplate;
        this.stockConfigDao = stockConfigDao;
        this.apiKey = Objects.isNull(System.getProperty("AK")) ? System.getenv("AK") : System.getProperty("AK");
        this.flag = Objects.isNull(System.getProperty("FLAG")) ? System.getenv("FLAG") : System.getProperty("FLAG");
    }

    @Override
    @Transactional
    public R<Object> stockSelectNow() {
        StockConfigEntity stockConfigEntity = stockConfigDao.selectById(1);
        saveStockDataAsync(stockConfigEntity.getType(), stockConfigEntity.getStrategy(), stockConfigEntity.getRecordHighDay(), stockConfigEntity.getIncreaseDay(), stockConfigEntity.getIncrease());
        return R.ok();
    }

    @Transactional
    public void saveStockDataAsync(String type, String strategy, int recordHighDay, int increaseDay, int increase) {
        String url = "http://api.vvtr.com/v1/symbols";
        URI uri = null;
        try {
            uri = new URI(url + "?type=" + (Objects.isNull(type) ? 3 : type) + "&apiKey=" + apiKey);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = getHttpHeadersFromRequest();
        ResponseEntity<ApiResponse<StockBriefDTO>> exchange = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {
        });
        List<StockBriefDTO> stockBriefDTOS = Objects.requireNonNull(exchange.getBody()).getData();
        LocalDateTime to = LocalDateTime.now().plusDays(1);
        LocalDateTime from = to.minusDays(100);
        List<StockInfoEntity> nowStockInfoEntities = getStockDetail(stockBriefDTOS, from, to, recordHighDay, increaseDay, increase);
        nowStockInfoEntities.forEach(s -> s.setStrategy(strategy));
        List<StockInfoEntity> stockInfoEntities = baseMapper.selectList(new QueryWrapper<>());
        Map<String, StockInfoEntity> collect = stockInfoEntities.stream().collect(Collectors.toMap(StockInfoEntity::getSymbol, stockInfoEntity -> stockInfoEntity));
        for (StockInfoEntity stockInfoEntity : nowStockInfoEntities) {
            if (collect.containsKey(stockInfoEntity.getSymbol())) {
                StockInfoEntity infoEntity = collect.get(stockInfoEntity.getSymbol());
                infoEntity.setConsecutiveEnrollment(Objects.isNull(infoEntity.getConsecutiveEnrollment()) ? 0 : infoEntity.getConsecutiveEnrollment() + 1);
                infoEntity.setHistoryMax(infoEntity.getHistoryMax().compareTo(stockInfoEntity.getHistoryMax()) > 0 ? infoEntity.getHistoryMax() : stockInfoEntity.getHistoryMax());
                infoEntity.setMa5(stockInfoEntity.getMa5());
                infoEntity.setMa10(stockInfoEntity.getMa10());
                infoEntity.setMa20(stockInfoEntity.getMa20());
                infoEntity.setClose(stockInfoEntity.getClose());
                infoEntity.setHistoryMaxTime(stockInfoEntity.getHistoryMaxTime());
                this.updateById(infoEntity);
            } else {
                stockInfoEntity.setConsecutiveEnrollment(1);
                this.save(stockInfoEntity);
            }
        }
        LambdaQueryWrapper<StockInfoEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<StockInfoEntity> infoEntityList = baseMapper.selectList(lambdaQueryWrapper);
        List<StockInfoEntity> infoEntityList1 = infoEntityList.stream().filter(entity -> !Objects.equals(4, entity.getProduct())).collect(Collectors.toList());
        List<StockInfoEntity> infoEntityList2 = infoEntityList.stream().filter(entity -> Objects.equals(4, entity.getProduct())).collect(Collectors.toList());
        List<List<StockInfoEntity>> list1s = splitList1(infoEntityList1);
        List<List<StockInfoEntity>> list2s = splitList1(infoEntityList2);
        cccc(list1s, from, to, api1);
        cccc(list2s, from, to, api4);
    }

    private void cccc(List<List<StockInfoEntity>> list1s, LocalDateTime from, LocalDateTime to, String url) {
        // 获取上个交易日收盘价
        for (List<StockInfoEntity> list : list1s) {
            String symbols = list.stream().map(StockInfoEntity::getSymbol).collect(Collectors.joining(","));
            List<StockDetailDTO> stockMaxDetails = getStockDetailDTOList(symbols, from.minusDays(2000), to, 20, url);
            Map<String, List<StockDetailDTO>> listMap = stockMaxDetails.stream().collect(Collectors.groupingBy(StockDetailDTO::getSymbol));
            for (StockInfoEntity stockInfoEntity : list) {
                if (listMap.containsKey(stockInfoEntity.getSymbol())) {
                    List<StockDetailDTO> stockDetailDTOS = listMap.get(stockInfoEntity.getSymbol());
                    double ma5 = stockDetailDTOS.stream().limit(5).map(StockDetailDTO::getClose).map(BigDecimal::doubleValue).mapToDouble(Double::doubleValue).average().orElse(0.0);
                    double ma10 = stockDetailDTOS.stream().limit(10).map(StockDetailDTO::getClose).map(BigDecimal::doubleValue).mapToDouble(Double::doubleValue).average().orElse(0.0);
                    double ma20 = stockDetailDTOS.stream().limit(20).map(StockDetailDTO::getClose).map(BigDecimal::doubleValue).mapToDouble(Double::doubleValue).average().orElse(0.0);
                    stockInfoEntity.setMa5(ma5);
                    stockInfoEntity.setMa10(ma10);
                    stockInfoEntity.setMa20(ma20);
                    stockInfoEntity.setClose(stockDetailDTOS.get(0).getClose());
                    this.updateById(stockInfoEntity);
                }
            }
        }
    }

    @Override
    public R<Object> getStockPoolAll(Integer product, String order, Long targetId) {
        LambdaQueryWrapper<StockInfoEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if ("desc".equals(order)) {
            lambdaQueryWrapper.orderByDesc(StockInfoEntity::getConsecutiveEnrollment);
        } else {
            lambdaQueryWrapper.orderByAsc(StockInfoEntity::getConsecutiveEnrollment);
        }
        if (Objects.isNull(product)) {
            lambdaQueryWrapper.eq(StockInfoEntity::getProduct, 1);
        } else {
            lambdaQueryWrapper.eq(StockInfoEntity::getProduct, product);
        }
        List<StockInfoEntity> stockInfoEntities = baseMapper.selectList(lambdaQueryWrapper);
        if (stockInfoEntities.isEmpty()) {
            return R.error("选股中，请稍候");
        }
        List<StockInfoVo> stockInfoVos = ConvertUtils.sourceToTarget(stockInfoEntities, StockInfoVo.class);
        return R.ok(stockInfoVos);
    }

    @Override
    public R<Object> getConfig() {
        StockConfigEntity stockConfigEntity = stockConfigDao.selectById(1);
        return R.ok(stockConfigEntity);
    }

    @Override
    public R<Object> updateStockConfig(StockConfigEntity stockConfig) {
        int i = stockConfigDao.updateById(stockConfig);
        if (i == 1) {
            return R.ok();
        } else {
            return R.error("更新失败");
        }
    }

    private List<StockInfoEntity> getStockDetail(List<StockBriefDTO> stockBriefDTOS, LocalDateTime from, LocalDateTime to, int recordHighDay, int increaseDay, int increase) {
        List<List<StockBriefDTO>> lists = splitList(stockBriefDTOS);
        List<StockInfoEntity> StockBriefAllResults = new ArrayList<>();
        for (List<StockBriefDTO> dtos : lists) {
            Map<String, StockBriefDTO> listMap = dtos.stream().collect(Collectors.toMap(StockBriefDTO::getSymbol, stockBriefDTO -> stockBriefDTO));
            List<StockInfoEntity> stockResults = new ArrayList<>();
            String symbols = dtos.stream().map(StockBriefDTO::getSymbol).collect(Collectors.joining(","));
            List<StockDetailDTO> stockDetailDTOList = getStockDetailDTOList(symbols, from, to, 60, api1);
            stockDetailDTOList.stream().collect(Collectors.groupingBy(StockDetailDTO::getSymbol)).forEach((symbol, stockDetailDTOListMap) -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                stockDetailDTOListMap.sort((s1, s2) -> {
                    LocalDateTime time1 = LocalDateTime.parse(s1.getTime(), formatter);
                    LocalDateTime time2 = LocalDateTime.parse(s2.getTime(), formatter);
                    return time2.compareTo(time1);
                });
                double ma5 = stockDetailDTOListMap.stream().limit(5).map(StockDetailDTO::getClose).map(BigDecimal::doubleValue).mapToDouble(Double::doubleValue).average().orElse(0.0);
                double ma10 = stockDetailDTOListMap.stream().limit(10).map(StockDetailDTO::getClose).map(BigDecimal::doubleValue).mapToDouble(Double::doubleValue).average().orElse(0.0);
                double ma20 = stockDetailDTOListMap.stream().limit(20).map(StockDetailDTO::getClose).map(BigDecimal::doubleValue).mapToDouble(Double::doubleValue).average().orElse(0.0);
                if (ma5 >= ma10 && ma10 >= ma20) {
                    BigDecimal maxIncrease = BigDecimal.ZERO;
                    for (int i = 1; i < increaseDay + 1; i++) {
                        StockDetailDTO  previous = stockDetailDTOListMap.get(i); // 获取今天的股票信息
                        StockDetailDTO current = stockDetailDTOListMap.get(i - 1); // 获取前一天的股票信息
                        if (previous.getClose().compareTo(BigDecimal.ZERO) != 0) {
                            BigDecimal increase1 = (current.getClose().subtract(previous.getClose())).divide(previous.getClose(), 4, BigDecimal.ROUND_HALF_UP)
                                    .multiply(BigDecimal.valueOf(100));
                            if (increase1.compareTo(maxIncrease) > 0) {
                                maxIncrease = increase1;
                            }
                        }
                    }
                    if (maxIncrease.compareTo(BigDecimal.valueOf(increase)) > 0) {
                        List<StockDetailDTO> stockMaxDetails = getStockDetailDTOList(symbol, from.minusDays(50000), to, 2000, api1);
                        BigDecimal nowMax = stockDetailDTOListMap.stream().limit(recordHighDay).map(StockDetailDTO::getHigh).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);

                        StockDetailDTO maxDetail = stockMaxDetails.stream().max(Comparator.comparing(StockDetailDTO::getHigh)).orElse(new StockDetailDTO());
                        BigDecimal historyMax = maxDetail.getHigh();
                            if (nowMax.compareTo(historyMax) >= 0) {
                            StockBriefDTO stockBriefDTO = listMap.get(stockDetailDTOListMap.get(0).getSymbol());
                            StockInfoEntity stockInfoEntity = ConvertUtils.sourceToTarget(stockBriefDTO, StockInfoEntity.class);
                            stockInfoEntity.setHistoryMax(historyMax);
                            stockInfoEntity.setHistoryMaxTime(maxDetail.getTime());
                            stockInfoEntity.setMa5(ma5);
                            stockInfoEntity.setMa10(ma10);
                            stockInfoEntity.setMa20(ma20);
                            stockInfoEntity.setClose(stockDetailDTOListMap.get(0).getClose());
                            stockResults.add(stockInfoEntity);
                        }
                    }
                }
            });
            StockBriefAllResults.addAll(stockResults);
            if (!Objects.equals("stock0011", flag)) {
                if (StockBriefAllResults.size() > 30) {
                    break;
                }
            }
        }
        return StockBriefAllResults;
    }

    private List<StockDetailDTO> getStockDetailDTOList(String symbols, LocalDateTime from, LocalDateTime to, int limit, String url) {
        int length = symbols.split(",").length;
        long time = System.currentTimeMillis();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("--------------- {} --------------- {} --------------- {} ----- {}", time, System.currentTimeMillis(), time - System.currentTimeMillis(), length);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fromStr = from.format(formatter);
        String toStr = to.format(formatter);
        // 使用 UriComponentsBuilder 构建 URL
        URI uri = UriComponentsBuilder.fromHttpUrl(url).queryParam("symbols", symbols).queryParam("from", fromStr).queryParam("to", toStr).queryParam("interval", "1d").queryParam("limit", length * limit).queryParam("apiKey", apiKey).build().toUri();
        ResponseEntity<ApiResponse<StockDetailDTO>> exchange = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(getHttpHeadersFromRequest()), new ParameterizedTypeReference<>() {
        });
        return Objects.requireNonNull(exchange.getBody()).getData();
    }

    private HttpHeaders getHttpHeadersFromRequest() {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.add(headerName, headerValue);
        }
        return headers;
    }

    private List<List<StockBriefDTO>> splitList(List<StockBriefDTO> stockBriefDTOS) {
        List<List<StockBriefDTO>> result = new ArrayList<>();
        if (stockBriefDTOS == null || stockBriefDTOS.isEmpty()) {
            return result; // 返回空列表
        }

        int totalSize = stockBriefDTOS.size();
        for (int i = 0; i < totalSize; i += 30) {
            int end = Math.min(i + 30, totalSize);
            result.add(stockBriefDTOS.subList(i, end));
        }
        return result;
    }

    private List<List<StockInfoEntity>> splitList1(List<StockInfoEntity> stockBriefDTOS) {
        List<List<StockInfoEntity>> result = new ArrayList<>();
        if (stockBriefDTOS == null || stockBriefDTOS.isEmpty()) {
            return result;
        }
        int totalSize = stockBriefDTOS.size();
        for (int i = 0; i < totalSize; i += 30) {
            int end = Math.min(i + 30, totalSize);
            result.add(stockBriefDTOS.subList(i, end));
        }
        return result;
    }
}
