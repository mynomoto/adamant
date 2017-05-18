(ns adamant.specter
  (:require
    [adamant.core :as adamant]
    [clojure.core.match :refer [match]]
    [zprint.core :as zp]
    [com.rpl.specter :as sp]
    [specter-edn.core :refer [SEXPRS]]
    [clojure.spec.alpha :as s]))

(defn conform-ns
  [ns-form]
  (s/conform ::adamant/ns ns-form))

(defn unform-ns
  [conformed-ns]
  (s/unform ::adamant/ns conformed-ns))

(defn transform
  [file-in file-out filter-fn transform-fn file-id]
  (spit file-out
    (zp/zprint-file-str
      (sp/transform
        [SEXPRS sp/ALL (sp/codewalker filter-fn)]
        transform-fn
        (slurp file-in))
      file-id)))

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

(defn normalize-ns
  [conformed-ns]
  (-> conformed-ns
      (update :clauses
        (fn [clauses]
          (mapv (fn [clause]
                  (match clause
                    [:require require] [:require
                                        (update require :libs
                                          #(->> %
                                                (mapv remove-suffix)
                                                (reduce (fn [libs lib]
                                                          (if (vector? (first lib))
                                                            (into libs lib)
                                                            (conj libs lib))) [])))]
                    else else))
            clauses)))))

(defn use-vector-on-require
  [ns-form]
  (for [element ns-form]
    (match element
      ([:require & args] :seq) (apply list (map #(if (seq? %) (vec %) %) element))
      _ element)))

(transform
  "resources/adamant_sample.clj"
  "resources/adamant_sample2.clj"
  (fn [form]
    (when (or (list? form) (vector? form))
      (= 'ns  (first form))))
  (fn [form] (-> form conform-ns normalize-ns unform-ns use-vector-on-require))
  "transform")

(defn simple-transform
  [file-in filter-fn transform-fn]
  (sp/transform
    [SEXPRS sp/ALL (sp/codewalker filter-fn)]
    transform-fn
    (slurp file-in)))

(defn debugf [v k]
  (println k v) v)

(defn debugl [k v]
  (println v k) v)

(defn debug [v]
  (println :debug v) v)

(defn st []
(simple-transform
  "resources/adamant_sample.clj"
  (fn [form]
    (when (or (list? form) (vector? form))
      (= 'ns  (first form))))
  (fn [form] (-> form conform-ns normalize-ns unform-ns use-vector-on-require))))
