(ns weather-collector.http-test
  (:require [weather-collector.http :as sut]
            [weather-collector.storage :as storage]
            [clojure.java.io :as io]
            [clojure.test :as t]))

(def path "test-http.csv")

(t/use-fixtures :each (fn [f]
                        (io/delete-file path true)
                        (f)
                        (io/delete-file path true)))

(def request {:request-method :get :uri "/temperatures"})

(t/deftest temperatures-returns-json
  (storage/append-reading! path "2026-08-25T14:00:00" 21.4)
  (let [resp ((sut/make-handler path) request)]
    (t/is (= 200 (:status resp)))
    (t/is (= "[{\"timestamp\":\"2026-08-25T14:00:00\",\"temperature\":21.4}]"
             (:body resp)))))

(t/deftest temperatures-empty-before-first-fetch
  (t/is (= "[]" (:body ((sut/make-handler path) request)))))

(t/deftest unknown-route-is-404
  (t/is (= 404 (:status ((sut/make-handler path)
                         {:request-method :get :uri "/nope"})))))
