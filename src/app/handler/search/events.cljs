(ns app.handler.search.events
  (:require
   [re-frame.core :as re-frame]
   [ajax.core :as ajax]
   [cljs-bean.core :as bean]
   [app.api :as api]
   [app.db :as db]))

(re-frame/reg-event-fx
 :search-question-answer
 (fn [{:keys [db]} [_ params cb]]
   (let [ek :search-question-answer]
     {:db    (assoc-in db [:loading ek] true)
      :http-xhrio {:method          :get
                   :timeout         3000
                   :uri             (api/endpoint "search" "question-answers")
                   :headers         (api/auth-header db)
                   :params          params
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:put-search-question-answer-success ek cb]
                   :on-failure      [:api-request-error ek]}})))

(re-frame/reg-event-fx
 :put-search-question-answer-success
 (fn [{db :db} [_ ek cb {{total :total result :result} :data}]]
   (cb)
   (let [{code :code msg :msg} body]
     {:db    (-> db
               (assoc-in [:loading ek] false)
               (assoc :search-count total)
               (assoc :search result))})))

(comment
  (re-frame/dispatch [:search-question-answer "aa"]))
