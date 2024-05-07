package com.example.final_project.currencyapi.service;

import com.example.final_project.currencyapi.model.FiatCurrencyDailyData;
import com.example.final_project.currencyapi.model.MKTCurrency;
import com.example.final_project.currencyapi.repository.CurrencyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

@Slf4j
@Service
public class DefaultCurrencyService implements CurrencyService {
    private static final UUID CONSTANT_CURRENCY_DATA_ID = UUID.fromString("f3921193-f133-4043-ae9a-08b6f4421af3");
    private static final String API_KEY = "d28460f556350b8bf782f2d4";
    private final FiatCurrencyDailyData fiatCurrency;
    private final CurrencyRepository currencyRepository;
    private final ObjectMapper objectMapper;

    public DefaultCurrencyService(CurrencyRepository currencyRepository,
                                  ObjectMapper objectMapper
    ) {
        this.currencyRepository = currencyRepository;
        this.objectMapper = objectMapper;
        this.fiatCurrency = currencyRepository.findById(CONSTANT_CURRENCY_DATA_ID)
                                              .orElseGet(() -> new FiatCurrencyDailyData(
                                                      CONSTANT_CURRENCY_DATA_ID,
                                                      1500,
                                                      LocalDate.now().minusDays(1),
                                                      null
                                              ));
    }

    @Override
    public FiatCurrencyDailyData getData() {
        return fiatCurrency;
    }

    @PostConstruct
    private void updateData() {
        if (!fiatCurrency.previousDateCheck().equals(LocalDate.now())) {
            HashMap<MKTCurrency, BigDecimal> jsonMap = getJSONMap();
            Integer currentValue = fiatCurrency.howMuchRequestLeftAPI();

            currencyRepository.save(new FiatCurrencyDailyData(CONSTANT_CURRENCY_DATA_ID,
                                                              --currentValue, LocalDate.now(), jsonMap
            ));
        }
    }

    @SuppressWarnings("unchecked")
    private HashMap<MKTCurrency, BigDecimal> getJSONMap() {
        try {
            String strURL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD";

            URL url = URI.create(strURL).toURL();
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            int responseCode = request.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) {
                    stringBuilder.append(scanner.nextLine());
                }
                scanner.close();

                String parsedJSON = stringBuilder.toString();

                Map<String, Object> jsonMap = objectMapper.readValue(parsedJSON, Map.class);

                return new HashMap<>((Map<MKTCurrency, BigDecimal>) jsonMap.get(
                        "conversion_rates"));
            }
        } catch (IOException e) {
            log.error(e.toString());
        }
        return new HashMap<>();
    }
}

