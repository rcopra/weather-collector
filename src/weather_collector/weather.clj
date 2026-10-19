(ns weather-collector.weather
  (:require [clj-http.client :as client]))

(def ^:private url "https://api.openweathermap.org/data/2.5/weather")

(defn fetch-temperature
  [city api-key]
  (let [resp (client/get url
                         {:query-params {:q city
                                         :appid api-key
                                         :units "metric"}
                          :as :json})]
    (get-in resp[:body :main :temp])))

(comment
  (fetch-temperature "Berlin" (System/getenv "OPENWEATHER_API_KEY"))
  )
