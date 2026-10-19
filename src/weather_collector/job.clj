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
      (println "Fetch failed, will retry next cycle:" (.getMessage e)))))

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
  (collect! (fn [] (throw (Exception. "boom"))) "scratch.csv")

  (def job (start-schedule #(collect! (fn [] 22.0) "scratch.csv")))
  (slurp "scratch.csv")
  ;; eval below or the job keeps looping
  (future-cancel job)
  )
