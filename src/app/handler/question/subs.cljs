(ns app.handler.question.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :question-list
 (fn [db _]
   (get db :questions)))
