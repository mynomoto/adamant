(ns adamant.sample
  (:refer-clojure :exclude [ancestors printf])
  (:require [clojure.core.match :refer [match] :as match]
            [clojure.set :as set :refer [union]]
            [clojure.walk :as walk]
            [lucid.query :as q]
            clojure.pprintclojure.string)
  (:import (java.util Date Timer Random)
           (java.sql Connection Statement)))

;; Comment

(def something "something")
