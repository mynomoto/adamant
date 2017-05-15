(ns adamant.lucid
  (:require
    [adamant.core :as adamant]
    [lucid.query :as q]
    [clojure.spec.alpha :as s]))

(def fragment {:file "resources/adamant_sample.clj"})

(def conformed
  (s/conform ::adamant/ns (first (q/$ fragment [ns]))))
