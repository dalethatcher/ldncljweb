(ns ldncljweb.views.root
  (:use [noir.core :only [defpage]]
        [noir.response :only [content-type]]
        [hiccup.core :only [html]]
        [net.cgrand.enlive-html :only [deftemplate append html-snippet]])
  (:require [ldncljweb.models.posts :as posts])
  (:import [java.util Date]))

(deftemplate main "public/index.html" [message]
    [:#posts]
             (append (html-snippet (html message))))

(defpage "/" []
  (content-type "text/html"
    (main (html (map (fn [p] [:div {:class "post"}
                              [:h1 (p :title)]
                              [:p {class "date"} (p :date)]
                              [:p {class "post-body"} (p :body)]])
                     (posts/find-most-recent-five))))))
