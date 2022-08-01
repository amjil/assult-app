(ns app.util.net
  (:require
   [re-frame.core :as re-frame]
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   ["@react-native-community/netinfo" :default net-info]))

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
  (prn "hi")
  )
