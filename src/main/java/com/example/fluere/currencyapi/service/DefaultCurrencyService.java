package com.example.fluere.currencyapi.service;

import com.example.fluere.currencyapi.model.FiatCurrencyDailyData;
import com.example.fluere.currencyapi.model.MKTCurrency;
import com.example.fluere.currencyapi.repository.CurrencyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(name = "app.property.service.currency-hardcoded", havingValue = "false")
public class DefaultCurrencyService implements CurrencyService {
    private static final UUID CONSTANT_CURRENCY_DATA_ID = UUID.fromString("f3921193-f133-4043-ae9a-08b6f4421af3");
    @Value("${CURRENCY_API_KEY}")
    private static String API_KEY;
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
        log.warn(fiatCurrency.toString());
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
                StringBuffer stringBuffer = new StringBuffer();
                Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) {
                    stringBuffer.append(scanner.nextLine());
                }
                scanner.close();

                String parsedJSON = stringBuffer.toString();

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

