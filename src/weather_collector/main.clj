(ns weather-collector.main)


(defn- config []
  {:api-key  (System/getenv "OPENWEATHER_API_KEY")
   :city     (or (System/getenv "CITY") "Berlin")
   :port     (parse-long (or (System/getenv "PORT") "3000"))
   :data-dir "."})
