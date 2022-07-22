(ns app.util.time
  (:require [cljs-time.core :as time]))

(defn month-date-from-string [m]
  (let [month (js/parseInt (subs m 5 7)) 
        day (js/parseInt (subs m 8 10))]
    (str month "-" day)))

(comment

  (def a "2022-07-20T10:10:10:1000")
  (js/parseInt "08")
  (month-date-from-string a)

  (time/now)
  cljs-time.core
  time
  (prn "Hi")
  )
