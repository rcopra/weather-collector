(ns weather-collector.job
  (:require [weather-collector.storage :as storage])
  (:import (java.time LocalDateTime ZoneOffset)
           (java.time.format DateTimeFormatter)))

(defn- now
  []
  (.format (LocalDateTime/now ZoneOffset/UTC)
           (DateTimeFormatter/ofPattern "yyyy-MM-dd'T'HH:mm:ss")))

(defn collect!
  [fetch path]
  (try
    (storage/append-reading! path (now) (fetch))
    (catch Exception e
      (println (now) "Fetch failed, will retry next cycle:"
               (str (ex-message e) " " (:body (ex-data e)))))))

(defn start-schedule
  [task]
  (future
    (loop []
      (task)
      (Thread/sleep (* 60 60 1000))
      (recur))))

(comment
  (now)
  (collect! (fn [] 21.4) "scratch.csv")
  (slurp "scratch.csv")

  (def job (start-schedule #(collect! (fn [] 22.0) "scratch.csv")))
  (slurp "scratch.csv")
  ;; eval below or the job keeps looping
  (future-cancel job)

  ;; only got status on bad config, need missing api key message
  (require '[weather-collector.weather :as weather])
  (weather/fetch-temperature "Berlin" "bad-key")
  (class *e)
  (clojure.repl/doc ex-info)
  (ex-data *e)
  (:status (ex-data *e))
  (:body (ex-data *e))

  ;; collect! now prints the reason
  (collect! #(weather/fetch-temperature "Berlin" "bad-key") "scratch.csv")
  (collect! (fn [] 21.4) "/no/such/dir/x.csv")
  (collect! (fn [] (throw (Exception. "boom"))) "scratch.csv")
  )
