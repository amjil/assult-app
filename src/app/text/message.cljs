(ns app.text.message
  (:require
    [taoensso.tempura :as tempura :refer [tr]]))


(def labels
  {:question
    {:title-placeholder "ᠠᠰᠠᠭᠤᠯᠲᠠ ᠪᠠᠨ ᠣᠷᠤᠭᠤᠯᠤᠭᠠᠳ ᠠᠰᠠᠭᠤᠯᠲᠠ ᠶᠢᠨ ᠲᠡᠮᠳᠡᠭ ᠶᠢᠡᠷ ᠲᠡᠭᠦᠰᠭᠡᠨ ᠡ"
     :content-placeholder "ᠠᠰᠠᠭᠤᠯᠲᠠ ᠶᠢᠨ ᠲᠠᠯ ᠠ ᠪᠡᠷ ᠨᠡᠮᠡᠯᠲᠡ ᠲᠠᠢᠯᠪᠦᠷᠢ ᠵᠢ ᠡᠨᠳᠡ ᠣᠷᠤᠭᠤᠯᠤᠨ ᠠ᠂ ᠲᠠ ᠬᠠᠷᠢᠭᠤᠯᠲᠠ ᠵᠢ ᠢᠯᠡᠭᠦᠦ ᠬᠤᠷᠳᠤᠨ ᠣᠯᠬᠤ ᠪᠣᠯᠤᠮᠵᠢᠲᠠᠢ(ᠰᠤᠩᠭᠤᠨ ᠲᠠᠭᠯᠠᠬᠤ)"
     :close-similar-titles "ᠠᠳᠠᠯᠢᠪᠲᠤᠷ ᠠᠰᠠᠭᠤᠯᠲᠠ ᠵᠢ ᠬᠠᠭᠠᠬᠤ"
     :vote "ᠵᠦᠪᠰᠢᠶᠡᠷᠡᠭᠰᠡᠨ"
     :all-answer-comments "《ᠪᠦᠬᠦ ᠰᠡᠳᠭᠡᠭᠳᠡᠯ ᠢ ᠦᠵᠡᠬᠦ》"}
   :search {:recent-search "ᠣᠷᠴᠢᠮ ᠤᠨ ᠬᠠᠢᠯᠲᠠ"
            :search-to-find "ᠬᠠᠯᠠᠮᠰᠢᠯ ᠬᠠᠢᠯᠲᠠ"}
   :delete "ᠬᠠᠰᠤᠬᠤ"
   :copy "ᠬᠠᠭᠤᠯᠬᠤ"})

(def tempura-dictionary
  {:om-MN
    {:errors
     {:already-has-answer "ᠲᠠ ᠨᠢᠭᠡᠨᠲᠡ ᠬᠠᠷᠢᠭᠤᠯᠤᠭᠰᠠᠨ ᠰᠢᠤ"}
     :example
     {:foo "test meesage"}}
   :zh-CN
    {:errors
     {:already-has-answer "您已回答"}}})


;; -----------------------------------------------------------
(defn locale-mn-message [m]
  (let [mk (if (keyword? m)
             m
             (keyword m))]
    (tr
      {:dict tempura-dictionary}
      [:om-MN]
      [mk])))

; (tr ; Just a functional call
;   {:dict tempura-dictionary} ; Opts map, see docstring for details
;   [:om-MN :fr] ; Vector of descending-preference locales to search
;   [:example/foo]) ; Vector of descending-preference resource-ids to search
