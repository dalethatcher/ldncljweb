(ns ldncljweb.views.root
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]
        [net.cgrand.enlive-html :only [deftemplate append html-snippet]])
  (:import [java.util Date]))

(deftemplate main "public/index.html" [message]
    [:#posts]
             (append (html-snippet (html [:p message]))))

(defpage "/" []
         (main (str "Hello templates! The time is " (Date.))))
