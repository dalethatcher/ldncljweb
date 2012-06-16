(ns ldncljweb.views.login
  (:use [noir.core :only [defpage]]
        [noir.response :only [json]])
  (:require [ldncljweb.models.posts :as posts] [clj-http.client :as client]))

(defn verify-browserid-assertion [assertion]
  (:body
   (client/post "https://browserid.org/verify"
                {:form-params
                 {:assertion assertion
                  :audience "http://localhost:8080"}
                 :throw-exceptions false})))

(defpage "/admin/posts/:id" {id :id}
  (json (posts/find-post id)))

(defpage [:post "/login"] {assertion :assertion}

  (json {:hai  (verify-browserid-assertion assertion)
         :user "bob"}))
