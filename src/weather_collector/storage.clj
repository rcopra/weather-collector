(ns weather-collector.storage
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def ^:private header "timestamp,temperature\n")

(defn csv-path
  [dir city]
  (str (io/file dir (str (str/lower-case city) ".csv"))))

(defn append-reading!
  [path timestamp temperature]
  (when-not(.exists (io/file path))
    (spit path header))
  (spit path (str timestamp "," temperature "\n") :append true))

(defn read-readings
  [path]
  (if (.exists (io/file path))
    (->> (slurp path)
         str/split-lines
         rest
         (mapv (fn [line]
                 (let [[timestamp temperature] (str/split line #",")]
                   {:timestamp timestamp
                    :temperature (parse-double temperature)}))))
    []))

(comment
  (str (io/file "." (str (str/lower-case "Berlin") ".csv")))
  (csv-path "." "berlin")
  (spit "scratch.csv" "timestamp,temperature\n")
  (spit "scratch.csv" "2026-08-25T14:00:00,21.4\n" :append true)
  (slurp "scratch.csv")
  (.exists (io/file "scratch.csv"))
  (.exists (io/file "nope.csv"))
  (append-reading! "scratch2.csv" "2026-08-25T14:00:00" 21.4)

  ;; need to split on comma in func
  (rest (str/split-lines(slurp "scratch2.csv")))
  (read-readings "scratch2.csv")
  (read-readings "nope.csv")
  )
