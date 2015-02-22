(ns timeware-ui.core
  (:gen-class :main true)
  (:require [clj-http.client :as client])
  (:require [carica.core :refer [configurer resources]])
  (:use seesaw.core seesaw.table)
  (use [clojure.string :only (join split)]))

(defn get-data []
  (get (client/get
    (join "/" [
      (carica.core/config :url) "history" (carica.core/config :username)
     ]) {:accept :json :as :json :headers {"X-Auth-Pass" (carica.core/config :password)}}) :body))

(defn punch-the-clock []
  (get (client/post
    (join "/" [
      (carica.core/config :url) "clock" (carica.core/config :username)
     ]) {:accept :json :as :json :headers {"X-Auth-Pass" (carica.core/config :password)}}) :body))

(defn make-table []
  (table :id :table :model [
    :columns [
        { :key :info :text "Info" }
        { :key :date :text "Date" }
        { :key :schedule :text "Pattern" }
        { :key :bookings :text "Bookings" }
        { :key :absence :text "Absence" }
        { :key :total :text "Total" }
        { :key :opening :text "Opening" }
        { :key :accum :text "Accum." }
        { :key :target :text "Target" }
        { :key :closing :text "Closing" }
      ]
    :rows (get-data)]))

(defn now [] (new java.util.Date))
(defn a-clock [e] [e] (when (punch-the-clock) (alert e (now))))
(defn a-exit [e] (System/exit 0))
(def clock-action (menu-item :text "Clock" :listen [:action a-clock]))
(def exit-action (menu-item :text "Exit" :listen [:action a-exit]))

(defn make-frame []
  (frame :title "Timeware" :on-close :exit :width 600 :height 500
       :menubar (menubar :items
                  [(menu :text "File" :items [clock-action exit-action])])
       :content (border-panel
                  :north (button :text "PUNCH THE CLOCK!"
                    :size [40 :by 40]
                    :mnemonic \N
                    :listen [:action a-clock])
       :center (scrollable (make-table))
       :south  (label :id :sel :text "Selection: "))))

(defn -main [& args]
  (let [f (show! (make-frame))
    t (select f [:#table])]
    (listen t :selection
      (fn [e]
        (config! (select f [:#sel])
          :text (str "Selection: "
            (value-at t (selection t))))))
    f))
