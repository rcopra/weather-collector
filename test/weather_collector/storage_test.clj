(ns weather-collector.storage-test
  (:require [weather-collector.storage :as sut]
            [clojure.java.io :as io]
            [clojure.test :as t]))

(def path "test-storage.csv")

(t/use-fixtures :each (fn [f]
                        (io/delete-file path true)
                        (f)
                        (io/delete-file path true)))

(t/deftest csv-path-uses-lower-case-city
  (t/is (= "./berlin.csv" (sut/csv-path "." "Berlin"))))

(t/deftest header-is-written-once
  (sut/append-reading! path "2026-08-25T14:00:00" 21.4)
  (sut/append-reading! path "2026-08-25T15:00:00" 22.0)
  (t/is (= (str "timestamp,temperature\n"
                "2026-08-25T14:00:00,21.4\n"
                "2026-08-25T15:00:00,22.0\n")
           (slurp path))))

(t/deftest read-readings-parses-rows
  (sut/append-reading! path "2026-08-25T14:00:00" 21.4)
  (t/is (= [{:timestamp "2026-08-25T14:00:00" :temperature 21.4}]
           (sut/read-readings path))))

(t/deftest read-readings-missing-file
  (t/is (= [] (sut/read-readings path))))
