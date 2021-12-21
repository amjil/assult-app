(ns app.subs
  (:require
   [re-frame.core :as re-frame]
   app.handler.candidates.subs
   app.handler.user.subs))

(re-frame/reg-sub
 :loading
 (fn [db _]
   (get db :loading)))
