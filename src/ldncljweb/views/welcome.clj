(ns ldncljweb.views.welcome
  (:require [ldncljweb.views.common :as common]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to ldncljweb"]))
