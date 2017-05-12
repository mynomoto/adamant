(ns adamant.core
  (:require
    clojure.string
    [clojure.walk]
    [clojure.core.match :refer [match]]
    [clojure.java
     [io :as io :refer [reader]]]
    [clojure.pprint :as pprint]
    [clojure set walk
     [set :as set :refer [union]]
     [walk :as walk]]
    [lucid.query :as q]
    [com.rpl.specter :as sp]
    [specter-edn.core :as spe]
    [clojure.core.specs.alpha :as core-specs]
    [clojure.spec.alpha :as s])
  (:import [java.io FileReader BufferedReader]))

(s/def ::ns (s/cat :form #{'ns} :name simple-symbol? :docstring (s/? string?) :attr-map (s/? map?) :clauses ::core-specs/ns-clauses))

; Using lucid.query
(def fragment {:file "src/adamant/core.clj"})

(def conformed
  (s/conform ::ns (first (q/$ fragment [ns]))))

; Using specter
(def self-file
  (slurp "src/adamant/core.clj"))

(defn valid-ns
  []
  (s/conform ::ns
             (first
               (sp/select [spe/SEXPRS sp/ALL (sp/codewalker (fn [form]  (when (or (list? form) (vector? form)) (= 'ns  (first form)))))] self-file))))

(defn append-prefix
  [prefix suffix]
  (match suffix
    [:lib lib] [:lib (symbol (str (name prefix) "." (name lib)))]
    [:prefix-list prefix-list] [:prefix-list (update prefix-list :prefix #(symbol (str (name prefix) "." (name %))))]))

(defn remove-suffix
  [lib]
  (match lib
    [:prefix-list {:prefix prefix
                   :suffix suffix}] (mapv #(append-prefix prefix %) suffix)
    else else))

(def sample
  '{:form ns,
    :name adamant.core,
    :clauses
    [[:require
      {:clause :require,
       :libs
       [[:lib clojure.string]
        [:prefix-list {:prefix clojure.walk}]
        [:prefix-list
         {:prefix clojure.core.match, :refer {:refer [:syms [match]]}}]
        [:prefix-list
         {:prefix clojure.java,
          :suffix
          [[:prefix-list
            {:prefix io, :refer {:as io, :refer [:syms [reader]]}}]]}]
        [:prefix-list {:prefix clojure.pprint, :refer {:as pprint}}]
        [:prefix-list
         {:prefix clojure,
          :suffix
          [[:lib set]
           [:lib walk]
           [:prefix-list
            {:prefix set, :refer {:as set, :refer [:syms [union]]}}]
           [:prefix-list {:prefix walk, :refer {:as walk}}]]}]
        [:prefix-list {:prefix lucid.query, :refer {:as q}}]
        [:prefix-list {:prefix com.rpl.specter, :refer {:as sp}}]
        [:prefix-list {:prefix specter-edn.core, :refer {:as spe}}]
        [:prefix-list
         {:prefix clojure.core.specs.alpha, :refer {:as core-specs}}]
        [:prefix-list {:prefix clojure.spec.alpha, :refer {:as s}}]]}]
     [:import
      {:clause :import,
       :classes
       [[:package-list
         {:package java.io, :classes [FileReader BufferedReader]}]]}]]})

(s/unform ::ns sample)

(defn normalize-ns
  [conformed-ns]
  (-> conformed-ns
      (update :clauses
        (fn [clauses] (mapv (fn [clause]
                              (match clause
                                [:require require] [:require (update require :libs #(mapv remove-suffix %))]
                                else else)) clauses)))))
