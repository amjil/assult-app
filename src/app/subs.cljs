(ns app.subs
  (:require
   [re-frame.core :as re-frame]
   app.handler.candidates.subs
   app.handler.keyboard.subs
   app.handler.user.subs
   app.handler.question.subs
   app.handler.answer.subs
   app.handler.search.subs))

(re-frame/reg-sub
 :loading
 (fn [db [_ query]]
   (get-in db [:loading query])))

(re-frame/reg-sub
  :errors
  (fn [db [_ query]]
    (get-in db [:errors query])))
