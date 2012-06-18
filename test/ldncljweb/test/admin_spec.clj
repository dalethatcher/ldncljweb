(ns ldncljweb.test.admin-spec
  (:require [noir.util.test :as n]
            [ldncljweb.views.admin]
            [ldncljweb.models.posts :as posts])
  (:use [clojure.test :only [deftest run-tests]]))

(deftest open-admin-posttitles-page
  (with-redefs [posts/find-all-posts-titles list]
    (doto (n/send-request "/admin/posttitles")
      (n/has-status 200)
      (n/has-content-type "application/json"))))

(deftest open-admin-post-page
  (with-redefs [posts/find-post (fn [id] nil)]
    (doto (n/send-request "/admin/posts/1")
      (n/has-status 200)
      (n/has-content-type "application/json"))))

(deftest open-admin-create-post-page
  (with-redefs [posts/create-post (fn [a b c] "")]
    (n/has-status
      (n/send-request [:post "/admin/posts/new"] nil nil nil)
      200)))

(deftest open-admin-modify-post-page
  (with-redefs [posts/update-post (fn [id a b c] "")]
    (n/has-status
      (n/send-request [:put "/admin/posts/1"] nil nil nil)
      200)))

(deftest open-admin-delete-post-page
  (with-redefs [posts/delete-post (fn [id] "")]
    (n/has-status
      (n/send-request [:delete "/admin/posts/1"])
      200)))