(ns app.handler.keyboard.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :keyboard-shift
 (fn [db _]
   (get-in db [:keyboard :shift])))
