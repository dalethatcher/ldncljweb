(ns ldncljweb.admin
  (:require [jayq.core :as jq]
            [jayq.util :as util]))

(defn load-post-data [id title]
  (let [success-fn (fn [data status xhr]
                     (.val (jq/$ :#post-id) (data "_id"))
                     (.val (jq/$ :#post-data) (data "date"))
                     (.val (jq/$ :#post-title) (data "title"))
                     (.val (jq/$ :#post-edit-area) (data "body")))]
    (.val (jq/$ :#post-edit-area) (str "Loading" title "..."))
    (.ajax js/jQuery (str "/admin/posts/" id) {:dataType "json" :success success-fn})))

(defn- load-posts-success-fn [data status xhr]
  (let
      [posts (map post-format-fn data)]
    (.append posts-list-element (.join posts ""))
    (.click
     (jq/$ "#posts-list div")
     (fn []
       (remove-class (jq/$ "#posts-list div") "selected")
       (this-as el
                (add-class el "selected")
                (load-post-data
                 (.attr el "data-id")
                 (.text el)))))
    ))

(defn load-posts []
  (let [posts-list-element (jq/$ :#posts-list)
        post-format-fn (fn [k post]
                         (str "<div data-id=\"" (post "id_") "\">"
                              (.html (.text (jq/$ "div") (post "title")))
                              "</div>"))
        ajax-url "/admin/posttitles"
        ajax-params {:dataType "json" :success load-posts-success-fn}]
    (.ajax js/jQuery ajax-url ajax-params)))

(defn clear-post-fields []
  (.val (jq/$ :#post-id) "")
  (.val (jq/$ :#post-title) "")
  (.val (jq/$ :#post-date) "")
  (.val (jq/$ :#post-edit-area) "")
  (jq/remove-class (jq/$ "#posts-list div")  "selected")
  (util/log "Our clear-post-fields called"))

(defn- post-save-click []
  (let [post-id-element (jq/$ :#post-id)
        post-id (.val post-id-element)
        ajax-params {:data {:title (.val (jq/$ :#post-title))
                            :date (.val (jq/$ :#post-date))
                            :body (.val (jq/$ :#post-edit-area))}
                     :success (fn [] (load-posts))}]
    (if (= (.-length post-id) 0)
      (.ajax js/jQuery (assoc ajax-params :url "/admin/posts/new" :type "POST"))
      (.ajax js/jQuery (assoc ajax-params :url (str "/admin/posts/" post-id) :type "PUT"))
    )))

(defn ^:export main
  []
  (jq/$
   (fn []
     (.click (.button (jq/$ :#post-save)) post-save-click)
     )))
