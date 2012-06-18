(ns ldncljweb.test.root-spec
  (:require [noir.util.test :as n]
            [ldncljweb.views.root]
            [ldncljweb.models.posts :as posts])
  (:use [clojure.test :only [deftest run-tests]]))

(deftest open-main-page
  (with-redefs [posts/find-most-recent-five list]
    (n/has-status
      (n/send-request "/")
      200)))