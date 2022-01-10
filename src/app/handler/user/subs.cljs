(ns app.handler.user.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :user-token
 (fn [db _]
   (get-in db [:user :token])))

(re-frame/reg-sub
 :user-mobile
 (fn [db _]
   (get-in db [:user :mobile])))

(re-frame/reg-sub
 :user
 (fn [db _]
   (get db :user)))
