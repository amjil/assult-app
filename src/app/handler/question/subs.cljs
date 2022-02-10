(ns app.handler.question.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :question-list
 (fn [db _]
   (get db :questions)))

(re-frame/reg-sub
 :question
 (fn [db _]
   (get db :question)))

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

(re-frame/reg-sub
 :question-my
 (fn [db _]
   (get db :question-my)))
