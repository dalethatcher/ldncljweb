(ns ldncljweb.views.login
  (:use [noir.core :only [defpage]]        
        [noir.response :only [json]])
  (:require [ldncljweb.models.posts :as posts]
            [clj-http.client :as client]
            [noir.session :as session]
            [cheshire.core :as json-parsing]))

(defn verify-browserid-assertion [assertion]
  (->
   (client/post "https://browserid.org/verify"
                {:form-params
                 {:assertion assertion
                  :audience "http://localhost:8080"}
                 :throw-exceptions false})
   (:body)
   (json-parsing/parse-string true)))

(defn update-session! [{:keys [status email]}]
  (if (= "okay" status)
    (session/put! :user-info {:email email}))
  (session/get :user-info))

(defn build-login-response [user-info]
  (if user-info
    (json
     {:redirect_to "/"})
    (throw (Exception. "BOOOOM"))))

(defpage "/admin/posts/:id" {id :id}
  (json (posts/find-post id)))

(defpage [:post "/login"] {assertion :assertion}
  (-> assertion
      (verify-browserid-assertion)
      (update-session!)
      (build-login-response)))