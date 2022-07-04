(ns app.handler.search.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :search-result
 (fn [db _]
   (get db :search)))

(re-frame/reg-sub
 :search-num
 (fn [db _]
   (get db :search-count)))
