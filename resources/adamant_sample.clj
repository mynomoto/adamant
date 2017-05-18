(ns adamant.sample
  (:refer-clojure :exclude [ancestors printf])
  (:require
    clojure.pprint
    [clojure.core.match :refer [match] :as match]
    [lucid.query :as q]
    [clojure
     string
     [set :as set :refer [union]]
     [walk :as walk]])
  (:import (java.util Date Timer Random)
           (java.sql Connection Statement)))

;; Comment

(def something
  "something")
