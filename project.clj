(defproject weather-collector "0.1.0-SNAPSHOT"
  :description "Fetches a city's temperature hourly, appends it to a CSV file and serves it over HTTP."
  :dependencies [[org.clojure/clojure "1.12.6"]
                 [clj-http "3.13.1"]
                 [cheshire "6.2.0"]
                 [ring/ring-core "1.13.0"]
                 [ring/ring-jetty-adapter "1.13.0"]]
  :main ^:skip-aot weather-collector.main
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
