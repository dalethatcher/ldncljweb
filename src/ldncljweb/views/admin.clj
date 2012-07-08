(ns ldncljweb.views.admin
  (:use [noir.core :only [defpage]]
        [noir.response :only [json]])
  (:require [ldncljweb.models.posts :as posts])
  (:import [org.joda.time DateTime]
           [org.joda.time.format ISODateTimeFormat]))

(defpage "/admin/posttitles" []
  (json (posts/find-all-posts-titles)))

(defpage "/admin/posts/:id" {id :id}
  (json (posts/find-post id)))

(defn parse-date [date]
  (try (.parseDateTime (ISODateTimeFormat/dateTimeParser) date)
    (catch Exception _ (DateTime.))))

(defpage [:post "/admin/posts/new"] {:keys [title date body]}
  (posts/create-post (parse-date date)
                     title
                     body)
  "")

(defpage [:put "/admin/posts/:id"] {id :id :keys [title date body]}
  (posts/update-post id (parse-date date) title body)
  "")

(defpage [:delete "/admin/posts/:id"] {id :id}
  (posts/delete-post id)
  "")