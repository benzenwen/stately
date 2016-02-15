(ns quantumcalc.ui.calc
  (:require [cljs.pprint :refer [pprint]]
            [reagent.ratom :refer-macros [reaction]]
            [re-frame.core :refer [register-sub subscribe]]
            [re-frame.db :refer [app-db]]
            [nodename.stately.core :refer [dispatch tree active-states]]
            [quantumcalc.ui.styles :refer [styles]]
            [quantumcalc.ui.react-widgets :refer [button text view vspacer]]
            [quantumcalc.ui.colors :refer [blue]]))


(register-sub :display
              (fn [db]
                (reaction (:display @db))))

(register-sub :alert
              (fn [db]
                (reaction (:message @db))))

(register-sub :active
              (fn [db]
                (reaction (:active @db))))


(defn calc-button
  [char]
  [button {:style [(.-btnCenter styles) {:flex 1
                                         :backgroundColor "white"
                                         :borderRadius 10
                                         :width 40
                                         :marginHorizontal 5}]
           :onPress #(dispatch [:keypad/button-pressed char])
           :textStyle [(:txtMedium styles) {:color blue}]}
   (str char)])

(defn four-button-row
  [& chars]
  [view {:style {:flex 1
                 :flexDirection "row"
                 :alignItems "center"}}
   (calc-button (first chars))
   (calc-button (second chars))
   (calc-button (second (rest chars)))
   (calc-button (last chars))])


(defn active-states-labels
  []
  (let [active (subscribe [:active])
        style (:txtMedium styles)
        active-states (remove #(= (name %) "none") @active)
        show-state (fn [fsm] [text {:style style}
                              (str (or (first (filter #(= (namespace %) fsm)
                                                      active-states))
                                       ""))])]
    [view
     (show-state "app")
     (show-state "calc")
     (show-state "operand1")
     (show-state "operand2")
     (show-state "alert")]))

(defn tree-button
  []
  [button {:style [(.-btnCenter styles) {:flex 1
                                         :backgroundColor "white"
                                         :borderRadius 10
                                         :width 50
                                         :marginHorizontal 5}]
           :onPress #(pprint (tree))
           :textStyle [(:txtMedium styles) {:color blue}]}
   "tree"])

(defn db-button
  []
  [button {:style [(.-btnCenter styles) {:flex 1
                                         :backgroundColor "white"
                                         :borderRadius 10
                                         :width 50
                                         :marginHorizontal 5}]
           :onPress #(pprint (dissoc @app-db :tree :active))
           :textStyle [(:txtMedium styles) {:color blue}]}
   "db"])

(defn alert-view
  [alert]
  [view {:style {:flex 1
                 :flexDirection "row"
                 :alignItems "center"}}
   [text {:style {:fontSize 18 :color "red" :borderColor "red"}}
    (or (get-in @alert [:title]) " ")]
   [button {:style [(:btnCenter styles) {:flex 1
                                         :backgroundColor "white"
                                         :borderRadius 5
                                         :width 25
                                         :marginHorizontal 5}]
            :onPress #(dispatch [:alert/off])
            :textStyle [(:txtMedium styles) {:color blue}]}
    "x"]])

(defn calc-screen
  []
  (let [display (subscribe [:display])
        alert (subscribe [:alert])]
    (fn []
      [view {:style [(.-vertLayoutBkg styles) {:flex 1 :backgroundColor blue}]}
       [vspacer 40]

       [text {:style (.-titleH2 styles)}
        "QuantumCalc"]

       [vspacer 20]

       (alert-view alert)

       [vspacer 50]

       [text {:style {:fontSize 24 :color "white"}}
        (or (get-in @display [:value]) " ")]

       [vspacer 20]

       [view
        (calc-button 'C)
        (four-button-row 7 8 9 '+)
        (four-button-row 4 5 6 '-)
        (four-button-row 1 2 3 'x)
        (four-button-row 0 '. '= '/)]

       [vspacer 40]

       [view
        (active-states-labels)
        [vspacer 20]
        (tree-button)
        (db-button)]])))