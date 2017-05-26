(ns adamant.sample
  (:refer-clojure :exclude [ancestors printf])
  (:require [clojure.walk :as walk]
            [clojure.set :as set :refer [union]]
            [lucid.query :as q]
            [clojure.core.match :refer [match] :as match]
            clojure.pprint
            clojure.string)
  (:import (java.util Date Timer Random)
           (java.sql Connection Statement)))

;; Comment

(def something "something")
