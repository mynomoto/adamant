(ns adamant.core
  (:require
    [clojure.core.specs.alpha :as core-specs]
    [clojure.spec.alpha :as s]))

(s/def ::exclude (s/coll-of simple-symbol?))
(s/def ::only (s/coll-of simple-symbol?))
(s/def ::rename (s/map-of simple-symbol? simple-symbol?))
(s/def ::filters (s/keys* :opt-un [::exclude ::only ::rename]))

(s/def ::ns-refer-clojure
  (s/spec (s/cat :clause #{:refer-clojure}
                 :filters ::filters)))

(s/def ::local-name (s/and simple-symbol? #(not= '& %)))

(s/def ::as ::local-name)

(s/def ::refer (s/or :all #{:all}
                     :syms (s/coll-of simple-symbol?)))

(s/def ::prefix-list
  (s/spec
    (s/cat :prefix simple-symbol?
           :suffix (s/* (s/alt :lib simple-symbol? :prefix-list ::prefix-list))
           :refer (s/keys* :opt-un [::as ::refer])
           :refer-macros (s/keys* :opt-un [::as ::refer])
           :include-macros (s/? #{true}))))

(s/def ::ns-require
  (s/spec (s/cat :clause #{:require}
                 :libs (s/* (s/alt :lib simple-symbol?
                                   :prefix-list ::prefix-list
                                   :flag #{:reload :reload-all :verbose})))))

(s/def ::ns-require-macros
  (s/spec (s/cat :clause #{:require-macros}
                 :libs (s/* (s/alt :lib simple-symbol?
                                   :prefix-list ::prefix-list
                                   :flag #{:reload :reload-all :verbose})))))

(s/def ::package-list
  (s/spec
    (s/cat :package simple-symbol?
           :classes (s/* simple-symbol?))))

(s/def ::import-list
  (s/* (s/alt :class simple-symbol?
              :package-list ::package-list)))

(s/def ::ns-import
  (s/spec
    (s/cat :clause #{:import}
           :classes ::import-list)))

(s/def ::use-prefix-list
  (s/spec
    (s/cat :prefix simple-symbol?
           :suffix (s/* (s/alt :lib simple-symbol? :prefix-list ::use-prefix-list))
           :filters ::filters)))

(s/def ::ns-use
  (s/spec (s/cat :clause #{:use}
            :libs (s/* (s/alt :lib simple-symbol?
                              :prefix-list ::use-prefix-list
                              :flag #{:reload :reload-all :verbose})))))

(s/def ::ns-use-macros
  (s/spec (s/cat :clause #{:use-macros}
            :libs (s/* (s/alt :lib simple-symbol?
                              :prefix-list ::use-prefix-list
                              :flag #{:reload :reload-all :verbose})))))

(s/def ::ns-refer
  (s/spec (s/cat :clause #{:refer}
                 :lib simple-symbol?
                 :filters ::filters)))

(s/def ::ns-load
  (s/spec (s/cat :clause #{:load}
                 :libs (s/* string?))))

(s/def ::name simple-symbol?)
(s/def ::extends simple-symbol?)
(s/def ::implements (s/coll-of simple-symbol? :kind vector?))
(s/def ::init symbol?)
(s/def ::class-ident (s/or :class simple-symbol? :class-name string?))
(s/def ::signature (s/coll-of ::class-ident :kind vector?))
(s/def ::constructors (s/map-of ::signature ::signature))
(s/def ::post-init symbol?)
(s/def ::method (s/and vector?
                  (s/cat :name simple-symbol?
                         :param-types ::signature
                         :return-type simple-symbol?)))
(s/def ::methods (s/coll-of ::method :kind vector?))
(s/def ::main boolean?)
(s/def ::factory simple-symbol?)
(s/def ::state simple-symbol?)
(s/def ::get simple-symbol?)
(s/def ::set simple-symbol?)
(s/def ::expose (s/keys :opt-un [::get ::set]))
(s/def ::exposes (s/map-of simple-symbol? ::expose))
(s/def ::prefix string?)
(s/def ::impl-ns simple-symbol?)
(s/def ::load-impl-ns boolean?)

(s/def ::ns-gen-class
  (s/spec (s/cat :clause #{:gen-class}
                 :options (s/keys* :opt-un [::name ::extends ::implements
                                            ::init ::constructors ::post-init
                                            ::methods ::main ::factory ::state
                                            ::exposes ::prefix ::impl-ns ::load-impl-ns]))))

(s/def ::ns-clauses
  (s/* (s/alt :refer-clojure ::ns-refer-clojure
              :require ::ns-require
              :import ::ns-import
              :use ::ns-use
              :refer ::ns-refer
              :load ::ns-load
              :gen-class ::ns-gen-class
              :require-macros ::ns-require-macros
              :use-macros ::ns-use-macros)))

(s/def ::ns
  (s/cat
    :form #{'ns}
    :name simple-symbol?
    :docstring (s/? string?)
    :attr-map (s/? map?)
    :clauses ::ns-clauses))
