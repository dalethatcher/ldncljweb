(ns ldncljweb.tools.add-announcement
  (:require [clojure.string :as cs])
  (:require [ldncljweb.models.posts :as posts])
  (:import [org.joda.time DateTime])
  (:gen-class))

(defn listify [v]
  (if (list? v) v [v]))

(defn conj-multiple [headers key-value]
  (if (empty? key-value) 
    headers
    (let [[raw-key value] key-value
          key (keyword raw-key)]
      (conj headers [key
                     (if (contains? headers key)
                       (conj (listify (headers key)) value)
                       value)]))))  

(defn parse-header [raw-header]
  (let [lines (cs/split-lines raw-header)
        key-values (map #(rest (re-find #"([0-9A-Za-z_-]+): (.*)" %)) lines)
        headers (reduce conj-multiple {} key-values)]
    headers))

(defn parse-message [message]
  (let [[raw-header body] (cs/split message #"\n\n" 2)]
    [(parse-header raw-header) body]))

(defn parse-boundary-from-header [header]
  (let [ct (header :Content-Type)
        [_ boundary] (re-matches #".*boundary=(.*)" ct)]
        boundary))

(defn seq-matches? [a b]
  (empty? (drop-while true? (map = a b))))

(defn split-at-boundary 
  ([boundary body] (split-at-boundary (seq (str "--" boundary)) (seq body) [] [] ))
  ([boundary remainder result acc]
    (cond
      (empty? remainder) result
      (seq-matches? boundary remainder) (recur boundary
                                              (drop (count boundary) remainder)
                                              (conj result (apply str acc))
                                              [])
      :else (recur boundary
                   (rest remainder)
                   result
                   (conj acc (first remainder))))))
  
(defn content-type [header]
  (let [raw-content-type (header :Content-Type)]
    (first (cs/split raw-content-type #";"))))

(defn parse-body-into-types [boundary body]
  (let [parts (filter #(not (empty? %)) (split-at-boundary boundary body))
        parsed-parts (map parse-message parts)]
    (reduce #(conj %1 [(content-type (first %2)) (second %2)]) {} parsed-parts)))

(defn parse-multipart-message-into-types [header body]
  (let [boundary (parse-boundary-from-header header)
        body-parts (parse-body-into-types boundary body)]
    [header body-parts]))

(defn parse-singlepart-message-into-types [header body]
  (let [type (content-type header)]
    [header {type body}]))

(defn is-multipart-message? [header]
  (let [type (content-type header)]
    (= type "multipart/alternative")))

(defn parse-message-by-type [message]
  (let [[header body] (parse-message message)]
    (if (is-multipart-message? header)
      (parse-multipart-message-into-types header body)
      (parse-singlepart-message-into-types header body))))

(defn parse-next-escape [r]
  (let [[f s & nr] r
        string-code (str f s)
        int-code (Integer/parseInt string-code 16)]
    [(char int-code) nr]))

(defn parse-quoted-printable-line [line]
  (loop [f (first line)
         r (rest line)
         a []]
    (if (= f \=)
      (if (empty? r)
        (apply str a)
        (let [[nc nr] (parse-next-escape r)]
          (recur (first nr) (rest nr) (conj a nc))))
      (if (empty? r)
        (apply str (concat a [f \newline]))
        (recur (first r) (rest r) (conj a f))))))

(defn parse-quoted-printable [message]
  (apply str (map parse-quoted-printable-line (cs/split-lines message))))

(defn drop-last-lines [message n]
  (apply str (map #(str % \newline) (drop-last n (cs/split-lines message)))))

(defn generate-html-line [line]
  (let [link-converted (cs/replace line #"http[s]{0,1}://[A-Za-z0-9+%.-]*" #(str "<a href=\"" %1 "\">" %1 "</a>"))]
    [link-converted "<br>\n"]))

(defn generate-html [plain-body]
  (let [lines (cs/split-lines plain-body)
        converted-lines (mapcat generate-html-line lines)]
  (apply str converted-lines)))

(defn message-body-to-html [message-by-types]
  (let [html-body (message-by-types "text/html")]
    (println "message-by-types: " message-by-types)
    (if html-body
      (parse-quoted-printable html-body)
      (generate-html (message-by-types "text/plain")))))

(defn process-raw-message [raw-message]
  (let [[header message-by-types] (parse-message-by-type raw-message)
        html-body (message-body-to-html message-by-types)
        de-signatured (drop-last-lines html-body 7)
        subject (cs/replace-first (header :Subject) #".*?ANN:\s*" "")]
    (println "Created post:" (posts/create-post (DateTime.) subject de-signatured))))

(defn -main [& args]
  (let [raw-message (slurp *in*)]
        (process-raw-message raw-message)))