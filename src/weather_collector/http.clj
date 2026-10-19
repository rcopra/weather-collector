(ns weather-collector.http
  (:require [cheshire.core :as json]
            [ring.adapter.jetty :as jetty]
            [weather-collector.storage :as storage]))

 (defn make-handler
    [path]
    (fn [request]
      (if (and (= :get (:request-method request))
               (= "/temperatures" (:uri request)))
        {:status 200
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string (storage/read-readings path))}
        {:status 404
         :body "Not found"})))

(defn start-server
    [handler port]
    (jetty/run-jetty handler {:port port :join? false}))

 (comment
    (storage/append-reading! "scratch.csv" "2026-08-25T14:00:00" 21.4)

    ((make-handler "scratch.csv") {:request-method :get :uri "/temperatures"})
    ((make-handler "nope.csv") {:request-method :get :uri "/temperatures"})
    ((make-handler "scratch.csv") {:request-method :get :uri "/nope"})
    ;; a real server directly in the file, cool
    (def server (start-server (make-handler "scratch.csv") 3000))
    (.stop server)
    )
