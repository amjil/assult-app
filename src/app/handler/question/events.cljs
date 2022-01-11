(ns app.handler.question.events
  (:require
   [re-frame.core :as re-frame]
   [ajax.core :as ajax]
   [cljs-bean.core :as bean]
   [app.api :as api]
   [app.db :as db]))



;; -----------------------------------------------------
(re-frame/reg-event-fx
 :user-send-code
 (fn [{:keys [db]} [_ params]]
   {:db    (assoc-in db [:loading :user-send-code] true)
    :http-xhrio {:method                 :post
                 :uri                    (api/endpoint "users" "send-code")
                 :params                 params
                 :format                 (ajax/json-request-format)
                 :response-format        (ajax/json-response-format {:keywords? true})
                 :on-success             [:user-send-code-success]
                 :on-failure             [:api-request-error :user-send-code]}}))

(re-frame/reg-event-fx
 :user-send-code-success
 (fn [{db :db} [_ {body :body}]]
   (let [{profile :profile} body]
     {:db (-> db
              (assoc-in [:loading :user-send-code] false))
      :dispatch [:navigate-to :user-in-code]})))

(comment
  )