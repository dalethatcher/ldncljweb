(defproject ldncljweb "0.1.0-SNAPSHOT"
            :description "FIXME: write this!"
            :dependencies [[org.clojure/clojure "1.3.0"]
                           [noir "1.2.1"]
                           [enlive "1.0.0"]
                           [congomongo "0.1.9"]
                           [clj-time "0.4.2"]
                           ;; ClojureScript
                           [jayq "0.1.0-alpha2"]
                           ;; [fetch "0.1.0-alpha2"]
                           ]
            :dev-dependencies [[lein-cljsbuild "0.0.13"]] ; cljsbuild plugin
            :cljsbuild {:source-path "src-cljs"
                        :compiler {:output-to "resources/public/js/cljs.js"
                                   :optimizations :simple
                                   :pretty-print true
                                   ;; :externs ["externs/jquery.js"]
                                   }
                        }
            :main ldncljweb.server)
