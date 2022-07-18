(ns app.handler.answer.subs
  (:require
   [re-frame.core :as re-frame]))



(re-frame/reg-sub
 :answers
 (fn [db _]
   (get db :answers)))

(re-frame/reg-sub
 :answer
 (fn [db _]
   (get db :answer)))

(re-frame/reg-sub
 :answer-comments
 (fn [db _]
   (get db :answer-comments)))
