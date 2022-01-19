(ns app.handler.question.events
  (:require
   [re-frame.core :as re-frame]
   [ajax.core :as ajax]
   [cljs-bean.core :as bean]
   [app.api :as api]
   [app.db :as db]))



;; -----------------------------------------------------
(re-frame/reg-event-fx
 :get-questions
 (fn [{:keys [db]} [_ params]]
   {:db    (assoc-in db [:loading :get-questions] true)
    :http-xhrio {:method                 :get
                 :uri                    (api/endpoint "questions")
                 :headers                (api/auth-header db)
                 :params                 params
                 :format                 (ajax/json-request-format)
                 :response-format        (ajax/json-response-format {:keywords? true})
                 :on-success             [:get-questions-success]
                 :on-failure             [:api-request-error :get-questions]}}))

(re-frame/reg-event-fx
 :get-questions-success
 (fn [{db :db} [_ body]]
   (let [{data :data} body]
     {:db (-> db
              (assoc :questions data)
              (assoc-in [:loading :get-questions] false))})))

(re-frame/reg-event-fx
 :set-question
 (fn [{db :db} [_ params]]
   {:db             (assoc db :question params)}))

(comment
  (re-frame/dispatch [:get-questions {}])
  (re-frame/subscribe [:question-list]))
