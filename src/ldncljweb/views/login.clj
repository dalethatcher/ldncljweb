(ns ldncljweb.views.login
  (:use [noir.core :only [defpage]]
        [noir.response :only [json]])
  (:require [ldncljweb.models.posts :as posts]))

(defpage "/admin/posts/:id" {id :id}
  (json (posts/find-post id)))

(defpage [:post "/login"] {assertion :assertion}
  (json {:hai assertion
         :user "bob"}))
