(ns weather-collector.job-test
  (:require [weather-collector.job :as sut]
            [weather-collector.storage :as storage]
            [clojure.java.io :as io]
            [clojure.test :as t]))

(def path "test-job.csv")

(t/use-fixtures :each (fn [f]
                        (io/delete-file path true)
                        (f)
                        (io/delete-file path true)))

(t/deftest collect-appends-a-reading
  (sut/collect! (fn [] 21.4) path)
  (t/is (= 21.4 (:temperature (first (storage/read-readings path))))))

(t/deftest collect-survives-a-failed-fetch
  (sut/collect! (fn [] (throw (Exception. "boom"))) path)
  (t/is (= [] (storage/read-readings path))))
