(ns ldncljweb.tools.add-announcement-test
  (:use [clojure.test])
  (:require [ldncljweb.tools.add-announcement :as aa]))

(def example (slurp "doc/announcement"))

(def simple-message (str "header-1: value-1\n"
                         "header-2: value-2\n"
                         "\n"
                         "This is the body\nof the message"))

(deftest can-read-parse-header-and-body
  (let [[head body] (aa/parse-message simple-message)]
    (is (= head {:header-1 "value-1" :header-2 "value-2"}))
    (is (= body "This is the body\nof the message"))))

(def multipart-body (str "--boundry"
                         "Content-Type: text/plain; charset=ISO-8859-1\n"
                         "\n"
                         "Some text\n"
                         "--boundry"
                         "Content-Type: text/html; charset=ISO-8859-1\n"
                         "\n"
                         "<b>Some text</b>\n"
                         "--boundry--"))

(deftest can-split-body-by-boundry
  (let [parts-by-type (aa/parse-body-into-types "boundry" multipart-body)]
    (is (= (parts-by-type "text/plain") "Some text\n"))
    (is (= (parts-by-type "text/html") "<b>Some text</b>\n"))))
  