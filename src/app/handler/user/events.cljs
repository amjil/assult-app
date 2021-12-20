(ns app.handler.user.events
  (:require
   [re-frame.core :as re-frame]
   [ajax.core :as ajax]
   [app.api :as api]))

(re-frame/reg-event-fx                                      ;; usage (dispatch [:login user])
 :login                                                     ;; triggered when a users submits login form
 (fn [{:keys [db]} [_ credentials]]                         ;; credentials = {:email ... :password ...}
   {:db    (assoc-in db [:loading :login] true)
    :http-xhrio {:method                 :post
                 :uri                    (api/endpoint "v1/users" "login") ;; evaluates to "api/users/login"
                 :params                 {:user credentials}     ;; {:user {:email ... :password ...}}
                 :format                 (ajax/json-request-format)              ;; make sure it's json
                 :response-format        (ajax/json-response-format {:keywords? true}) ;; json response and all keys to keywords
                 :on-success             [:login-success]        ;; trigger login-success
                 :on-failure             [:api-request-error :login]}})) ;; trigger api-request-error with :login

(re-frame/reg-event-fx
 :login-success
 (fn [{db :db} [_ {body :body}]]
   (let [{props :user} body
         user (merge (:user db) props)]
     {:db               (assoc-in db [:user :token] user)
      :store-user-in-ls user})))
