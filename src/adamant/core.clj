(ns adamant.core
  (:require
    clojure.string
    [clojure.java
     [io :as io :refer [reader]]]
    [clojure set walk
     [set :as set :refer [union]]
     [walk :as walk]]
    [lucid.query :as q]
    [clojure.core.specs :as core-specs]
    [clojure.spec :as s]))

(s/def ::ns (s/cat :name simple-symbol? :docstring (s/? string?) :attr-map (s/? map?) :clauses ::core-specs/ns-clauses))

(def fragment {:file "src/adamant/core.clj"})

(def conformed
  (s/conform ::ns (rest (first (q/$ fragment [ns])))))

'{:name adamant.core
 :clauses [[:require {:clause :require
                      :libs [[:prefix-list {:prefix clojure.string
                                            :refer {:as str}}]
                             [:prefix-list {:prefix clojure.java
                                            :suffix [[:prefix-list {:prefix io
                                                                    :refer {:as io
                                                                            :refer [:syms [reader]]}}]]}]
                             [:prefix-list {:prefix clojure
                                            :suffix [[:prefix-list {:prefix set
                                                                    :refer {:as set
                                                                            :refer [:syms [union]]}}]
                                                     [:prefix-list {:prefix walk
                                                                    :refer {:as walk}}]]}]
                             [:prefix-list {:prefix lucid.query
                                            :refer {:as q}}]
                             [:prefix-list {:prefix clojure.core.specs
                                            :refer {:as core-specs}}]
                             [:prefix-list {:prefix clojure.spec
                                            :refer {:as s}}]]}]]}

'{:name adamant.core
 :clauses [[:require {:clause :require
                      :libs [[:lib clojure.string]
                             [:prefix-list {:prefix clojure.java
                                            :suffix [[:prefix-list {:prefix io
                                                                    :refer {:as io
                                                                            :refer [:syms [reader]]}}]]}]
                             [:prefix-list {:prefix clojure
                                            :suffix [[:lib set]
                                                     [:lib walk]
                                                     [:prefix-list {:prefix set
                                                                    :refer {:as set
                                                                            :refer [:syms [union]]}}]
                                                     [:prefix-list {:prefix walk
                                                                    :refer {:as walk}}]]}]
                             [:prefix-list {:prefix lucid.query
                                            :refer {:as q}}]
                             [:prefix-list {:prefix clojure.core.specs
                                            :refer {:as core-specs}}]
                             [:prefix-list {:prefix clojure.spec
                                            :refer {:as s}}]]}]]}
