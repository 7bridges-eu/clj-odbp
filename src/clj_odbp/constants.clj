(ns clj-odbp.constants)

(def ^:const storage-type-plocal "plocal")

(def ^:const record-type-document (byte \d))

(def ^:const request-command-sync-mode (byte \s))
(def ^:const request-command-query "q")

;; version byte + 1 (size:varint of Class)
(def ^:const fixed-header-int 5)

;; type key (1) + position (4) + type value (1)
(def ^:const fixed-oemap-header-int 6)
