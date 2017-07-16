(ns adamant.specter
  (:require
    [adamant.core :as adamant]
    [clojure.core.match :refer [match]]
    [hara.io.file :as f]
    [com.rpl.specter :as sp]
    [specter-edn.core :refer [SEXPRS]]
    [clojure.spec.alpha :as s]))

(defn conform-ns
  [ns-form]
  (s/conform ::adamant/ns ns-form))

(defn short-circuit-invalid
  [conformed-ns]
  (when-not (= conformed-ns :clojure.spec.alpha/invalid)
    conformed-ns))

(defn unform-ns
  [conformed-ns]
  (s/unform ::adamant/ns conformed-ns))

(defn transform
  [file-in file-out filter-fn transform-fn file-id]
  (spit file-out
    (sp/transform
      [SEXPRS sp/ALL (sp/codewalker filter-fn)]
      transform-fn
      (slurp file-in))))

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
      ([:require & args] :seq) (apply list :require (sort-by str (mapv #(if (seq? %) (vec %) %) args)))
      ([:require-macros & args] :seq) (apply list :require-macros (sort-by str (mapv #(if (seq? %) (vec %) %) args)))
      _ element)))

(defn fix-ns
  [file]
  (try
    (transform
      file
      file
      (fn [form]
        (when (or (list? form) (vector? form))
          (= 'ns  (first form))))
      (fn [form] (if-let [new-ns (some-> form conform-ns short-circuit-invalid normalize-ns unform-ns use-vector-on-require)]
                   new-ns
                   form))
      file)
    (catch Exception e
      (str file ": " (pr-str e)))))

(defn fix-project-ns
  [path]
  (->> (f/select path {:exclude [f/directory? ".git/"] :recursive true :include [".clj$" ".cljs$" ".edn$" ".cljc$"]})
       (mapv str)
       (mapv fix-ns)))
