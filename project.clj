(defproject convert-tool "0.0.1"
  :description "TBD"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"

  :javac-options     ["-target" "1.8" "-source" "1.8"]

  :dependencies [[org.clojure/clojure "1.8.0"]

                 ;[com.taoensso/timbre "4.10.0"]    ; clojure logging

                 [org.slf4j/slf4j-simple "1.7.25"]  ; for the draining of sl4fj-reliant libraries used here

                 [clj-time "0.13.0"]                ; https://github.com/clj-time/clj-time

                 [org.clojure/data.csv "0.1.3"]     ; for csv

                 [org.clojure/math.combinatorics "0.1.4"]

                 [io.aviso/pretty "0.1.33"] ; pretty exceptions in leinigen (http://ioavisopretty.readthedocs.io/en/latest/lein-plugin.html)
                 [mvxcvi/puget "1.0.1"]]    ; color printing function (https://github.com/greglook/puget#usage), see `with-color` and `cprint`

  :profiles {:dev {:dependencies
                    [[org.clojure/tools.trace "0.7.5"]
                     [criterium "0.4.3"]
                     [rhizome "0.2.5"]
                     ;[org.noisesmith.poirot :as poirot]
                     ]}}

  :plugins [[io.aviso/pretty "0.1.33"]      ; pretty exceptions in leinigen, needed here as well as in :dependencies
            [mvxcvi/whidbey "1.3.1"]        ; more colorful repl (https://github.com/greglook/whidbey)
            [lein-codox "0.10.3"]
            [lein-auto "0.1.3"]   ; provides the auto lein command for watching source changes
            [test2junit "1.2.6"]] ; push test results into junit xml format (or sucky html reports) https://github.com/ruedigergad/test2junit

  ; this doesn't work yet ― see https://github.com/weavejester/lein-auto/issues/6
  ; :auto {:default {:paths (:source-paths :java-source-paths :test-paths :java-source-paths "my path")}} ; https://github.com/weavejester/lein-auto#usage

  :codox {:metadata {:doc/format :markdown}}

  :main convert-tool.core) ; treat docstrings as codox extended markdown (https://github.com/weavejester/codox/blob/master/example/src/clojure/codox/markdown.clj)
