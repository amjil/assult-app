(ns app.text.message
  (:require
    [tongue.core :as tongue]))


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

(def dicts
  {:om-MN
    {:errors
     {:already-has-answer "ᠲᠠ ᠨᠢᠭᠡᠨᠲᠡ ᠬᠠᠷᠢᠭᠤᠯᠤᠭᠰᠠᠨ ᠰᠢᠤ"}
     :example
     {:foo "test meesage"}}
   :zh-CN
    {:errors
     {:already-has-answer "您已回答"}}
   :tongue/fallback :en})

(def translate
  (tongue/build-translate dicts))
;; -----------------------------------------------------------
(defn translate-mn [m]
  (let [mk (if (keyword? m)
             m
             (keyword m))]
    (translate :om-MN mk)))
