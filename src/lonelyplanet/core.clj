(ns lonelyplanet.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as s])
  (:gen-class))

(def cli-options [["-h" "--help" "Show this help message."]
                  ["-t" "--taxonomy FILENAME" "Supply an alternate filename for the taxonomy.xml file."
                   :default "taxonomy.xml" ]
                  ["-d" "--destinations FILENAME" "Supply an alternate filename for the destinations.xml file."
                   :default "destinations.xml"]])

(defn- usage
  "Generate help text for the CLI. Options-summary is expected to be a string (generated by tools.cli/parse-opts)."
  [options-summary]
  (s/join
    \newline
    ["Lonely Planet August 2014 coding test by Oliver Mooney (August 17th 2015)"
     "Usage: lonely [options] input-directory output-directory"
     "The input directory should contain two files, named taxonomy.xml and destinations.xml."
     "These filenames can be overriden using the options below:"
     ""
     options-summary
     ""]))

(defn- error-msg
  "Generate an error message from the error map supplied by tools.cli/parse-opts"
  [errors]
  (str "Invocation errors: \n" (s/join \newline errors)))

(defn- exit
  "Generate exit text on completion/error. Supply standard unix codes for exit status, and any text for msg."
  [status msg]
  (println msg)
  (System/exit status))

(defn -main
  "Parse commandline options & arguments, & respond accordingly. Main entry point."
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]

    ;; check everything we've been told to do is kosher
    (cond (:help options) (exit 0 (usage summary))
          (not= (count arguments) 2) (exit 1 (usage summary))
          errors (exit 1 (error-msg errors)))

    ;; let's get cracking, so
    (exit 0 "Done.")
    ))
