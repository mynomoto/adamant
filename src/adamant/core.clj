(ns adamant.core
  (:require
    [clojure.core.specs.alpha :as core-specs]
    [clojure.spec.alpha :as s]))

(s/def ::ns
  (s/cat
    :form #{'ns}
    :name simple-symbol?
    :docstring (s/? string?)
    :attr-map (s/? map?)
    :clauses ::core-specs/ns-clauses))
