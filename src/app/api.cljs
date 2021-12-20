(ns app.api
  (:require [clojure.string :as str]))

(def api-url "https://localhost:3000/api")

(defn endpoint
  "Concat any params to api-url separated by /"
  [& params]
  (str/join "/" (concat [api-url] params)))

(defn auth-header
  "Get user token and format for API authorization"
  [db]
  (when-let [token (get-in db [:user :token])]
    {"Authorization" (str "Token " token)}))
