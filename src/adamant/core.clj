(ns adamant.core
  (:require
    [clojure.string :as str]
    [clojure
     [set :as set :refer [union]]
     [walk :as walk]]
    [lucid.query :as q]
    [clojure.core.specs :as core-specs]
    [clojure.spec :as s]))

(s/def ::ns (s/cat :name simple-symbol? :docstring (s/? string?) :attr-map (s/? map?) :clauses ::core-specs/ns-clauses))

(def fragment {:file "src/adamant/core.clj"})

(s/conform ::ns (rest (first (q/$ fragment [ns]))))
