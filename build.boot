(def project 'adamant)
(def version "0.1.0-SNAPSHOT")

(set-env! :resource-paths #{"src"}
          :source-paths   #{"test"}
          :dependencies   '[[org.clojure/clojure "1.9.0-alpha16"]
                            [org.clojure/core.match "0.3.0-alpha4"]
                            [metosin/boot-alt-test "0.3.2"]

                            [com.maitria/specter-edn "0.1.1"]
                            [im.chit/lucid.query "1.3.7"]
                            [im.chit/hara "2.5.2"]])

(task-options!
 pom {:project     project
      :version     version
      :description "Keep your namespaces strict"
      :url         "https://github.com/yourname/adamant"
      :scm         {:url "https://github.com/mynomoto/adamant"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask build
  "Build and install the project locally."
  []
  (comp (pom) (jar) (install)))

(require '[metosin.boot-alt-test :refer [alt-test]])
