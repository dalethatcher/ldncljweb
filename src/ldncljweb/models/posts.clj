(ns ldncljweb.models.posts
  (:require [somnium.congomongo :as cm]
            [somnium.congomongo.coerce :as cmc])
  (:import [org.joda.time DateTime]))

(extend-protocol cmc/ConvertibleFromMongo
  java.util.Date
  (mongo->clojure [^java.util.Date d keywordize] (new org.joda.time.DateTime d)))

(extend-protocol cmc/ConvertibleToMongo
  org.joda.time.DateTime
  (clojure->mongo [^org.joda.time.DateTime dt] (.toDate dt)))

(def conn (cm/make-connection "ldncljweb"
                           :host "127.0.0.1"
                           :port 27017))

(cm/set-write-concern conn :safe)

(defn create-post [date title body]
  (cm/with-mongo conn
    (cm/insert! :posts {:date date :title title :body body})))

(defn find-all-posts []
  (cm/with-mongo conn
    (cm/fetch :posts)))

(defn find-most-recent-five []
  (cm/with-mongo conn
    (cm/fetch :posts 
              :sort {:date -1} 
              :limit 5)))