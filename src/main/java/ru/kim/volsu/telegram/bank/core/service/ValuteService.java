package ru.kim.volsu.telegram.bank.core.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.kim.volsu.telegram.bank.core.model.Valute;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class ValuteService {

    private static final Logger log = LogManager.getLogger(ValuteService.class);

    private final RestTemplate restTemplate;

    @Value ("${cbr.valute.url}")
    private String url;

    public ValuteService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Valute> parseValute() {
        String response = restTemplate.getForObject(URI.create(url), String.class);
        JSONObject jsonObject = new JSONObject(response);
        JSONObject valuteObject = jsonObject.getJSONObject("Valute");

        HashMap<String, JSONObject> valuteMap = new HashMap<>();
        valuteMap.put("BYN", valuteObject.getJSONObject("BYN"));
        valuteMap.put("USD", valuteObject.getJSONObject("USD"));
        valuteMap.put("KZT", valuteObject.getJSONObject("KZT"));
        valuteMap.put("CNY", valuteObject.getJSONObject("CNY"));
        valuteMap.put("UAH", valuteObject.getJSONObject("UAH"));
        valuteMap.put("JPY", valuteObject.getJSONObject("JPY"));

        HashMap<String, Valute> resultMap = new HashMap<>();
        valuteMap.forEach((key, value) -> {
            Valute valute = new Valute();
            valute.setCharCode(value.getString("CharCode"));
            valute.setNominal(value.getInt("Nominal"));
            valute.setName(value.getString("Name"));
            valute.setValue(value.getBigDecimal("Value"));

            resultMap.put(key, valute);
        });

        log.info("Спаршены валюты");
        return resultMap;
    }

}
