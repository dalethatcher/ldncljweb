(ns ldncljweb.tools.add-announcement-test
  (:use [clojure.test]
        [ldncljweb.tools.add-announcement]))

(def example (slurp "doc/announcement"))

(def simple-example (str "header-1: value-1\n"
                         "header-2: value-2\n"
                         "\n"
                         "This is the body\nof the message"))

(deftest can-read-parse-header-and-body
  (let [[head body] (parse-message simple-example)]
    (is (= head {:header-1 "value-1" :header-2 "value-2"}))
    (is (= body "This is the body\nof the message"))))
