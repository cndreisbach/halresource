(defproject halresource "0.2.0-SNAPSHOT"
  :description "halresource is a library for building and representing resources in Hypertext Application Format (http://stateless.co/hal_specification.html)."
  :url "https://github.com/cndreisbach/halresource"
  :license {:name "Unlicense"
            :url "http://unlicense.org/"}
  :min-lein-version "2.0.0"
  :plugins [[lein-midje "3.0.0"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.xml "0.0.7"]                 
                 [cheshire "5.1.1"]
                 [inflections "0.8.1"]]
  :profiles {:dev {:dependencies [[midje "1.6-SNAPSHOT"]]}})
