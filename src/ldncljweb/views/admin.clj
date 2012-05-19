(ns ldncljweb.views.admin
  (:use [noir.core :only [defpage]]
        [noir.response :only [json]]))

(defpage "/admin/posts/name" []
  (json [:some "bad<script>alert('really?')</script>"]))