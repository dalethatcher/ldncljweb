(ns ldncljweb.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css include-js html5]]))

(defpartial layout [& content]
            (html5
              [:head
               [:title "ldncljweb"]
               (include-css "/css/reset.css")
               (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js")
               (include-js "/js/cljs.js")
               ]
              [:body
               [:div#wrapper
                content]]))
