(ns app.util.net
  (:require
   [re-frame.core :as re-frame]
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [promesa.core :as p]

   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]]

   ["@react-native-community/netinfo" :default net-info]))

(defn configure []
  (j/call
   net-info
   :configure
   (bean/->js
    {:reachabilityUrl "http://t.amjil.net/api/ping"
     :reachabilityTest
     #(go (= (j/get % :status) 200))
     :reachabilityLongTimeout (* 60 1000)
     :reachabilityShortTimeout (* 5 1000)
     :reachabilityRequestTimeout (* 15 1000)
     :reachabilityShouldRun (fn [] true)
     :shouldFetchWiFiSSID true
     :useNativeReachability false})))

(defn net-info-fetch []
  (-> (j/call net-info :fetch)
      (p/then
       (fn [state]
         (js/console.log "Connection Type" (j/get state :type))
         (js/console.log "is connected? " (j/get state :isConnected))))))

;; copied from status-im source code
;; (defn add-net-info-listener []
;;   (when net-info
;;     (.addEventListener ^js net-info
;;                        #(re-frame/dispatch [::network-info-changed
;;                                             (bean/->clj %)]))))
                    ;;    #(js/console.log "aaaaaa net info >.... " %))))

;; (re-frame/reg-fx
;;  ::listen-to-network-info
;;  (fn []
;;    (add-net-info-listener)))

(comment
  (require '["@react-native-community/netinfo" :default net-info])
  (require '["@react-native-community/netinfo" :as netinfo])
  netinfo
  net-info
  (.then
   (.fetch net-info)
   (fn [state]
     (js/console.log "Connection Type" (j/get state :type))
     (js/console.log "is connected? " (j/get state :isConnected))))
  (configure)
  (net-info-fetch)
  (prn "hi")
  )
