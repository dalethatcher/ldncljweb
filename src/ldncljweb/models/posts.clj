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

(defn post-id-to-string [row-values]
  (if (:_id row-values) (conj row-values [:_id (str (:_id row-values))])
    row-values))

(defn post-date-to-string [row-values]
  (if (:date row-values) (conj row-values [:date (str (:date row-values))])
    row-values))

(defn find-all-posts-titles []
  (map post-id-to-string
       (cm/with-mongo conn
         (cm/fetch :posts 
                   :only [:title]
                   :sort {:date -1}))))

(defn find-post-raw [id]
  (cm/with-mongo conn
                 (cm/fetch-by-id :posts (cm/object-id id))))                                 
                                 
(defn find-post [id]
  (post-date-to-string
    (post-id-to-string 
      (find-post-raw id))))

(defn update-post [id date title body]
  (cm/with-mongo conn
          (cm/update! :posts (find-post-raw id)
                      {:date date
                       :title title
                       :body body})))

(defn delete-post [id]
  (cm/with-mongo conn
                 (cm/destroy! :posts { :_id (cm/object-id id) })))