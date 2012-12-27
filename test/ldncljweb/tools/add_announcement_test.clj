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

(def parsed-header {:Content-Type "multipart/alternative; boundary=1234"})

(deftest can-find-message-boundary
  (is (= "1234" (aa/parse-boundary-from-header parsed-header))))

(def multipart-body (str "--boundary\n"
                         "Content-Type: text/plain; charset=ISO-8859-1\n"
                         "\n"
                         "Some text\n"
                         "--boundary\n"
                         "Content-Type: text/html; charset=ISO-8859-1\n"
                         "\n"
                         "<b>Some text</b>\n"
                         "--boundary--\n"))

(deftest can-split-body-by-boundary
  (let [parts-by-type (aa/parse-body-into-types "boundary" multipart-body)]
    (is (= (parts-by-type "text/plain") "Some text\n"))
    (is (= (parts-by-type "text/html") "<b>Some text</b>\n"))))
  
(def multipart-message (str "Subject: Some subject\n" 
                            "Content-Type: multipart/alternative; boundary=1234\n"
                            "\n"
                            "--1234\n"
                            "Content-Type: text/plain; charset=ISO-8859-1\n"
                            "\n"
                            "Plain text body.\n"
                            "\n"
                            "--1234\n"
                            "Content-Type: text/html; charset=ISO-8859-1\n"
                            "Content-Transfer-Encoding: quoted-printable\n"
                            "\n"
                            "<b>HTML body.</b>\n"
                            "--1234--\n"))

(deftest can-parse-message-into-types
  (let [[header message-bodies-by-type] (aa/parse-message-by-type multipart-message)]
    (is (= (header :Subject) "Some subject"))
    (is (= (message-bodies-by-type "text/html") "<b>HTML body.</b>\n"))))

(def quoted-printable-example (str "first line encoded here=\n"
                                   "this=3Dsecond\n"
                                   "and the third\n"))

(deftest can-deconvert-quoted-printable
  (is (= "first line encoded herethis=second\nand the third\n" (aa/parse-quoted-printable quoted-printable-example))))