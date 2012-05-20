(ns ldncljweb.views.admin
  (:use [noir.core :only [defpage]]
        [noir.response :only [json]])
  (:require [ldncljweb.models.posts :as posts]))

(defpage "/admin/posttitles" []
  (json (posts/find-all-posts-titles)))

(defpage "/admin/posts/:id" {id :id}
  (json (posts/find-post id)))