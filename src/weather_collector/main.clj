(ns weather-collector.main
  (:require [weather-collector.http :as http]
            [weather-collector.job :as job]
            [weather-collector.storage :as storage]
            [weather-collector.weather :as weather])
  (:gen-class))

(defn- config []
  {:api-key  (System/getenv "OPENWEATHER_API_KEY")
   :city     (or (System/getenv "CITY") "Berlin")
   :port     (parse-long (or (System/getenv "PORT") "3000"))
   :data-dir (or (System/getenv "DATA_DIR") ".")})

(defn- exit
  [status msg]
  (println msg)
  (System/exit status))

(defn -main
  [& _args]
  (let [{:keys [api-key city port data-dir]} (config)
        path  (storage/csv-path data-dir city)
        fetch #(weather/fetch-temperature city api-key)]
    (if-not api-key
      (exit 1 "OPENWEATHER_API_KEY is not set, see .env.example")
      (do
        (job/start-schedule #(job/collect! fetch path))
        (http/start-server (http/make-handler path) port)
        (println "Collecting" city "into" path "- serving on port" port)))))
