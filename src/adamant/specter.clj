(ns adamant.specter
  (:require
    [adamant.core :as adamant]
    [clojure.core.match :refer [match]]
    [zprint.core :as zp]
    [com.rpl.specter :as sp]
    [specter-edn.core :refer [SEXPRS]]
    [clojure.spec.alpha :as s]))

(def sample-file
  (slurp "resources/adamant_sample.clj"))

(defn conformed-ns
  [file-string]
  (s/conform ::adamant/ns
             (first
               (sp/select [SEXPRS sp/ALL (sp/codewalker (fn [form]  (when (or (list? form) (vector? form)) (= 'ns  (first form)))))] file-string))))

(defn t
  [file-in file-out]
  (spit file-out
    (zp/zprint-file-str
      (sp/transform
        [SEXPRS sp/ALL (sp/codewalker
                         (fn [form]
                           (when (or (list? form) (vector? form))
                             (= 'ns  (first form)))))]
        (fn [form] form #_(list (first form) (symbol "adamant.core")))
        (slurp file-in))
      "xxx")))

(t "resources/adamant_sample.clj" "resources/adamant_sample2.clj" )

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
        (fn [clauses] (mapv (fn [clause]
                                   (match clause
                                     [:require require] [:require (update require :libs #(->> %
                                                                                              (mapv remove-suffix)
                                                                                              (reduce (fn [libs lib]
                                                                                                        (if (vector? (first lib))
                                                                                                          (into libs lib)
                                                                                                          (conj libs lib))) [])))]
                                     else else)) clauses)))))
