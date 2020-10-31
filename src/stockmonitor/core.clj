(ns stockmonitor.core
  (:gen-class)
  (:require [clojure.data.csv :as csv]
            [clojure.data.json :as json]
            [clojure.java.io :as io]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn csv-data->maps [csv-data]
  (map zipmap
       (->> (first csv-data)                                ;; First row is the header
            (map (fn [key] (clojure.string/replace key " " "")))
            (map keyword)                                   ;; Drop if you want string keys instead
            repeat)
       (rest csv-data)))

(defn read-stocks
  "reads stock from a csv into a map"
  [filepath]
  (->> filepath
       io/reader
       csv/read-csv
       csv-data->maps))

(defn remove-unused-fields [stock]
  (dissoc stock :TradedVolume :NumberOfTrades :Currency :SecurityID :SecurityType :Mnemonic))

(def interesting-stocks #{"DE000ETC0837"})

(defn get-stock-history [filename]
  (->> filename
       read-stocks
       (map remove-unused-fields)))

(comment
  (-> "https://query1.finance.yahoo.com/v7/finance/quote?symbols=amz.de"
      slurp
      (json/read-str :key-fn keyword)
      :quoteResponse
      :result
      first)
  {:fiftyDayAverageChangePercent      0.012787027,
   :bidSize                           100,
   :fiftyDayAverage                   2710.3428,
   :twoHundredDayAverage              2523.9712,
   :esgPopulated                      false,
   :averageDailyVolume3Month          5408,
   :regularMarketDayLow               2709.0,
   :twoHundredDayAverageChangePercent 0.087571844,
   :regularMarketOpen                 2730.0,
   :fiftyDayAverageChange             34.657227,
   :regularMarketPreviousClose        2713.5,
   :averageDailyVolume10Day           4719,
   :fiftyTwoWeekLow                   1431.0,
   :regularMarketDayHigh              2748.5,
   :symbol                            "AMZ.DE",
   :tradeable                         false,
   :priceToBook                       18.652912,
   :financialCurrency                 "USD",
   :regularMarketChangePercent        1.1608624,
   :marketState                       "POSTPOST",
   :triggerable                       false,
   :twoHundredDayAverageChange        221.02881,
   :askSize                           100,
   :fiftyTwoWeekLowChangePercent      0.918239,
   :bookValue                         147.162,
   :sharesOutstanding                 500889984,
   :marketCap                         1360435740672,
   :fullExchangeName                  "XETRA",
   :priceHint                         2,
   :fiftyTwoWeekLowChange             1314.0,
   :exchangeDataDelayedBy             0,
   :exchangeTimezoneName              "Europe/Berlin",
   :region                            "US",
   :currency                          "EUR",
   :firstTradeDateMilliseconds        1198828800000,
   :regularMarketChange               31.5,
   :shortName                         "AMAZON.COM INC.  DL-,01",
   :messageBoardId                    "finmb_18749",
   :fiftyTwoWeekHigh                  3000.0,
   :gmtOffSetMilliseconds             3600000,
   :exchangeTimezoneShortName         "CET",
   :language                          "en-US",
   :regularMarketVolume               5911,
   :regularMarketPrice                2745.0,
   :fiftyTwoWeekHighChange            -255.0,
   :quoteSourceName                   "Delayed Quote",
   :regularMarketTime                 1603989341,
   :market                            "de_market",
   :trailingPE                        105.42689,
   :fiftyTwoWeekHighChangePercent     -0.085,
   :quoteType                         "EQUITY",
   :longName                          "Amazon.com, Inc.",
   :exchange                          "GER",
   :regularMarketDayRange             "2709.0 - 2748.5",
   :sourceInterval                    15,
   :ask                               2738.0,
   :epsTrailingTwelveMonths           26.037,
   :underlyingSymbol                  "AMZ.F",
   :bid                               2734.5,
   :fiftyTwoWeekRange                 "1431.0 - 3000.0"})

(comment
  (-> "https://query1.finance.yahoo.com/v7/finance/download/AMZ.DE?period1=1603238400&period2=1603670400&interval=1d&events=history&includeAdjustedClose=true"
      read-stocks
      clojure.pprint/pprint)
  ({:Date     "2020-10-21",
    :Open     "2733.000000",
    :High     "2733.000000",
    :Low      "2664.500000",
    :Close    "2685.500000",
    :AdjClose "2685.500000",
    :Volume   "3859"}
   {:Date     "2020-10-22",
    :Open     "2670.500000",
    :High     "2710.500000",
    :Low      "2645.000000",
    :Close    "2657.500000",
    :AdjClose "2657.500000",
    :Volume   "3779"}
   {:Date     "2020-10-23",
    :Open     "2693.500000",
    :High     "2700.000000",
    :Low      "2656.500000",
    :Close    "2669.000000",
    :AdjClose "2669.000000",
    :Volume   "4160"}))
;https://github.com/Deutsche-Boerse/dbg-pds
;$ aws s3 ls deutsche-boerse-xetra-pds/2020-10-24/ --no-sign-request
;$ aws s3 cp s3://deutsche-boerse-xetra-pds/2019-08-01/2019-08-01_BINS_XETR04.csv . --no-sign-request
;$ wget https://deutsche-boerse-xetra-pds.s3.eu-central-1.amazonaws.com/2019-08-01/2019-08-01_BINS_XETR04.csv

; https://query1.finance.yahoo.com/v7/finance/quote?symbols=amz.de
; https://finance.yahoo.com/quote/AMZ.DE/history?p=AMZ.DE
; https://query1.finance.yahoo.com/v7/finance/download/AMZ.DE?period1=1603238400&period2=1603670400&interval=1d&events=history&includeAdjustedClose=true