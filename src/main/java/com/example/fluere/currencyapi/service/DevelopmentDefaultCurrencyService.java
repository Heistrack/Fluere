package com.example.fluere.currencyapi.service;

import com.example.fluere.currencyapi.model.FiatCurrencyDailyData;
import com.example.fluere.currencyapi.model.MKTCurrency;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

//TODO I want to have hardcoded test and dev currency service here, to save API requests
@Slf4j
@Service
@Profile({"dev", "test"})
public class DevelopmentDefaultCurrencyService implements CurrencyService {
    private static final UUID CONSTANT_CURRENCY_DATA_ID = UUID.fromString("f3921193-f133-4043-ae9a-08b6f4421af3");
    private final FiatCurrencyDailyData currencyDailyData;
    private final String HARDCODED_JSON_CURRENCIES = "XPF=110.4386, LBP=89500.0, BMD=1.0, TND=3.0954, DZD=133.5693, GNF=8680.354, XCD=2.7, TTD=6.7693, BRL=5.6952, KGS=85.502, BHD=0.376, HNL=24.9326, OMR=0.3845, CNY=7.1293, NPR=134.5674, CVE=102.0473, SYP=12889.3691, " +
            "INR=84.1089, GIP=0.7704, CHF=0.8655, " +
            "MMK=2097.6442, ARS=984.83, MNT=3371.6594, SZL=17.5375, RSD=108.2477, AWG=1.79, PYG=7863.1202, WST=2.7414, XOF=607.0708, JMD=158.4753, " +
            "GHS=16.3003, LKR=292.9399, VUV=119.5841, BZD=2.0, NZD=1.6538, GMD=70.8059, KMF=455.3031, " +
            "DOP=60.2018, PHP=57.8219, BSD=1.0, RUB=96.1651, MAD=9.8929, PKR=277.6624, GEL=2.7261, NGN=1634.9307, " +
            "LYD=4.809, UGX=3657.9522, SLL=23194.7758, ZWL=27.0645, ERN=15.0, JOD=0.709, XDR=0.7513, TRY=34.2607, SAR=3.75, IMP=0.7704, SSP=3448.96, CLP=953.169, MVR=15.4413, TOP=2.3339, GBP=0.7704, SDG=449.3671, IQD=1308.9704, COP=4263.1708, MDL=17.7406, FJD=2.2349, VES=39.323, AFN=66.1514, AOA=916.0096, " +
            "NAD=17.5375, BND=1.3158, CAD=1.3823, PAB=1.0, TZS=2712.0367, ALL=91.2235, DJF=177.721, " +
            "TVD=1.4964, CUP=24.0, SBD=8.4771, BBD=2.0, TWD=32.0301, AUD=1.4964, BWP=13.3443, SHP=0.7704, HUF=370.5855, JEP=0.7704, TMT=3.5, IRR=42043.3401, KRW=1378.7599, PEN=3.7636, SLE=23.1948, KID=1.4964, LRD=192.5235, HKD=7.7722, TJS=10.6577, MOP=8.0054, " +
            "SEK=10.5434, AED=3.6725, EGP=48.6895, MWK=1742.0819, STN=22.6741, KYD=0.8333, ETB=118.5536, SRD=33.4505, YER=250.2453, PLN=3.9977, AZN=1.6999, PGK=3.9458, XAF=607.0708, DKK=6.9052, " +
            "CZK=23.348, KWD=0.3065, ANG=1.79, USD=1, HTG=131.6466, NIO=36.803, MXN=19.9343, UYU=41.6451, MZN=63.9193, FOK=6.9013, BAM=1.8101, MRU=39.7567, SCR=14.1173, BYN=3.2751, MKD=56.6586, BGN=1.8098, " +
            "CDF=2836.8784, FKP=0.7704, SGD=1.3158, BTN=84.1046, NOK=10.9225, THB=33.5009, " +
            "MUR=46.1815, SOS=571.4482, RON=4.5965, UAH=41.339, HRK=6.973, MGA=4588.1655, JPY=150.9973, " +
            "GTQ=7.7278, RWF=1367.9051, BDT=119.5049, QAR=3.64, KHR=4083.6043, CRC=514.9293, IDR=15562.3949, " +
            "AMD=387.2303, ZAR=17.5365, UZS=12793.5942, VND=25421.5353, KZT=484.8233, ILS=3.7739, BOB=6.9191, LSL=17.5375, LAK=21912.0114, " +
            "KES=129.1178, GYD=209.2182, BIF=2910.2313, MYR=4.3244, GGP=0.7704, ISK=137.9991, EUR=0.9257, ZMW=26.5883";


    public DevelopmentDefaultCurrencyService() {
        this.currencyDailyData = new FiatCurrencyDailyData(
                CONSTANT_CURRENCY_DATA_ID,
                1410,
                LocalDate.now(),
                getHashMapFromJSON()
        );
    }

    @Override
    public FiatCurrencyDailyData getData() {
        return currencyDailyData;
    }

    @PostConstruct
    private void dataCheckForConsole() {
        log.error(currencyDailyData.toString());
    }

    private HashMap<MKTCurrency, BigDecimal> getHashMapFromJSON() {
        return Arrays.stream(
                             HARDCODED_JSON_CURRENCIES.split(", "))
                     .map(String::trim)
                     .map(s -> s.split("="))
                     .collect(Collectors.toMap(
                             s -> MKTCurrency.valueOf(
                                     s[0]),
                             s -> new BigDecimal(
                                     s[1]),
                             (x, y) -> y,
                             HashMap::new
                     ));
    }
}
