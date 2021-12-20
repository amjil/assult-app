(ns app.handler.user.events
  (:require
   [re-frame.core :as re-frame]
   [ajax.core :as ajax]
   [app.api :as api]
   [app.db :as db]))

;; -- Login --------------------------------------------------------------------
(re-frame/reg-event-fx
 :login
 (fn [{:keys [db]} [_ credentials]]
   {:db    (assoc-in db [:loading :login] true)
    :http-xhrio {:method                 :post
                 :uri                    (api/endpoint "users" "login")
                 :params                 {:user credentials}
                 :format                 (ajax/json-request-format)
                 :response-format        (ajax/json-response-format {:keywords? true})
                 :on-success             [:login-success]
                 :on-failure             [:api-request-error :login]}}))

(re-frame/reg-event-fx
 :login-success
 (fn [{db :db} [_ {body :body}]]
   (let [{props :user} body
         user (merge (:user db) props)]
     {:db               (assoc-in db [:user :token] user)
      :store-user-in-ls user})))

;; -- Register ----------------------------------------------------------------
(re-frame/reg-event-fx
 :register-user
 (fn [{:keys [db]} [_ registration]]
   {:db    (assoc-in db [:loading :register-user] true)
    :http-xhrio {:method                 :post
                 :uri                    (api/endpoint "users")
                 :params                 {:user registration}
                 :format                 (ajax/json-request-format)
                 :response-format        (ajax/json-response-format {:keywords? true})
                 :on-success             [:register-user-success]
                 :on-failure             [:api-request-error :register-user]}}))

(re-frame/reg-event-fx
 :register-user-success
 (fn [{db :db} [_ {body :body}]]
   (let [{props :user} body
         user (merge (:user db) props)]
     {:db               (assoc db :user user)
      :store-user-in-ls user
      :dispatch-n       [[:navigate-back]]})))

;; -- Profile ------------------------------------------------------------------
(re-frame/reg-event-fx
 :get-user-profile
 (fn [{:keys [db]} [_ params]]
   {:db    (assoc-in db [:loading :profile] true)
    :fetch {:method                 :get
            :uri                    (api/endpoint "profiles" (:profile params))
            :headers                (api/auth-header db)
            :format                 (ajax/json-request-format)
            :response-format        (ajax/json-response-format {:keywords? true})
            :on-success             [:get-user-profile-success]
            :on-failure             [:api-request-error :get-user-profile]}}))

(re-frame/reg-event-fx
 :get-user-profile-success
 (fn [{db :db} [_ {body :body}]]
   (let [{profile :profile} body]
     {:db (-> db
              (assoc-in [:loading :profile] false)
              (assoc :profile profile))})))

;; -- Logout ------------------------------------------------------------------
;;
(re-frame/reg-event-fx                                      ;; usage (dispatch [:logout])
 :logout
 ;; The event handler function removes the user from
 ;; app-state = :db and sets the url to "/".
 (fn [{:keys [db]} _]
   {:db                  db/default-db
    :remove-user-from-ls nil
    :dispatch            [:navigate-to :sign-in]}))
