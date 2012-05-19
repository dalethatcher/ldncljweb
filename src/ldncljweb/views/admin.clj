(ns ldncljweb.views.admin
  (:use [noir.core :only [defpage]]
        [noir.response :only [json]])
  (:require [ldncljweb.models.posts :as posts]))

(defpage "/admin/posts/titles" []
  (json (posts/find-all-posts-titles)))